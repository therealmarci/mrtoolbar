package at.marcelr.mrtoolbar.mod.mixin;

import at.marcelr.mrtoolbar.mod.MRtoolbarClient;
import at.marcelr.mrtoolbar.mod.Protocol;
import at.marcelr.mrtoolbar.mod.ToolSlotRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the tool slot highlight between the hotbar background and the selection frame / items - so it looks like
 * part of the hotbar and Minecraft's selection frame stays on top when scrolling over it.
 */
@Mixin(Hud.class)
public class HudMixin {

    @Inject(method = "extractItemHotbar",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void mrtoolbar$drawToolSlots(GuiGraphicsExtractor graphics, DeltaTracker delta, CallbackInfo info) {
        Protocol.StatePayload state = MRtoolbarClient.state();
        Minecraft client = Minecraft.getInstance();
        if (state == null || !state.active() || client.player == null) {
            return;
        }
        ToolSlotRenderer.draw(graphics, MRtoolbarClient.design(), graphics.guiWidth() / 2 - 91, graphics.guiHeight() - 22,
                state.slots(), client.player.getInventory().getSelectedSlot());
    }
}
