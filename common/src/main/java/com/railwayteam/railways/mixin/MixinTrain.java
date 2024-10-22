/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.buffer.TrackBuffer;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.content.coupling.coupler.TrackCoupler;
import com.railwayteam.railways.content.fuel.LiquidFuelTrainHandler;
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
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.objectweb.asm.Opcodes;
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
public abstract class MixinTrain implements IOccupiedCouplers, IIndexedSchedule, IHandcarTrain, IStrictSignalTrain, IBufferBlockedTrain, ICrashAdvancement {
    @Shadow public TrackGraph graph;
    @Shadow public Navigation navigation;
    @Shadow public abstract void arriveAt(GlobalStation station);
    @Shadow public List<Carriage> carriages;
    @Shadow public boolean invalid;
    @Shadow public double speed;
    @Shadow public int fuelTicks;
    @Shadow public Player backwardsDriver;

    @Unique private Set<UUID> railways$occupiedCouplers;
    @Unique private int railways$index = 0;
    @Unique private boolean railways$isHandcar = false;
    @Unique private boolean railways$isStrictSignalTrain = false;
    @Unique private int railways$controlBlockedTicks = -1;
    @Unique private int railways$controlBlockedSign = 0;
    
    @Unique private ThreadLocal<Level> railways$level;

    @Override
    public boolean railways$isControlBlocked() {
        return railways$controlBlockedTicks > 0;
    }

    @Override
    public void railways$setControlBlocked(boolean controlBlocked, boolean forceBackwards) {
        railways$controlBlockedTicks = controlBlocked ? 3 : -1;
        railways$controlBlockedSign = forceBackwards ? -1 : Mth.sign(speed);
    }

    @Override
    public int railways$getBlockedSign() {
        return railways$controlBlockedSign;
    }

    @Override
    public void railways$setStrictSignals(boolean strictSignals) {
        railways$isStrictSignalTrain = strictSignals;
    }

    @Override
    public boolean railways$isHandcar() {
        return railways$isHandcar;
    }

    @Override
    public void railways$setHandcar(boolean handcar) {
        railways$isHandcar = handcar;
    }

    @Override
    public int railways$getIndex() {
        return railways$index;
    }

    @Override
    public void railways$setIndex(int index) {
        this.railways$index = index;
    }

