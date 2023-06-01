package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.coupling.coupler.TrackCoupler;
import com.railwayteam.railways.mixin_interfaces.IIndexedSchedule;
import com.railwayteam.railways.mixin_interfaces.IOccupiedCouplers;
import com.railwayteam.railways.mixin_interfaces.IWaypointableNavigation;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
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

    @Inject(method = "earlyTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;addToSignalGroups(Ljava/util/Collection;)V", ordinal = 2))
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

    @Inject(
            method = {
                    "lambda$backSignalListener$12", // fabric
                    "lambda$backSignalListener$10" // forge
            },
            at = @At("HEAD"),
            cancellable = true
    )
    private void backCouplerListener(Double distance, Pair couple, CallbackInfoReturnable<Boolean> cir) {
        if (couple.getFirst() instanceof TrackCoupler coupler) {
            occupiedCouplers.remove(coupler.getId());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "collectInitiallyOccupiedSignalBlocks", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", ordinal = 0))
    private void clearOccupiedCouplers(CallbackInfo ci) {
        occupiedCouplers.clear();
    }

    @Inject(
            method = {
                    "lambda$collectInitiallyOccupiedSignalBlocks$20", // fabric
                    "lambda$collectInitiallyOccupiedSignalBlocks$18" // forge
            },
            at = @At("HEAD"),
            cancellable = true
    )
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
/*
    private CarriageContraptionEntity entityInUse;

    @Redirect(method = "disassemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;getContraption()Lcom/simibubi/create/content/contraptions/Contraption;"))
    private Contraption saveCarriageContraptionEntity(CarriageContraptionEntity instance) {
        entityInUse = instance;
        return instance.getContraption();
    }

    @Redirect(method = "disassemble", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;relative(Lnet/minecraft/core/Direction;I)Lnet/minecraft/core/BlockPos;", remap = true))
    private BlockPos moveHangingCarriageDown(BlockPos instance, Direction pDirection, int pDistance) {
        BlockPos ret = instance.relative(pDirection, pDistance);
        if (entityInUse != null && ((AccessorCarriageBogey) entityInUse.getCarriage().leadingBogey()).getType() instanceof IPotentiallyUpsideDownBogeyBlock pudb && pudb.isUpsideDown()) {
            ret = ret.below(2);
        }
        entityInUse = null;
        return ret;
    }

    private Carriage tmpCarriage;
    private Carriage tmpPrevCarriage;

    @Inject(method = "tick", at = @At("HEAD"))
    private void resetTmpCarriagesHead(Level level, CallbackInfo ci) {
        tmpCarriage = tmpPrevCarriage = null;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D", remap = true), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void storeTmpCarriages(Level level, CallbackInfo ci, double distance, Carriage previousCarriage, int carriageCount, boolean stalled,
                                   double maxStress, int i, Carriage carriage) {
        tmpCarriage = carriage;
        tmpPrevCarriage = previousCarriage;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D", remap = true)) //TODO set carriages right before this
    private double adjustDistanceTo(Vec3 instance, Vec3 pVec) {
        if (tmpCarriage != null && tmpPrevCarriage != null) {
            if (isUpsideDown(tmpCarriage.leadingBogey()) != isUpsideDown(tmpPrevCarriage.trailingBogey())) {
                tmpCarriage = tmpPrevCarriage = null;
                //account for 2 block height difference
                return Math.sqrt(instance.distanceToSqr(pVec)-4);
            }
        }
        tmpCarriage = tmpPrevCarriage = null;
        return instance.distanceTo(pVec);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void resetTmpCarriagesReturn(Level level, CallbackInfo ci) {
        tmpCarriage = tmpPrevCarriage = null;
    }*/
}
