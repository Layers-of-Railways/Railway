package com.railwayteam.railways.mixin.conductor_possession;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.conductor.ClientHandler;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void loadEffect(ResourceLocation resourceLocation);

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void railways$bobView(PoseStack matrixStack, float partialTicks, CallbackInfo ci) {
        if (!(minecraft.getCameraEntity() instanceof ConductorEntity conductor)) {
            return;
        }
        float f = conductor.walkDist - conductor.walkDistO;
        float g = -(conductor.walkDist + f * partialTicks);
        float h = Mth.lerp(partialTicks, conductor.oBob, conductor.bob);
        matrixStack.translate(Mth.sin(g * (float)Math.PI) * h * 0.5f, -Math.abs(Mth.cos(g * (float)Math.PI) * h), 0.0);

        matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(g * (float)Math.PI) * h * 3.0f));
        matrixStack.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(g * (float)Math.PI - 0.2f) * h) * 5.0f));
        ci.cancel();
    }

    @Inject(method = "checkEntityPostEffect", at = @At("RETURN"))
    private void railways$checkEntityPostEffect(Entity entity, CallbackInfo ci) {
        if (entity instanceof ConductorEntity && CRConfigs.client().useConductorSpyShader.get()) {
            loadEffect(new ResourceLocation("shaders/post/scan_pincushion.json"));
        }
    }

    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void railways$shouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        if (ClientHandler.isPlayerMountedOnCamera()) {
            boolean flag = !minecraft.options.hideGui;
            HitResult hitresult = this.minecraft.hitResult;
            if (hitresult != null && hitresult.getType() == HitResult.Type.BLOCK && minecraft.level != null
                    && hitresult instanceof BlockHitResult blockHitResult) {
                flag &= ConductorEntity.canSpyInteract(minecraft.level.getBlockState(blockHitResult.getBlockPos()));
            } else {
                flag = false;
            }
            cir.setReturnValue(flag);
        }
    }
}
