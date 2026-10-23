package at.marcelr.mrtoolbar.mod;

import at.marcelr.mrtoolbar.mod.Protocol.Action;
import at.marcelr.mrtoolbar.mod.Protocol.ActionPayload;
import at.marcelr.mrtoolbar.mod.Protocol.DesignPayload;
import at.marcelr.mrtoolbar.mod.Protocol.HelloPayload;
import at.marcelr.mrtoolbar.mod.Protocol.StatePayload;
import at.marcelr.mrtoolbar.mod.Protocol.WelcomePayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ServerboundPlayChannelEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import at.marcelr.mrtoolbar.mod.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client part of MRtoolbar - the toolbar only works with it. The mod handles keys and looks (key bindings in
 * Options -> Controls -> MRtoolbar, the highlight inside the hotbar, the design screen); everything that moves items
 * stays on the server, the mod only asks.
 * <p>
 * On joining it says hello (protocol + version); only if the server answers OK, keys and highlight are active.
 */
public final class MRtoolbarClient implements ClientModInitializer {

    public static final Logger LOG = LoggerFactory.getLogger("MRtoolbar");
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("mrtoolbar", "main"));

    /** Held: the mouse wheel switches tools on a tool slot instead of changing the slot. */
    public static KeyMapping scrollKey;
    private static KeyMapping openKey;
    private static KeyMapping nextKey;
    private static KeyMapping previousKey;
    private static KeyMapping designKey;
    private static KeyMapping sortKey;

    private static DesignConfig design;
    /** Tool slots from the server, or null (no MRtoolbar on the server, or incompatible). */
    private static volatile StatePayload state;
    /** The server answered the hello with OK. */
    private static volatile boolean compatible;
    private static boolean helloSent;
    /** Ticks since the hello without an answer - it is repeated now and then (the server may have missed it). */
    private static int helloTicks;
    /** Plugin version from the welcome, or null. */
    private static volatile String pluginVersion;
    /** Status of the last welcome, or null if none arrived yet. */
    private static volatile Protocol.Status status;

    @Override
    public void onInitializeClient() {
        design = DesignConfig.load();

        PayloadTypeRegistry.serverboundPlay().register(HelloPayload.TYPE, HelloPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ActionPayload.TYPE, ActionPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(WelcomePayload.TYPE, WelcomePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StatePayload.TYPE, StatePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(DesignPayload.TYPE, DesignPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(WelcomePayload.TYPE, (payload, context) -> welcome(payload));
        ClientPlayNetworking.registerGlobalReceiver(StatePayload.TYPE, (payload, context) -> {
            if (compatible) {
                if (!payload.equals(state)) {
                    LOG.info("Tool slots from the server: active={}, slots={}", payload.active(),
                            Integer.toBinaryString(payload.slots()));
                }
                state = payload;
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(DesignPayload.TYPE,
                (payload, context) -> context.client().gui.setScreen(new DesignScreen(null)));

        // Say hello as soon as the server can receive it (it announces its channels after joining).
        ServerboundPlayChannelEvents.REGISTER.register((handler, sender, client, channels) -> {
            if (channels.contains(HelloPayload.TYPE.id())) {
                sayHello();
            }
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (ClientPlayNetworking.canSend(HelloPayload.TYPE)) {
                sayHello();
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            state = null;
            compatible = false;
            helloSent = false;
            helloTicks = 0;
            pluginVersion = null;
            status = null;
        });

        // Fixed order in Options -> Controls (otherwise Minecraft sorts alphabetically by the translated name).
        // The ids stay the same, so bindings players already changed are kept.
        openKey = key("open", InputConstants.Type.KEYBOARD, InputConstants.KEY_B, 0);
        scrollKey = key("scroll", InputConstants.Type.KEYBOARD, InputConstants.KEY_LALT, 1);
        nextKey = key("next", InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue(), 2);
        previousKey = key("previous", InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue(), 3);
        sortKey = key("sort", InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_MIDDLE, 4);
        designKey = key("design", InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue(), 5);

        // Sorting only works in inventory screens (key bindings do not fire there, so the screen events catch it).
        // In the world the key does nothing - its default, the middle mouse button, is "Pick Block" there.
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!(screen instanceof AbstractContainerScreen<?>)) {
                return;
            }
            ScreenKeyboardEvents.allowKeyPress(screen).register((current, event) -> {
                if (sortKey.matches(event) && !typing(current)) {
                    send(sortTarget((AbstractContainerScreen<?>) current));
                    return false;
                }
                return true;
            });
            ScreenMouseEvents.allowMouseClick(screen).register((current, event) -> {
                // In creative the middle mouse button copies items in inventories - that has to keep working.
                if (sortKey.matchesMouse(event) && (client.player == null || !client.player.hasInfiniteMaterials())) {
                    send(sortTarget((AbstractContainerScreen<?>) current));
                    return false;
                }
                return true;
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (helloSent && status == null && client.getConnection() != null && ++helloTicks % 60 == 0
                    && helloTicks <= 600) {
                LOG.info("No answer from MRtoolbar on the server yet, saying hello again.");
                ClientPlayNetworking.send(hello());
            }
            while (openKey.consumeClick()) {
                send(Action.OPEN);
            }
            while (nextKey.consumeClick()) {
                send(Action.NEXT);
            }
            while (previousKey.consumeClick()) {
                send(Action.PREVIOUS);
            }
            while (designKey.consumeClick()) {
                client.gui.setScreen(new DesignScreen(null));
            }
            // Presses in the world are dropped (see the screen events above).
            while (sortKey.consumeClick()) {
            }
        });
    }

    /**
     * What the sort key sorts: the own inventory if the mouse is over it (or in the player's inventory screen, where
     * the crafting grid is no container), otherwise the open container - the server checks whether it may.
     */
    private static Action sortTarget(AbstractContainerScreen<?> screen) {
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            return Action.SORT;
        }
        Slot slot = ((AbstractContainerScreenAccessor) screen).mrtoolbar$hoveredSlot();
        return slot != null && slot.container instanceof Inventory ? Action.SORT : Action.SORT_CONTAINER;
    }

    /** Whether {@code key} only works in inventory screens (it then never clashes with keys used in the world). */
    public static boolean screenOnly(KeyMapping key) {
        return key != null && key == sortKey;
    }

    /** Whether a text field of the screen has the focus (e.g. the creative search) - then the key is a letter. */
    private static boolean typing(Screen screen) {
        for (GuiEventListener child : screen.children()) {
            if (child instanceof EditBox box && box.isFocused()) {
                return true;
            }
        }
        return false;
    }

    private static KeyMapping key(String name, InputConstants.Type type, int defaultKey, int order) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping("key.mrtoolbar." + name, type, defaultKey,
                CATEGORY, order));
    }

    private static void sayHello() {
        if (helloSent) {
            return;
        }
        helloSent = true;
        helloTicks = 0;
        ClientPlayNetworking.send(hello());
    }

    private static HelloPayload hello() {
        return new HelloPayload(Protocol.VERSION, version("mrtoolbar"), version("minecraft"));
    }

    private static String version(String mod) {
        return FabricLoader.getInstance().getModContainer(mod)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("?");
    }

    private static void welcome(WelcomePayload payload) {
        compatible = payload.status() == Protocol.Status.OK;
        pluginVersion = payload.pluginVersion();
        status = payload.status();
        if (compatible) {
            LOG.info("Connected to MRtoolbar {} on the server (protocol {}-{}).", payload.pluginVersion(),
                    payload.minProtocol(), payload.maxProtocol());
            return;
        }
        state = null;
        LOG.warn("MRtoolbar on the server ({}, protocol {}-{}) does not accept this mod (protocol {}): {}",
                payload.pluginVersion(), payload.minProtocol(), payload.maxProtocol(), Protocol.VERSION,
                payload.status());
        // Too old / too new: the server explains it in the chat with the download link. Only unknown answers here.
        if (payload.status() == Protocol.Status.UNKNOWN) {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                client.player.sendSystemMessage(Component.translatable("mrtoolbar.message.incompatible",
                        payload.pluginVersion()));
            }
        }
    }

    /** Asks the server to run an action (only with a compatible MRtoolbar on the server). */
    public static void send(Action action) {
        if (compatible && ClientPlayNetworking.canSend(ActionPayload.TYPE)) {
            ClientPlayNetworking.send(new ActionPayload(action));
        }
    }

    /** Current tool slot state, or null without (compatible) MRtoolbar on the server. */
    public static StatePayload state() {
        return compatible ? state : null;
    }

    /** Whether the selected hotbar slot is an active tool slot. */
    public static boolean onToolSlot() {
        Minecraft client = Minecraft.getInstance();
        StatePayload current = state();
        return current != null && client.player != null
                && current.isToolSlot(client.player.getInventory().getSelectedSlot());
    }

    /** One line for the design screen: connected or not, and which slots the server reports as tool slots. */
    public static Component statusLine() {
        if (Minecraft.getInstance().getConnection() == null) {
            return Component.translatable("mrtoolbar.status.offline");
        }
        if (status == null) {
            return Component.translatable(helloSent ? "mrtoolbar.status.waiting" : "mrtoolbar.status.no_plugin");
        }
        if (status != Protocol.Status.OK) {
            return Component.translatable("mrtoolbar.status.incompatible", pluginVersion);
        }
        StatePayload current = state;
        StringBuilder slots = new StringBuilder();
        for (int slot = 0; current != null && slot < 9; slot++) {
            if ((current.slots() & (1 << slot)) != 0) {
                slots.append(slots.isEmpty() ? "" : ", ").append(slot + 1);
            }
        }
        if (current == null || !current.active()) {
            return Component.translatable("mrtoolbar.status.paused", pluginVersion);
        }
        return Component.translatable("mrtoolbar.status.connected", pluginVersion,
                slots.isEmpty() ? "-" : slots.toString());
    }

    public static DesignConfig design() {
        return design;
    }

    public static void design(DesignConfig newDesign) {
        design = newDesign;
        design.save();
    }
}
