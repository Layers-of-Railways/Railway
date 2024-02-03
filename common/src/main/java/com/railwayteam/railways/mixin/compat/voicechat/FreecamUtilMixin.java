package com.railwayteam.railways.mixin.compat.voicechat;

import com.railwayteam.railways.annotation.ConditionalMixin;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.conductor.ClientHandler;
import de.maxhenkel.voicechat.integration.freecam.FreecamUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ConditionalMixin(mods = Mods.VOICECHAT)
@Mixin(FreecamUtil.class)
public class FreecamUtilMixin {
    @Inject(method = "isFreecamEnabled", at = @At("HEAD"), cancellable = true, remap = false)
    private static void conductorIsNotFreecam(CallbackInfoReturnable<Boolean> cir) {
        if (ClientHandler.isPlayerMountedOnCamera())
            cir.setReturnValue(false);
    }
}
