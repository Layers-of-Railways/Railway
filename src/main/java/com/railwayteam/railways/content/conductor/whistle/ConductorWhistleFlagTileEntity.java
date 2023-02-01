package com.railwayteam.railways.content.conductor.whistle;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.structureMovement.ITransformableTE;
import com.simibubi.create.content.contraptions.components.structureMovement.StructureTransform;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.Schedule;
import com.simibubi.create.content.logistics.trains.management.schedule.destination.DestinationInstruction;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ConductorWhistleFlagTileEntity extends SmartTileEntity implements ITransformableTE {

    public TrackTargetingBehaviour<GlobalStation> station;
    private boolean tickedOnce = false;

    public ConductorWhistleFlagTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(100);
    }

    protected String targetStationName() {
        return ConductorWhistleItem.SPECIAL_MARKER+this.getBlockPos().toShortString();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level.isClientSide)
            return;

        if (station.getEdgePoint() == null)
            station.tick();
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
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        station = new TrackTargetingBehaviour<>(this, EdgePointType.STATION);
        behaviours.add(station);
    }
}
