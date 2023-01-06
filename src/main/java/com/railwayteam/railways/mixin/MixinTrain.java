package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.coupling.coupler.TrackCoupler;
import com.railwayteam.railways.mixin_interfaces.IIndexedSchedule;
import com.railwayteam.railways.mixin_interfaces.IOccupiedCouplers;
import com.railwayteam.railways.mixin_interfaces.IWaypointableNavigation;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.TrackNode;
import com.simibubi.create.content.logistics.trains.entity.*;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.TrackEdgePoint;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleRuntime;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(value = Train.class, remap = false)
public abstract class MixinTrain implements IOccupiedCouplers, IIndexedSchedule {
    @Shadow public TrackGraph graph;

    @Shadow public Navigation navigation;
    @Shadow public double speed;

    @Shadow public abstract void arriveAt(GlobalStation station);

    @Shadow public abstract void leaveStation();

    @Shadow public ScheduleRuntime runtime;
    public Set<UUID> occupiedCouplers;
    protected int index = 0;

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public Set<UUID> getOccupiedCouplers() {
        return occupiedCouplers;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCouplers(UUID id, UUID owner, TrackGraph graph, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded, CallbackInfo ci) {
        occupiedCouplers = new HashSet<>();
    }

    @Inject(method = "earlyTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/Train;addToSignalGroups(Ljava/util/Collection;)V", ordinal = 2))
    private void tickOccupiedCouplers(Level level, CallbackInfo ci) {
        for (UUID uuid : occupiedCouplers) {
            TrackCoupler coupler = graph.getPoint(CREdgePointTypes.COUPLER, uuid);
            if (coupler == null)
                continue;

            coupler.keepAlive((Train) (Object) this);
        }
    }

    @Inject(method = "frontSignalListener", at = @At("RETURN"), cancellable = true)
    private void frontCouplerListener(CallbackInfoReturnable<TravellingPoint.IEdgePointListener> cir) {
        TravellingPoint.IEdgePointListener originalListener = cir.getReturnValue();
        cir.setReturnValue((distance, couple) -> {
            if (couple.getFirst() instanceof TrackCoupler trackCoupler) {
                occupiedCouplers.add(trackCoupler.getId());
                return false;
            }

            if (((IWaypointableNavigation) navigation).isWaypointMode() && couple.getFirst()instanceof GlobalStation station) {
                if (!station.canApproachFrom(couple.getSecond()
                    .getSecond()) || navigation.destination != station)
                    return false;
                //speed = 0; // No slowing down
                navigation.distanceToDestination = 0;
                ((AccessorNavigation) navigation).getCurrentPath().clear();
                arriveAt(navigation.destination);
                navigation.destination = null;
                return true;
            }

            return originalListener.test(distance, couple);
        });
    }

    @Inject(method = "lambda$backSignalListener$10", at = @At("HEAD"), cancellable = true)
    private void backCouplerListener(Double distance, Pair<TrackEdgePoint, Couple<TrackNode>> couple, CallbackInfoReturnable<Boolean> cir) {
        if (couple.getFirst() instanceof TrackCoupler coupler) {
            occupiedCouplers.remove(coupler.getId());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "collectInitiallyOccupiedSignalBlocks", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", ordinal = 0))
    private void clearOccupiedCouplers(CallbackInfo ci) {
        occupiedCouplers.clear();
    }

    @Inject(method = "lambda$collectInitiallyOccupiedSignalBlocks$18", at = @At("HEAD"), cancellable = true)
    private void reAddOccupiedCouplers(MutableObject<UUID> prevGroup, Double distance, Pair<TrackEdgePoint, Couple<TrackNode>> couple, CallbackInfoReturnable<Boolean> cir) {
        if (couple.getFirst() instanceof TrackCoupler coupler) {
            occupiedCouplers.add(coupler.getId());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeOccupiedCouplers(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        tag.put("OccupiedCouplers", NBTHelper.writeCompoundList(occupiedCouplers, uid -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("Id", uid);
            return compoundTag;
        }));
        tag.putInt("ScheduleHolderIndex", index);
    }

    @Inject(method = "read", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void readOccupiedCouplers(CompoundTag tag, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> cir,
                                             UUID id, UUID owner, UUID graphId, TrackGraph graph, List<Carriage> carriages, List<Double> carriageSpacing,
                                             boolean doubleEnded, Train train) {

        NBTHelper.iterateCompoundList(tag.getList("OccupiedCouplers", Tag.TAG_COMPOUND),
            c -> ((IOccupiedCouplers) train).getOccupiedCouplers().add(c.getUUID("Id")));
        ((IIndexedSchedule) train).setIndex(tag.getInt("ScheduleHolderIndex"));
    }
}
