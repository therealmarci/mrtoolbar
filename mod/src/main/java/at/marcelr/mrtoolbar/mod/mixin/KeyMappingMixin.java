package at.marcelr.mrtoolbar.mod.mixin;

import at.marcelr.mrtoolbar.mod.MRtoolbarClient;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * "Sort inventory" only works in inventory screens, so it never clashes with keys used while playing - e.g. its
 * default, the middle mouse button, is also "Pick Block". Without this the controls list would show both in red.
 */
@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Inject(method = "same", at = @At("HEAD"), cancellable = true)
    private void mrtoolbar$noConflictWithScreenKeys(KeyMapping other, CallbackInfoReturnable<Boolean> info) {
        if (MRtoolbarClient.screenOnly((KeyMapping) (Object) this) != MRtoolbarClient.screenOnly(other)) {
            info.setReturnValue(false);
        }
    }
}
