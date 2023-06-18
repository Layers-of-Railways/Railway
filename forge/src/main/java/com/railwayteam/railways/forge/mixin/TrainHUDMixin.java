package com.railwayteam.railways.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.switches.TrainHUDSwitchExtension;
import com.simibubi.create.content.trains.TrainHUD;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrainHUD.class, remap = false)
public class TrainHUDMixin {
    @Inject(method = "renderOverlay", at = @At("HEAD"))
    private static void renderOverlayHook(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int width,
                                   int height, CallbackInfo ci) {
        TrainHUDSwitchExtension.renderOverlay(poseStack, partialTicks, width, height);
    }
}
