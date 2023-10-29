package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageSounds;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CarriageSounds.class, remap = false)
public class MixinCarriageSounds {
    @Unique
    private boolean snr$skip;

    @Unique
    private boolean snr$skipSteam;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void skipIfInvisible(CarriageContraptionEntity entity, CallbackInfo ci) {
        snr$skip = entity.getCarriage().bogeys.both((b) -> b == null || b.getStyle() == CRBogeyStyles.INVISIBLE || b.getStyle() == CRBogeyStyles.INVISIBLE_MONOBOGEY);
        snr$skipSteam = entity.getCarriage().bogeys.both((b) -> b == null || b.getStyle() == CRBogeyStyles.HANDCAR);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void actuallySkip(Carriage.DimensionalCarriageEntity dce, CallbackInfo ci) {
        if (snr$skip)
            ci.cancel();
    }

    @SuppressWarnings("unused")
    @WrapWithCondition(method = "tick", at = {
        @At(value = "INVOKE", target = "Lcom/simibubi/create/AllSoundEvents$SoundEntry;playAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;FFZ)V", ordinal = 0),
        @At(value = "INVOKE", target = "Lcom/simibubi/create/AllSoundEvents$SoundEntry;playAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;FFZ)V", ordinal = 1)
    })
    private boolean shouldPlaySteamSound(AllSoundEvents.SoundEntry instance, Level world, Vec3 pos, float volume, float pitch, boolean fade) {
        return instance != AllSoundEvents.STEAM || !snr$skipSteam;
    }
}
