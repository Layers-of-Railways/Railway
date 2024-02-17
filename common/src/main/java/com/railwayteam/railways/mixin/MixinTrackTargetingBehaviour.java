package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IPreAssembleCallback;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrackTargetingBehaviour.class)
public abstract class MixinTrackTargetingBehaviour<T extends TrackEdgePoint> extends BlockEntityBehaviour implements IPreAssembleCallback {
    @Shadow private T edgePoint;

    private MixinTrackTargetingBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Shadow public abstract Direction.AxisDirection getTargetDirection();

    @Override
    public void railways$preAssemble() {
        if (edgePoint != null && !getWorld().isClientSide)
            edgePoint.blockEntityRemoved(getPos(), getTargetDirection() == Direction.AxisDirection.POSITIVE);
    }
}
