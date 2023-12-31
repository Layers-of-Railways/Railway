package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.buffer.TrackBuffer;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.content.coupling.coupler.TrackCoupler;
import com.railwayteam.railways.mixin_interfaces.*;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(value = Train.class, remap = false)
public abstract class MixinTrain implements IOccupiedCouplers, IIndexedSchedule, IHandcarTrain, IStrictSignalTrain, IBufferBlockedTrain {
    @Shadow public TrackGraph graph;

    @Shadow public Navigation navigation;
    @Shadow public double speed;

    @Shadow public abstract void arriveAt(GlobalStation station);

    @Shadow public List<Carriage> carriages;
    @Shadow public boolean invalid;
    @Unique
    public Set<UUID> snr$occupiedCouplers;
    @Unique
    protected int snr$index = 0;
    @Unique
    protected boolean snr$isHandcar = false;
    @Unique
    protected boolean snr$isStrictSignalTrain = false;
    @Unique
    protected int snr$controlBlockedTicks = -1;
    @Unique
    protected int snr$controlBlockedSign = 0;

    @Override
    public boolean snr$isControlBlocked() {
        return snr$controlBlockedTicks > 0;
    }

    @Override
    public void snr$setControlBlocked(boolean controlBlocked) {
        snr$controlBlockedTicks = controlBlocked ? 3 : -1;
        if (controlBlocked && Mth.sign(speed) != 0) {
            snr$controlBlockedSign = Mth.sign(speed);
        }
    }

    @Override
    public int snr$getBlockedSign() {
        return snr$isControlBlocked() ? snr$controlBlockedSign : 0;
    }

    @Override
    public void snr$setStrictSignals(boolean strictSignals) {
        snr$isStrictSignalTrain = strictSignals;
    }

    @Override
    public boolean snr$isHandcar() {
        return snr$isHandcar;
    }

    @Override
    public void snr$setHandcar(boolean handcar) {
        snr$isHandcar = handcar;
    }

    @Override
    public int snr$getIndex() {
        return snr$index;
    }

    @Override
    public void snr$setIndex(int index) {
        this.snr$index = index;
    }

    @Override
    public Set<UUID> snr$getOccupiedCouplers() {
        return snr$occupiedCouplers;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCouplers(UUID id, UUID owner, TrackGraph graph, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded, CallbackInfo ci) {
        snr$occupiedCouplers = new HashSet<>();
    }

    @Inject(method = "earlyTick", at = @At("HEAD"))
    private void killEmptyTrains(Level level, CallbackInfo ci) { // hopefully help deal with empty trains
        if (carriages.size() == 0)
            invalid = true;

        if (snr$controlBlockedTicks > 0)
            snr$controlBlockedTicks--;
    }

    @Inject(method = "earlyTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;addToSignalGroups(Ljava/util/Collection;)V", ordinal = 2))
    private void tickOccupiedCouplers(Level level, CallbackInfo ci) {
        for (UUID uuid : snr$occupiedCouplers) {
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
                snr$occupiedCouplers.add(trackCoupler.getId());
                return false;
            }

            if (couple.getFirst() instanceof TrackBuffer) {
                // prevent actual overrun
                speed = 0;
                return true;
            }

            if (((IWaypointableNavigation) navigation).snr$isWaypointMode() && couple.getFirst()instanceof GlobalStation station) {
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

            if (snr$isStrictSignalTrain && couple.getFirst() instanceof SignalBoundary signal) {
                UUID groupId = signal.getGroup(couple.getSecond()
                    .getSecond());
                SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(groupId);
                if (signalEdgeGroup != null && signalEdgeGroup.isOccupiedUnless((Train)(Object)this)) {
                    return true;
                }
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
    private void backCouplerListener(Double distance, Pair<TrackEdgePoint, Couple<TrackNode>> couple, CallbackInfoReturnable<Boolean> cir) {
        if (couple.getFirst() instanceof TrackCoupler coupler) {
            snr$occupiedCouplers.remove(coupler.getId());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "collectInitiallyOccupiedSignalBlocks", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", ordinal = 0))
    private void clearOccupiedCouplers(CallbackInfo ci) {
        snr$occupiedCouplers.clear();
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
            snr$occupiedCouplers.add(coupler.getId());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeOccupiedCouplers(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        tag.put("OccupiedCouplers", NBTHelper.writeCompoundList(snr$occupiedCouplers, uid -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("Id", uid);
            return compoundTag;
        }));
        tag.putInt("ScheduleHolderIndex", snr$index);
        tag.putBoolean("IsHandcar", snr$isHandcar);
    }

    @Inject(method = "read", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void readOccupiedCouplers(CompoundTag tag, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> cir,
                                             UUID id, UUID owner, UUID graphId, TrackGraph graph, List<Carriage> carriages, List<Double> carriageSpacing,
                                             boolean doubleEnded, Train train) {

        NBTHelper.iterateCompoundList(tag.getList("OccupiedCouplers", Tag.TAG_COMPOUND),
            c -> ((IOccupiedCouplers) train).snr$getOccupiedCouplers().add(c.getUUID("Id")));
        ((IIndexedSchedule) train).snr$setIndex(tag.getInt("ScheduleHolderIndex"));
        ((IHandcarTrain) train).snr$setHandcar(tag.getBoolean("IsHandcar"));
    }

    @Inject(method = "collideWithOtherTrains", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;crash()V", ordinal = 0), cancellable = true)
    private void snr$handcarCollision(Level level, Carriage carriage, CallbackInfo ci, @Local Train train, @Local(name = "v", ordinal = 0) Vec3 v) {
        // Self Train / Train that collided with the other one
        if (((IHandcarTrain) this).snr$isHandcar()) {
            if (!invalid) {
                TrainUtils.discardTrain((Train) (Object) this);
                Containers.dropItemStack(level, v.x, v.y, v.z, CRBlocks.HANDCAR.asStack());
            }
            ci.cancel();
        }

        // Other Train / Train that got collided with
        if (((IHandcarTrain) train).snr$isHandcar()) {
            if (!train.invalid) {
                TrainUtils.discardTrain(train);
                Containers.dropItemStack(level, v.x, v.y, v.z, CRBlocks.HANDCAR.asStack());
            }
            ci.cancel();
        }
    }

    @Inject(method = "collideWithOtherTrains", at = @At("HEAD"), cancellable = true)
    private void maybeNoCollision(Level level, Carriage carriage, CallbackInfo ci) {
        if (CRConfigs.server().optimization.disableTrainCollision.get())
            ci.cancel();
    }

    @Inject(method = "maxSpeed", at = @At("RETURN"), cancellable = true)
    private void slowDownHandcars(CallbackInfoReturnable<Float> cir) {
        if (snr$isHandcar)
            cir.setReturnValue(cir.getReturnValue() * 0.5f);
    }

    @Inject(method = "maxTurnSpeed", at = @At("RETURN"), cancellable = true)
    private void slowDownHandcarsOnTurns(CallbackInfoReturnable<Float> cir) {
        if (snr$isHandcar)
            cir.setReturnValue(cir.getReturnValue() * 0.75f);
    }
}
