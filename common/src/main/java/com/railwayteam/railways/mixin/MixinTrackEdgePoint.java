package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.coupling.coupler.SecondaryTrackTargetingBehaviour;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackEdgePoint.class, remap = false)
public abstract class MixinTrackEdgePoint {
    @Shadow public abstract void write(CompoundTag nbt, DimensionPalette dimensions);

    @Shadow public abstract EdgePointType<?> getType();

    @Inject(method = "invalidateAt", at = @At("RETURN"))
    private void invalidateSecondaryEdgePoint(LevelAccessor level, BlockPos tilePos, CallbackInfo ci) {
        TrackTargetingBehaviour<?> behaviour = BlockEntityBehaviour.get(level, tilePos, SecondaryTrackTargetingBehaviour.TYPE);
        if (behaviour == null)
            return;
        CompoundTag migrationData = new CompoundTag();
        DimensionPalette dimensions = new DimensionPalette();
        write(migrationData, dimensions);
        dimensions.write(migrationData);
        behaviour.invalidateEdgePoint(migrationData);
    }

    @Inject(method = "canCoexistWith", at = @At("RETURN"), cancellable = true)
    private void couplerCoExistWithEverything(EdgePointType<?> otherType, boolean front, CallbackInfoReturnable<Boolean> cir) {
        if (getType() == CREdgePointTypes.COUPLER || otherType == CREdgePointTypes.COUPLER)
            cir.setReturnValue(true);
    }
}
