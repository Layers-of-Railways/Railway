package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.util.DebugRendererExtensions;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
    @Inject(method = "render", at = @At("RETURN"))
    private void renderExtra(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ, CallbackInfo ci) {
        DebugRendererExtensions.render(poseStack, bufferSource, camX, camY, camZ);
    }
}
