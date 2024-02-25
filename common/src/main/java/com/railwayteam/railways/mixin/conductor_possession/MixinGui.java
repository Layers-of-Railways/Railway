package com.railwayteam.railways.mixin.conductor_possession;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.conductor.ClientHandler;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui {
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void railways$cancelExperienceBar(PoseStack poseStack, int xPos, CallbackInfo ci) {
        if (ClientHandler.isPlayerMountedOnCamera())
            ci.cancel();
    }
}
