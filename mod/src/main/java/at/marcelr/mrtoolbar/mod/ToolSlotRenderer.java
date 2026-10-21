package at.marcelr.mrtoolbar.mod;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Draws the tool slot highlight into a hotbar. Called right after Minecraft drew the hotbar background and before
 * the selection frame and the items, so the highlight sits inside the hotbar instead of on top of it.
 */
public final class ToolSlotRenderer {

    /** Replaceable by texture packs: assets/mrtoolbar/textures/gui/sprites/hud/tool_slot.png */
    public static final Identifier SPRITE = Identifier.fromNamespaceAndPath("mrtoolbar", "hud/tool_slot");
    /**
     * White sprite for plain rectangles. Minecraft sorts GUI elements of one layer by pipeline and texture (with a
     * random seed) - a {@code fill} could end up below the hotbar texture. Drawn as a sprite of the same GUI atlas,
     * the highlight keeps its place between hotbar background and selection frame.
     */
    private static final Identifier PIXEL = Identifier.fromNamespaceAndPath("mrtoolbar", "hud/pixel");

    private ToolSlotRenderer() {
    }

    /**
     * @param left     left edge of the hotbar texture (182 px wide)
     * @param top      top edge of the hotbar texture (22 px high)
     * @param slots    bit i = hotbar slot i is a tool slot
     * @param selected the selected hotbar slot
     */
    public static void draw(GuiGraphicsExtractor graphics, DesignConfig design, int left, int top, int slots, int selected) {
        if (!design.enabled || slots == 0) {
            return;
        }
        int color = design.argb();
        for (int slot = 0; slot < 9; slot++) {
            if ((slots & (1 << slot)) == 0 || (slot == selected && design.selected == DesignConfig.Selected.HIDE)) {
                continue;
            }
            // A slot cell is 20x20 px and starts 1 px inside the hotbar texture.
            int x = left + 1 + slot * 20 + design.inset;
            int y = top + 1 + design.inset;
            int size = 20 - 2 * design.inset;
            drawSlot(graphics, design, x, y, size, color);
        }
    }

    private static void rect(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
        if (x2 > x1 && y2 > y1) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PIXEL, x1, y1, x2 - x1, y2 - y1, color);
        }
    }

    private static void drawSlot(GuiGraphicsExtractor graphics, DesignConfig design, int x, int y, int size, int color) {
        int t = design.thickness;
        int right = x + size;
        int bottom = y + size;
        switch (design.style) {
            case FRAME -> {
                // A one pixel frame without its corner pixels looks rounded like the vanilla slots.
                int cut = t == 1 ? 1 : 0;
                rect(graphics, x + cut, y, right - cut, y + t, color);
                rect(graphics, x + cut, bottom - t, right - cut, bottom, color);
                rect(graphics, x, y + t, x + t, bottom - t, color);
                rect(graphics, right - t, y + t, right, bottom - t, color);
            }
            case CORNERS -> {
                int length = Math.max(3, size / 4);
                rect(graphics, x, y, x + length, y + t, color);
                rect(graphics, x, y + t, x + t, y + length, color);
                rect(graphics, right - length, y, right, y + t, color);
                rect(graphics, right - t, y + t, right, y + length, color);
                rect(graphics, x, bottom - t, x + length, bottom, color);
                rect(graphics, x, bottom - length, x + t, bottom - t, color);
                rect(graphics, right - length, bottom - t, right, bottom, color);
                rect(graphics, right - t, bottom - length, right, bottom - t, color);
            }
            case UNDERLINE -> rect(graphics, x + 2, bottom - t, right - 2, bottom, color);
            case FILL -> rect(graphics, x, y, right, bottom, color);
            case TEXTURE -> graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, x, y, size, size, color);
        }
    }
}
