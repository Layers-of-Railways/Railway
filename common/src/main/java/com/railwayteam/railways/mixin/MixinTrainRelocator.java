package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.coupling.TrainUtils;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrainRelocator.class)
public class MixinTrainRelocator {
    @Inject(method = "relocate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;collectInitiallyOccupiedSignalBlocks()V", shift = At.Shift.AFTER))
    private static void tryToApproachStation(Train train, Level level, BlockPos pos, BezierTrackPointLocation bezier,
                                             boolean bezierDirection, Vec3 lookAngle, boolean simulate,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!simulate && !level.isClientSide)
            TrainUtils.tryToParkNearby(train, 1.25);
    }
}
