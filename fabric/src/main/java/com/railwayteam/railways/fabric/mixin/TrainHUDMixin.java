package com.railwayteam.railways.fabric.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.switches.TrainHUDSwitchExtension;
import com.simibubi.create.content.trains.TrainHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrainHUD.class, remap = false)
public class TrainHUDMixin {
    @Inject(method = "renderOverlay", at = @At("HEAD"))
    private static void renderOverlayHook(PoseStack poseStack, float partialTicks, Window window, CallbackInfo ci) {
        TrainHUDSwitchExtension.renderOverlay(poseStack, partialTicks, window.getGuiScaledWidth(), window.getGuiScaledHeight());
    }
}
