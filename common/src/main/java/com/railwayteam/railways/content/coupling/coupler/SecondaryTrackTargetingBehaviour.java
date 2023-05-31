package com.railwayteam.railways.content.coupling.coupler;

import com.railwayteam.railways.mixin.AccessorTrackTargetingBehavior;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.UUID;

public class SecondaryTrackTargetingBehaviour<T extends TrackEdgePoint> extends TrackTargetingBehaviour<T> {

    public static final BehaviourType<SecondaryTrackTargetingBehaviour<?>> TYPE = new BehaviourType<>();

    public SecondaryTrackTargetingBehaviour(SmartBlockEntity te, EdgePointType<T> edgePointType) {
        super(te, edgePointType);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        AccessorTrackTargetingBehavior accessor = (AccessorTrackTargetingBehavior) this;
        nbt.putUUID("SecondaryId", accessor.getId());
        nbt.put("SecondaryTargetTrack", NbtUtils.writeBlockPos(accessor.getTargetTrack()));
        nbt.putBoolean("SecondaryOrtho", accessor.isOrthogonal());
        nbt.putBoolean("SecondaryTargetDirection", accessor.getTargetDirection() == Direction.AxisDirection.POSITIVE);
        if (accessor.getRotatedDirection() != null)
            nbt.put("SecondaryRotatedAxis", VecHelper.writeNBT(accessor.getRotatedDirection()));
        if (accessor.getPrevDirection() != null)
            nbt.put("SecondaryPrevAxis", VecHelper.writeNBT(accessor.getPrevDirection()));
        if (accessor.getMigrationData() != null && !clientPacket)
            nbt.put("SecondaryMigrate", accessor.getMigrationData());
        if (accessor.getTargetBezier() != null) {
            CompoundTag bezierNbt = new CompoundTag();
            bezierNbt.putInt("Segment", accessor.getTargetBezier().segment());
            bezierNbt.put("Key", NbtUtils.writeBlockPos(accessor.getTargetBezier().curveTarget()
                .subtract(getPos())));
            nbt.put("SecondaryBezier", bezierNbt);
        }
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        AccessorTrackTargetingBehavior accessor = (AccessorTrackTargetingBehavior) this;
        accessor.setId(nbt.contains("SecondaryId") ? nbt.getUUID("SecondaryId") : UUID.randomUUID());
        accessor.setTargetTrack(NbtUtils.readBlockPos(nbt.getCompound("SecondaryTargetTrack")));
        accessor.setTargetDirection(nbt.getBoolean("SecondaryTargetDirection") ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
        accessor.setOrthogonal(nbt.getBoolean("SecondaryOrtho"));
        if (nbt.contains("SecondaryPrevAxis"))
            accessor.setPrevDirection(VecHelper.readNBT(nbt.getList("SecondaryPrevAxis", Tag.TAG_DOUBLE)));
        if (nbt.contains("SecondaryRotatedAxis"))
            accessor.setRotatedDirection(VecHelper.readNBT(nbt.getList("SecondaryRotatedAxis", Tag.TAG_DOUBLE)));
        if (nbt.contains("SecondaryMigrate"))
            accessor.setMigrationData(nbt.getCompound("SecondaryMigrate"));
        if (clientPacket)
            accessor.setEdgePoint(null);
        if (nbt.contains("SecondaryBezier")) {
            CompoundTag bezierNbt = nbt.getCompound("SecondaryBezier");
            BlockPos key = NbtUtils.readBlockPos(bezierNbt.getCompound("Key"));
            accessor.setTargetBezier(new BezierTrackPointLocation(bezierNbt.contains("FromStack") ? key : key.offset(getPos()),
                bezierNbt.getInt("Segment")));
        }
    }
}
