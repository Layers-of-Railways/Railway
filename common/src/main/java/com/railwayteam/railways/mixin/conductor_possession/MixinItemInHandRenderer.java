package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ClientHandler;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {
    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void snr$cancelHandRendering(CallbackInfo ci) {
        if (ClientHandler.isPlayerMountedOnCamera())
            ci.cancel();
    }
}
