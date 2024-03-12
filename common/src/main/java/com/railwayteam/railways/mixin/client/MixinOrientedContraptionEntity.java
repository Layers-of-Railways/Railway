package com.railwayteam.railways.mixin.client;

import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OrientedContraptionEntity.class)
public class MixinOrientedContraptionEntity {
    @Unique
    private float railways$lastRot;

    @Inject(method = "applyRotation", at = @At("RETURN"))
    public void railways$followTrainRotationWhenRiding(Vec3 localPos, float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        OrientedContraptionEntity contraptionEntity = (OrientedContraptionEntity) (Object) this;
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.getVehicle() != null && player.getVehicle().is(contraptionEntity)) {
            float viewYRot = contraptionEntity.getViewYRot(partialTicks);
            float diff = (railways$lastRot - viewYRot + 180) % 180;
            player.setYRot((player.getYRot() + diff) % 360);
            railways$lastRot = viewYRot;
        }
    }
}
