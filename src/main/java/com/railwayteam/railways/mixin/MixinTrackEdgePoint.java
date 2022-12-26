package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.coupling.coupler.SecondaryTrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.TrackEdgePoint;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrackEdgePoint.class, remap = false)
public abstract class MixinTrackEdgePoint {
    @Shadow public abstract void write(CompoundTag nbt, DimensionPalette dimensions);

    @Inject(method = "invalidateAt", at = @At("RETURN"))
    private void invalidateSecondaryEdgePoint(LevelAccessor level, BlockPos tilePos, CallbackInfo ci) {
        TrackTargetingBehaviour<?> behaviour = TileEntityBehaviour.get(level, tilePos, SecondaryTrackTargetingBehaviour.TYPE);
        if (behaviour == null)
            return;
        CompoundTag migrationData = new CompoundTag();
        DimensionPalette dimensions = new DimensionPalette();
        write(migrationData, dimensions);
        dimensions.write(migrationData);
        behaviour.invalidateEdgePoint(migrationData);
    }
}
