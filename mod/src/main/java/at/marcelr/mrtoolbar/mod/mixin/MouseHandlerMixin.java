package at.marcelr.mrtoolbar.mod.mixin;

import at.marcelr.mrtoolbar.mod.MRtoolbarClient;
import at.marcelr.mrtoolbar.mod.Protocol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * While the "hold to scroll" key is down on a tool slot, the mouse wheel switches tools instead of the hotbar slot.
 */
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void mrtoolbar$scrollTools(long window, double xOffset, double yOffset, CallbackInfo info) {
        Minecraft client = Minecraft.getInstance();
        if (client.gui.screen() != null || yOffset == 0 || !MRtoolbarClient.scrollKey.isDown() || !MRtoolbarClient.onToolSlot()) {
            return;
        }
        // Wheel down = towards slot 9 = next tool, like scrolling through the hotbar.
        MRtoolbarClient.send(yOffset < 0 ? Protocol.Action.NEXT : Protocol.Action.PREVIOUS);
        info.cancel();
    }
}
