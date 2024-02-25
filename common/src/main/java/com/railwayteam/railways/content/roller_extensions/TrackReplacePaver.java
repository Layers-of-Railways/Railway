package com.railwayteam.railways.content.roller_extensions;

import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.simibubi.create.content.contraptions.actors.roller.PaveTask;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TrackReplacePaver {

    @ApiStatus.Internal
    public static boolean tickInstantly;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void pave(MovementContext context, BlockPos pos, BlockState stateToPaveWith, @Nullable PaveTask trackProfile) {
        BlockState replacedState;
        BlockPos trackPos = pos.above();
        if (trackProfile != null) {
            Couple<Integer> key = Couple.create(trackPos.getX(), trackPos.getZ());
            if (trackProfile.keys().contains(key)) {
                int height = (int) trackProfile.get(key);
                trackPos = new BlockPos(trackPos.getX(), height + 1, trackPos.getZ());
            }
        }
        if ((replacedState = context.world.getBlockState(trackPos)).getBlock() instanceof ITrackBlock
                && stateToPaveWith.getBlock() instanceof ITrackBlock newTrackBlock) {
            FilterItemStack filter = FilterItemStack.of(context.blockEntityData.getCompound("Filter"));
            if (replacedState.getBlock() != stateToPaveWith.getBlock()) {
                boolean restoreBE = false;
                Pair<SlabBlock, Boolean> casingData = null;
                Map<BlockPos, BezierConnection> connections = new HashMap<>();
                if (replacedState.getOptionalValue(TrackBlock.HAS_BE).orElse(false)) {
                    if (context.world.getBlockEntity(trackPos) instanceof TrackBlockEntity trackBE) {
                        restoreBE = true;
                        casingData = Pair.of(
                                ((IHasTrackCasing) trackBE).getTrackCasing(),
                                ((IHasTrackCasing) trackBE).isAlternate());
                        connections.putAll(trackBE.getConnections());
                        //trackBE.removeInboundConnections(false);
                        trackBE.getConnections().clear();
                        for (BlockPos key : connections.keySet()) {
                            if (context.world.getBlockEntity(key) instanceof TrackBlockEntity otherBE) {
                                otherBE.getConnections().remove(trackPos);
                            }
                        }
                        trackBE.invalidate();
                    }
                }
                for (Property property : replacedState.getProperties()) {
                    if (stateToPaveWith.hasProperty(property))
                        stateToPaveWith = stateToPaveWith.setValue(property, replacedState.getValue(property));
                }
                ItemStack held = extract(filter, context);
                if (!held.isEmpty()) {
                    tickInstantly = true;
                    context.world.setBlock(trackPos, stateToPaveWith, 3);
                    tickInstantly = false;
                }
                if (restoreBE && context.world.getBlockEntity(trackPos) instanceof TrackBlockEntity trackBE) {
                    //trackBE.load(beTag);
                    // restore connections
                    for (Map.Entry<BlockPos, BezierConnection> entry : connections.entrySet()) {
                        if (context.world.getBlockEntity(entry.getKey()) instanceof TrackBlockEntity otherBE) {
                            trackBE.getConnections().put(entry.getKey(), entry.getValue());
                            otherBE.getConnections().put(trackPos, entry.getValue().secondary());
                        }
                    }
                    if (casingData != null) {
                        ((IHasTrackCasing) trackBE).setTrackCasing(casingData.getFirst());
                        ((IHasTrackCasing) trackBE).setAlternate(casingData.getSecond());
                    }
//                    trackBE.validateConnections();
//                    trackBE.notifyUpdate();

                    //context.world.scheduleTick(trackPos, stateToPaveWith.getBlock(), 1);
                }
            }
            if (context.world.getBlockEntity(trackPos) instanceof TrackBlockEntity trackBE) {
                boolean changed = false;
                for (Map.Entry<BlockPos, BezierConnection> entry : trackBE.getConnections().entrySet()) {
                    BezierConnection connection = entry.getValue();
                    int requiredTracksForTurn = (connection.getSegmentCount() + 1) / 2;
                    if (connection.getMaterial() == newTrackBlock.getMaterial())
                        continue;
                    if (extract(filter, context, requiredTracksForTurn).isEmpty())
                        continue;

                    connection.setMaterial(newTrackBlock.getMaterial());
                    if (context.world.getBlockEntity(entry.getKey()) instanceof TrackBlockEntity other) {
                        if (other.getConnections().containsKey(trackPos)) {
                            other.getConnections().get(trackPos).setMaterial(newTrackBlock.getMaterial());
                            other.notifyUpdate();
                        }
                    }
                    changed = true;
                }
                //noinspection ConstantValue
                if (changed)
                    trackBE.notifyUpdate();
            }
        }
    }

    public static ItemStack extract(FilterItemStack filter, MovementContext context) {
        return extract(filter, context, 1);
    }

    @ExpectPlatform
    public static ItemStack extract(FilterItemStack filter, MovementContext context, int amt) {
        throw new AssertionError();
    }
}
