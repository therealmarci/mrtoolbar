package at.marcelr.mrtoolbar.mod;

import at.marcelr.mrtoolbar.mod.DesignConfig.Selected;
import at.marcelr.mrtoolbar.mod.DesignConfig.Style;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;
import java.util.function.IntConsumer;

/**
 * Hotbar design: how tool slots are highlighted. The preview at the top uses the hotbar textures of the active
 * resource packs, so the highlight can be matched to the texture pack. Saved when the screen closes.
 */
public final class DesignScreen extends Screen {

    private static final Identifier HOTBAR = Identifier.withDefaultNamespace("hud/hotbar");
    private static final Identifier SELECTION = Identifier.withDefaultNamespace("hud/hotbar_selection");
    private static final int[] PRESETS = {0xFFAA00, 0xFFFFFF, 0xFFFF55, 0xFF5555, 0x55FF55, 0x55FFFF, 0x5555FF, 0xFF55FF};
    /** Preview: slot 1 and 3 are tool slots, slot 3 is selected. */
    private static final int PREVIEW_SLOTS = 0b101;
    private static final int PREVIEW_SELECTED = 2;
    private static final ItemStack[] PREVIEW_ITEMS = {
            new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.TORCH, 32), new ItemStack(Items.NETHERITE_SWORD),
            ItemStack.EMPTY, new ItemStack(Items.BREAD, 12), ItemStack.EMPTY, new ItemStack(Items.COBBLESTONE, 64),
            ItemStack.EMPTY, new ItemStack(Items.WATER_BUCKET)};

    private final Screen parent;
    private final DesignConfig draft;
    private EditBox hex;

    public DesignScreen(Screen parent) {
        super(Component.translatable("mrtoolbar.design.title"));
        this.parent = parent;
        this.draft = MRtoolbarClient.design().copy();
    }

    @Override
    protected void init() {
        int column = 150;
        int gap = 10;
        int left = width / 2 - column - gap / 2;
        int right = width / 2 + gap / 2;
        int y = 80;

        addRenderableWidget(CycleButton.onOffBuilder(draft.enabled)
                .create(left, y, column, 20, Component.translatable("mrtoolbar.design.enabled"),
                        (button, value) -> draft.enabled = value));
        addRenderableWidget(CycleButton.builder((Style style) -> Component.translatable(
                        "mrtoolbar.design.style." + style.name().toLowerCase(Locale.ROOT)), draft.style)
                .withValues(Style.values())
                .create(right, y, column, 20, Component.translatable("mrtoolbar.design.style"),
                        (button, value) -> draft.style = value));

        y += 24;
        hex = new EditBox(font, left, y, column, 20, Component.translatable("mrtoolbar.design.color"));
        hex.setMaxLength(7);
        hex.setValue(hexOf(draft.color));
        hex.setHint(Component.translatable("mrtoolbar.design.color.hint"));
        hex.setResponder(text -> {
            Integer parsed = parseHex(text);
            hex.setTextColor(parsed == null ? 0xFFFF5555 : 0xFFE0E0E0);
            if (parsed != null) {
                draft.color = parsed;
            }
        });
        addRenderableWidget(hex);
        addRenderableWidget(new IntSlider(right, y, column, "mrtoolbar.design.opacity", "%", 10, 100, draft.opacity,
                value -> draft.opacity = value));

        y += 24;
        int presetWidth = (column - (PRESETS.length - 1) * 2) / PRESETS.length;
        for (int i = 0; i < PRESETS.length; i++) {
            int preset = PRESETS[i];
            addRenderableWidget(Button.builder(Component.literal("■").withColor(preset), button -> hex.setValue(hexOf(preset)))
                    .bounds(left + i * (presetWidth + 2), y, presetWidth, 20).build());
        }
        addRenderableWidget(CycleButton.builder((Selected mode) -> Component.translatable(
                        "mrtoolbar.design.selected." + mode.name().toLowerCase(Locale.ROOT)), draft.selected)
                .withValues(Selected.values())
                .create(right, y, column, 20, Component.translatable("mrtoolbar.design.selected"),
                        (button, value) -> draft.selected = value));

        y += 24;
        addRenderableWidget(new IntSlider(left, y, column, "mrtoolbar.design.thickness", " px", 1, 3, draft.thickness,
                value -> draft.thickness = value));
        addRenderableWidget(new IntSlider(right, y, column, "mrtoolbar.design.inset", " px", 0, 4, draft.inset,
                value -> draft.inset = value));

        y += 34;
        addRenderableWidget(Button.builder(Component.translatable("mrtoolbar.design.reset"), button -> {
            MRtoolbarClient.design(new DesignConfig());
            minecraft.gui.setScreen(new DesignScreen(parent));
        }).bounds(left, y, column, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(right, y, column, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, 15, 0xFFFFFFFF);
        graphics.centeredText(font, Component.translatable("mrtoolbar.design.hint"), width / 2, 60, 0xFFA0A0A0);
        graphics.centeredText(font, MRtoolbarClient.statusLine(), width / 2, 70, 0xFFFFFF80);

        // Preview in the same order as the real hotbar: background, highlight, selection frame, items.
        int left = width / 2 - 91;
        int top = 34;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR, left, top, 182, 22);
        ToolSlotRenderer.draw(graphics, draft, left, top, PREVIEW_SLOTS, PREVIEW_SELECTED);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTION, left - 1 + PREVIEW_SELECTED * 20, top - 1, 24, 23);
        for (int slot = 0; slot < 9; slot++) {
            if (!PREVIEW_ITEMS[slot].isEmpty()) {
                graphics.fakeItem(PREVIEW_ITEMS[slot], left + 3 + slot * 20, top + 3);
            }
        }
    }

    @Override
    public void onClose() {
        MRtoolbarClient.design(draft);
        minecraft.gui.setScreen(parent);
    }

    private static String hexOf(int color) {
        return String.format("#%06X", color & 0xFFFFFF);
    }

    private static Integer parseHex(String text) {
        String value = text.trim().startsWith("#") ? text.trim().substring(1) : text.trim();
        if (!value.matches("[0-9a-fA-F]{6}")) {
            return null;
        }
        return Integer.parseInt(value, 16);
    }

    /** Slider for whole numbers from {@code min} to {@code max}. */
    private static final class IntSlider extends AbstractSliderButton {

        private final String key;
        private final String unit;
        private final int min;
        private final int max;
        private final IntConsumer onChange;

        IntSlider(int x, int y, int width, String key, String unit, int min, int max, int value, IntConsumer onChange) {
            super(x, y, width, 20, Component.empty(), (double) (value - min) / (max - min));
            this.key = key;
            this.unit = unit;
            this.min = min;
            this.max = max;
            this.onChange = onChange;
            updateMessage();
        }

        private int intValue() {
            return min + (int) Math.round(value * (max - min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable(key).append(": " + intValue() + unit));
        }

        @Override
        protected void applyValue() {
            onChange.accept(intValue());
        }
    }
}
