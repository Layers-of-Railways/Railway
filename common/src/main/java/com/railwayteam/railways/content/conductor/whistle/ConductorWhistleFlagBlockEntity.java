package com.railwayteam.railways.content.conductor.whistle;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ConductorWhistleFlagBlockEntity extends SmartBlockEntity implements ITransformableBlockEntity {

    public TrackTargetingBehaviour<GlobalStation> station;
    private boolean tickedOnce = false;
    private DyeColor color = DyeColor.RED;

    public ConductorWhistleFlagBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(100);
    }

    protected String targetStationName() {
        return ConductorWhistleItem.SPECIAL_MARKER + this.getBlockPos().toShortString();
    }

    DyeColor getColor() {
        return color;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level.isClientSide)
            return;

        if (station.getEdgePoint() == null)
            station.tick();
        if (station.getEdgePoint() != null)
            station.getEdgePoint().name = targetStationName();

        if (tickedOnce) {
            boolean found = false;
            for (Train train : Create.RAILWAYS.trains.values()) {
                Schedule schedule = train.runtime == null ? null : train.runtime.getSchedule();
                if (schedule != null && schedule.entries.size() == 1 && schedule.entries.get(0).instruction instanceof DestinationInstruction destInst &&
                        destInst.getData() != null && destInst.getData().getString("Text").equals(targetStationName())) {
                    if (!train.runtime.completed) {
                        found = true;
                        break;
                    } else {
                        train.runtime.discardSchedule();
                    }
                }
            }
            if (!found) {
                level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
                return;
            }
        } else {
            tickedOnce = true;
        }
    }

    @Override
    public void transform(StructureTransform transform) {
        station.transform(transform);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        station = new TrackTargetingBehaviour<>(this, EdgePointType.STATION);
        behaviours.add(station);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putByte("SelectedColor", ConductorEntity.idFrom(color));
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        color = ConductorEntity.colorFrom(tag.getByte("SelectedColor"));
    }
}
