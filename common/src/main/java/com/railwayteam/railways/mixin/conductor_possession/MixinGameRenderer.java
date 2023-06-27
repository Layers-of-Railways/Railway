package com.railwayteam.railways.mixin.conductor_possession;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.Config;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void loadEffect(ResourceLocation resourceLocation);

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void snr$bobView(PoseStack matrixStack, float partialTicks, CallbackInfo ci) {
        if (!(minecraft.getCameraEntity() instanceof ConductorEntity conductor)) {
            return;
        }
        float f = conductor.walkDist - conductor.walkDistO;
        float g = -(conductor.walkDist + f * partialTicks);
        float h = Mth.lerp(partialTicks, conductor.oBob, conductor.bob);
        matrixStack.translate(Mth.sin(g * (float)Math.PI) * h * 0.5f, -Math.abs(Mth.cos(g * (float)Math.PI) * h), 0.0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(g * (float)Math.PI) * h * 3.0f));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(Math.abs(Mth.cos(g * (float)Math.PI - 0.2f) * h) * 5.0f));
        ci.cancel();
    }

    @Inject(method = "checkEntityPostEffect", at = @At("RETURN"))
    private void snr$checkEntityPostEffect(Entity entity, CallbackInfo ci) {
        if (entity instanceof ConductorEntity && Config.CONDUCTOR_SPY_SHADER.get()) {
            loadEffect(new ResourceLocation("shaders/post/scan_pincushion.json"));
        }
    }
}
