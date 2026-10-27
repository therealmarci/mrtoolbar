package at.marcelr.mrtoolbar;

import at.marcelr.mrtoolbar.controls.ToolAction;
import at.marcelr.mrtoolbar.util.Wire;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * Connection to the MRtoolbar Fabric mod - without it the toolbar cannot be used. See docs/COMPATIBILITY.md.
 * <p>
 * On joining, the mod sends {@code hello} (protocol, mod version); the plugin checks it ({@link Compatibility}) and
 * answers {@code welcome}. Only after an OK the toolbar works for the player: the plugin then sends {@code state}
 * (which hotbar slots are tool slots) and runs the mod's {@code action}s. The mod only asks - moving items, checking
 * and saving stays on the server.
 * <p>
 * The formats of {@code hello} and {@code welcome} never change (fields may only be added at the end), so that every
 * mod version can tell every plugin version whether they fit.
 */
public final class ModBridge implements Listener, PluginMessageListener {

    public static final String HELLO = "mrtoolbar:hello";
    public static final String WELCOME = "mrtoolbar:welcome";
    public static final String STATE = "mrtoolbar:state";
    public static final String ACTION = "mrtoolbar:action";
    public static final String DESIGN = "mrtoolbar:design";

    /** What the handshake of a player gave. */
    public record Client(int protocol, String modVersion, Compatibility.Status status) {
    }

    private final MRToolbar plugin;
    private final Map<UUID, Client> clients = new HashMap<>();
    /** Last state sent per player, to send only changes. */
    private final Map<UUID, byte[]> sent = new HashMap<>();
    /** When the state was last sent per player (ms) - it is sent again now and then, see {@link #RESEND_MILLIS}. */
    private final Map<UUID, Long> sentAt = new HashMap<>();
    /**
     * Messages for channels the client has not announced yet, per player in the order they were sent (per channel the
     * newest wins). Bukkit drops plugin messages to unannounced channels silently - and the mod's hello can arrive
     * before its announcement. The order matters: a state that overtakes the welcome is thrown away by the mod.
     */
    private final Map<UUID, LinkedHashMap<String, byte[]>> waiting = new HashMap<>();
    /**
     * The state goes out again after this long even without a change, so a mod that missed it (or lost it some other
     * way) shows the tool slots again by itself instead of only after switching the toolbar off and on.
     */
    private static final long RESEND_MILLIS = 10_000;

