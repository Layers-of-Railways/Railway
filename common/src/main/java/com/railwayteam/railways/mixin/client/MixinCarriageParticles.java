package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarriageParticles.class)
public class MixinCarriageParticles {
    @Unique private boolean snr$isHandcar;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void snr$checkIfHandcar(CarriageContraptionEntity entity, CallbackInfo ci) {
        snr$isHandcar = entity.getCarriage().bogeys.both(b -> b == null || b.getStyle() == CRBogeyStyles.HANDCAR);
    }

    @WrapWithCondition(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
    ))
    private boolean snr$skipParticlesForHandcar(Level instance, ParticleOptions particleData, double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed, @Local(ordinal = 1) boolean spark) {
        return spark || !snr$isHandcar;
    }
}