    @Override
    public Set<UUID> railways$getOccupiedCouplers() {
        return railways$occupiedCouplers;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCouplers(UUID id, UUID owner, TrackGraph graph, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded, CallbackInfo ci) {
        railways$occupiedCouplers = new HashSet<>();
    }

    @Inject(method = "earlyTick", at = @At("HEAD"))
    private void killEmptyTrains(Level level, CallbackInfo ci) { // hopefully help deal with empty trains
        if (carriages.isEmpty())
            invalid = true;

        if (railways$controlBlockedTicks > 0)
            railways$controlBlockedTicks--;
    }

    @Inject(method = "earlyTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;addToSignalGroups(Ljava/util/Collection;)V", ordinal = 2))
    private void tickOccupiedCouplers(Level level, CallbackInfo ci) {
        for (UUID uuid : railways$occupiedCouplers) {
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
                railways$occupiedCouplers.add(trackCoupler.getId());
                return false;
            }

            if (couple.getFirst() instanceof TrackBuffer) {
                // prevent actual overrun
                speed = 0;
                return true;
            }

            if (((IWaypointableNavigation) navigation).railways$isWaypointMode() && couple.getFirst()instanceof GlobalStation station) {
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

            if (railways$isStrictSignalTrain && couple.getFirst() instanceof SignalBoundary signal) {
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
            railways$occupiedCouplers.remove(coupler.getId());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "collectInitiallyOccupiedSignalBlocks", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", ordinal = 0))
    private void clearOccupiedCouplers(CallbackInfo ci) {
        railways$occupiedCouplers.clear();
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
            railways$occupiedCouplers.add(coupler.getId());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeOccupiedCouplers(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        tag.put("OccupiedCouplers", NBTHelper.writeCompoundList(railways$occupiedCouplers, uid -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("Id", uid);
            return compoundTag;
        }));
        tag.putInt("ScheduleHolderIndex", railways$index);
        tag.putBoolean("IsHandcar", railways$isHandcar);
    }

    @Inject(method = "read", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void readOccupiedCouplers(CompoundTag tag, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> cir,
                                             UUID id, UUID owner, UUID graphId, TrackGraph graph, List<Carriage> carriages, List<Double> carriageSpacing,
                                             boolean doubleEnded, Train train) {

        NBTHelper.iterateCompoundList(tag.getList("OccupiedCouplers", Tag.TAG_COMPOUND),
            c -> ((IOccupiedCouplers) train).railways$getOccupiedCouplers().add(c.getUUID("Id")));
        ((IIndexedSchedule) train).railways$setIndex(tag.getInt("ScheduleHolderIndex"));
        ((IHandcarTrain) train).railways$setHandcar(tag.getBoolean("IsHandcar"));
    }

    @Inject(method = "collideWithOtherTrains", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;crash()V", ordinal = 0), cancellable = true)
    private void railways$handcarCollision(Level level, Carriage carriage, CallbackInfo ci, @Local(name = "train") Train train, @Local Pair<Train, Vec3> collision) {
        Vec3 v = collision.getSecond();

        // Self Train / Train that collided with the other one
        if (((IHandcarTrain) this).railways$isHandcar()) {
            if (!invalid) {
                TrainUtils.discardTrain((Train) (Object) this);
                Containers.dropItemStack(level, v.x, v.y, v.z, CRBlocks.HANDCAR.asStack());
            }

            ((ICrashAdvancement) this).railways$awardCrashAdvancements();

            ci.cancel();
        }

        // Other Train / Train that got collided with
        if (((IHandcarTrain) train).railways$isHandcar()) {
            if (!train.invalid) {
                TrainUtils.discardTrain(train);
                Containers.dropItemStack(level, v.x, v.y, v.z, CRBlocks.HANDCAR.asStack());
            }

            ((ICrashAdvancement) train).railways$awardCrashAdvancements();

            ci.cancel();
        }
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void railways$shareLevel(Level level, CallbackInfo ci) {
        railways$level.set(level);
    }
    
    @WrapOperation(method = "burnFuel", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/entity/Train;fuelTicks:I", opcode = Opcodes.PUTFIELD))
    private void railways$modifyFuelUsage(Train instance, int value, Operation<Void> original) {
        int config = CRConfigs.server().realism.netherExtraFuelUsage.get();
        if (railways$level.get().dimension() == Level.NETHER)
            value -= (config - 1);
        original.call(instance, value);
    }

    @Inject(method = "burnFuel", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", shift = At.Shift.AFTER))
    private void railways$fluidFuelsSystem(CallbackInfo ci) {
        boolean iterateFromBack = speed < 0;
        int carriageCount = carriages.size();

        for (int index = 0; index < carriageCount; index++) {
            int i = iterateFromBack ? carriageCount - 1 - index : index;
            Carriage carriage = carriages.get(i);
            CombinedTankWrapper fuelFluids = ((IFuelInventory) carriage.storage).railways$getFuelFluids();

            if (fuelFluids == null) continue;

            fuelTicks += LiquidFuelTrainHandler.handleFuelDraining(fuelFluids);
        }
    }

    @Inject(method = "maxSpeed", at = @At("RETURN"), cancellable = true)
    public void maxSpeed(CallbackInfoReturnable<Float> cir) {
        if (railways$isHandcar)
            cir.setReturnValue(cir.getReturnValue() * 0.5f);
        else if (CRConfigs.server().realism.realisticTrains.get() && fuelTicks <= 0)
            cir.setReturnValue(AllConfigs.server().trains.trainTopSpeed.getF() / (20 * 20));
    }

    @Inject(method = "maxTurnSpeed", at = @At("RETURN"), cancellable = true)
    public void maxTurnSpeed(CallbackInfoReturnable<Float> cir) {
        if (railways$isHandcar)
            cir.setReturnValue(cir.getReturnValue() * 0.75f);
        else if (CRConfigs.server().realism.realisticTrains.get() && fuelTicks <= 0)
            cir.setReturnValue(AllConfigs.server().trains.trainTurningTopSpeed.getF() / (20 * 20));
    }

    @Inject(method = "acceleration", at = @At("HEAD"), cancellable = true)
    public void acceleration(CallbackInfoReturnable<Float> cir) {
        if (!railways$isHandcar && CRConfigs.server().realism.realisticTrains.get() && fuelTicks <= 0)
            cir.setReturnValue(AllConfigs.server().trains.trainAcceleration.getF() / (400 * 20));
    }

    // Award crash advancements when a train has crashed with a handcar
    @Override
    public void railways$awardCrashAdvancements() {
        for (Carriage carriage : carriages)
            carriage.forEachPresentEntity(e -> e.getIndirectPassengers()
                    .forEach(entity -> {
                        if (!(entity instanceof Player p))
                            return;
                        Optional<UUID> controllingPlayer = e.getControllingPlayer();
                        if (controllingPlayer.isPresent() && controllingPlayer.get()
                                .equals(p.getUUID()))
                            return;
                        AllAdvancements.TRAIN_CRASH.awardTo(p);
                    }));

        if (backwardsDriver != null)
            AllAdvancements.TRAIN_CRASH_BACKWARDS.awardTo(backwardsDriver);
    }
}