    public ModBridge(MRToolbar plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, HELLO, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, ACTION, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, WELCOME);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, STATE);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, DESIGN);
    }

    public void stop() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);
    }

    /** Whether the player plays with a compatible MRtoolbar mod (the toolbar works only then). */
    public boolean compatible(Player player) {
        Client client = clients.get(player.getUniqueId());
        return client != null && client.status() == Compatibility.Status.OK;
    }

    /** Whether the player has a mod that is too old (or from before the handshake) - for a better hint than "no mod". */
    public boolean outdated(Player player) {
        Client client = clients.get(player.getUniqueId());
        return client != null ? client.status() == Compatibility.Status.MOD_TOO_OLD : preHandshakeMod(player);
    }

    /** The handshake result of the player, or null if the mod did not say hello (yet). */
    public Client client(Player player) {
        return clients.get(player.getUniqueId());
    }

    /** Sends the player's tool slot state to the mod if it changed. */
    public void update(Player player) {
        if (!compatible(player)) {
            return;
        }
        ToolbarManager toolbars = plugin.toolbars();
        int mask = 0;
        for (ToolSlot slot : toolbars.get(player).slots()) {
            mask |= 1 << slot.hotbar();
        }
        byte[] state = new Wire.Writer().bool(toolbars.active(player)).shortValue(mask).bytes();
        UUID id = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (!Arrays.equals(state, sent.get(id)) || now - sentAt.getOrDefault(id, 0L) >= RESEND_MILLIS) {
            sent.put(id, state);
            sentAt.put(id, now);
            send(player, STATE, state);
        }
    }

    /**
     * Sends a message now, or as soon as the client announced the channel. While something is waiting, later messages
     * wait behind it, so they never overtake each other (e.g. the state the welcome).
     */
    private void send(Player player, String channel, byte[] message) {
        LinkedHashMap<String, byte[]> queue = waiting.get(player.getUniqueId());
        if ((queue == null || queue.isEmpty()) && player.getListeningPluginChannels().contains(channel)) {
            player.sendPluginMessage(plugin, channel, message);
            return;
        }
        queue = waiting.computeIfAbsent(player.getUniqueId(), id -> new LinkedHashMap<>());
        // Newest wins, but at the end of the queue - it was sent after everything else in it.
        queue.remove(channel);
        queue.put(channel, message);
    }

    /** Sends the waiting messages in order, up to the first one whose channel the client has not announced yet. */
    private void flush(Player player) {
        LinkedHashMap<String, byte[]> queue = waiting.get(player.getUniqueId());
        if (queue == null) {
            return;
        }
        var it = queue.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, byte[]> next = it.next();
            if (!player.getListeningPluginChannels().contains(next.getKey())) {
                return;
            }
            player.sendPluginMessage(plugin, next.getKey(), next.getValue());
            it.remove();
        }
        waiting.remove(player.getUniqueId());
    }

    /** Opens the hotbar design screen of the mod. @return false without a compatible mod */
    public boolean openDesign(Player player) {
        if (!compatible(player)) {
            return false;
        }
        send(player, DESIGN, new byte[0]);
        return true;
    }

    // ------------------------------------------------------------------ events

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        long delay = plugin.settings().mod().handshakeSeconds() * 20L;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && !clients.containsKey(player.getUniqueId())) {
                withoutMod(player, preHandshakeMod(player));
            }
        }, delay);
    }

    @EventHandler
    public void onRegister(PlayerRegisterChannelEvent event) {
        flush(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clients.remove(event.getPlayer().getUniqueId());
        sent.remove(event.getPlayer().getUniqueId());
        sentAt.remove(event.getPlayer().getUniqueId());
        waiting.remove(event.getPlayer().getUniqueId());
    }

    /**
     * A mod from before the handshake (test versions with protocol 1): it listens on mrtoolbar channels but never says
     * hello. It is too old - not missing.
     */
    private static boolean preHandshakeMod(Player player) {
        return player.getListeningPluginChannels().stream().anyMatch(channel -> channel.startsWith("mrtoolbar:"));
    }

    /**
     * No (compatible) mod: the selected tools go back into the belts, the hotbar is a normal one.
     *
     * @param outdated whether an old mod without handshake was found (says "too old" instead of "no mod")
     */
    private void withoutMod(Player player, boolean outdated) {
        plugin.messages().run(player, () -> withoutModNow(player, outdated));
    }

    private void withoutModNow(Player player, boolean outdated) {
        plugin.toolbars().suspend(player);
        if (!player.hasPermission(ToolbarManager.PERM_USE)) {
            return;
        }
        if (outdated) {
            plugin.getLogger().info(player.getName() + " uses an MRtoolbar mod from before the handshake (too old).");
            plugin.messages().send(player, "mod-too-old", tag("version", "?"), tag("download", downloadLink()));
        } else if (plugin.toolbars().get(player).slotCount() > 0) {
            plugin.messages().send(player, "mod-missing-notice", tag("download", downloadLink()));
        }
    }

    public Component downloadLink() {
        return plugin.messages().get("mod-download-label")
                .clickEvent(ClickEvent.openUrl(plugin.settings().mod().downloadUrl()));
    }

    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] message) {
        plugin.messages().run(player, () -> receive(channel, player, message));
    }

    private void receive(String channel, Player player, byte[] message) {
        try {
            switch (channel) {
                case HELLO -> hello(player, new Wire.Reader(message));
                case ACTION -> action(player, new Wire.Reader(message));
                default -> {
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Invalid " + channel + " message from " + player.getName() + ": " + e.getMessage());
        }
    }

    /** hello: VarInt protocol, String mod version, String Minecraft version (frozen format). */
    private void hello(Player player, Wire.Reader in) throws IOException {
        int protocol = in.varInt();
        String modVersion = in.string();
        Settings.ModSettings settings = plugin.settings().mod();
        Compatibility.Status status = Compatibility.check(protocol, modVersion, settings.minimumVersion());
        clients.put(player.getUniqueId(), new Client(protocol, modVersion, status));
        sent.remove(player.getUniqueId());

        // welcome: VarInt status, VarInt min protocol, VarInt max protocol, String plugin version,
        // String minimum mod version, String download URL (frozen format).
        send(player, WELCOME, new Wire.Writer()
                .varInt(status.code())
                .varInt(Compatibility.MIN_PROTOCOL)
                .varInt(Compatibility.MAX_PROTOCOL)
                .string(plugin.updateChecker().currentVersion())
                .string(settings.minimumVersion())
                .string(settings.downloadUrl())
                .bytes());

        if (status == Compatibility.Status.OK) {
            plugin.getLogger().info(player.getName() + " uses the MRtoolbar mod " + modVersion + " (protocol " + protocol + ").");
            plugin.toolbars().guard(player);
            update(player);
        } else {
            plugin.getLogger().info(player.getName() + " uses an incompatible MRtoolbar mod " + modVersion
                    + " (protocol " + protocol + "): " + status.name().toLowerCase(Locale.ROOT));
            withoutMod(player, false);
            plugin.messages().send(player, status == Compatibility.Status.MOD_TOO_OLD ? "mod-too-old" : "mod-too-new",
                    tag("version", modVersion), tag("download", downloadLink()));
        }
    }

    /** action: byte action (0 open, 1 next, 2 previous, 3 sort, 4 sort container). Unknown codes are ignored. */
    private void action(Player player, Wire.Reader in) throws IOException {
        int code = in.unsignedByte();
        ToolAction[] actions = ToolAction.values();
        if (!compatible(player) || code >= actions.length) {
            return;
        }
        switch (actions[code]) {
            case SORT -> plugin.sorter().sortByKey(player);
            case SORT_CONTAINER -> plugin.sorter().sortContainerByKey(player);
            default -> plugin.toolbars().perform(player, actions[code]);
        }
    }
}
