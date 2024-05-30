/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.IConductorHoldingFakePlayer;
import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchState;
import com.railwayteam.railways.mixin_interfaces.*;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.BlockPosUtils;
import com.railwayteam.railways.util.MixinVariables;
import com.railwayteam.railways.util.packet.SwitchDataUpdatePacket;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.*;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.railwayteam.railways.util.BlockPosUtils.normalize;

@Mixin(value = CarriageContraptionEntity.class, remap = false)
public abstract class MixinCarriageContraptionEntity extends OrientedContraptionEntity implements IDistanceTravelled {
    @Shadow private Carriage carriage;

    private MixinCarriageContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Unique private boolean railways$fakePlayer = false;

    @Unique private double railways$distanceTravelled;

    @Inject(method = "control", at = @At("HEAD"))
    private void recordFakePlayer(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player.getGameProfile() == ConductorEntity.FAKE_PLAYER_PROFILE)
            railways$fakePlayer = true;
    }

    @WrapOperation(method = "control", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z", remap = true))
    private boolean railways$closerThan(Vec3 instance, Position pos, double distance, Operation<Boolean> original) {
        if (railways$fakePlayer) {
            railways$fakePlayer = false;
            return true;
        }
        return original.call(instance, pos, distance);
    }

    @WrapOperation(method = "control", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/entity/Train;throttle:D", opcode = Opcodes.GETFIELD))
    private double conductorSpeedControl(Train instance, Operation<Double> original, BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
        if (player instanceof IConductorHoldingFakePlayer conductorHolder && conductorHolder.getConductor() != null) {
            return conductorHolder.getConductor().getForwardSignalStrength() / 15.0d;
        }
        return original.call(instance);
    }

    @Inject(method = "control", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;maxSpeed()F"))
    private void showSwitchOverlay(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player,
                                   CallbackInfoReturnable<Boolean> cir) {
        Navigation nav = carriage.train.navigation;

        StructureBlockInfo info = contraption.getBlocks()
                .get(controlsLocalPos);
        Direction initialOrientation = getInitialOrientation().getCounterClockWise();
        boolean inverted = false;
        if (info != null && info.state().hasProperty(ControlsBlock.FACING))
            inverted = !info.state().getValue(ControlsBlock.FACING)
                    .equals(initialOrientation);

        int targetSpeed = 0;
        if (heldControls.contains(0))
            targetSpeed++;
        if (heldControls.contains(1))
            targetSpeed--;

        if (inverted) {
            targetSpeed *= -1;
        }

        boolean spaceDown = heldControls.contains(4);

        double directedSpeed = targetSpeed != 0 ? targetSpeed : carriage.train.speed;
        MixinVariables.temporarilySkipSwitches = true;
        boolean forward = !carriage.train.doubleEnded || (directedSpeed != 0 ? directedSpeed > 0 : !inverted);
        Pair<TrackSwitch, Pair<Boolean, Optional<SwitchState>>> lookAheadData = ((IGenerallySearchableNavigation) nav).railways$findNearestApproachableSwitch(forward);
        MixinVariables.temporarilySkipSwitches = false;
        TrackSwitch lookAhead = lookAheadData == null ? null : lookAheadData.getFirst();
        boolean headOn = lookAheadData != null && lookAheadData.getSecond().getFirst();
        Optional<SwitchState> targetState = lookAheadData == null ? Optional.empty() : lookAheadData.getSecond().getSecond();

        if (lookAhead != null) {
            // try to reserve switch
            if (CRConfigs.server().flipDistantSwitches.get() && spaceDown && lookAhead.isAutomatic()
                    && !lookAhead.isLocked() && !carriage.train.navigation.isActive()) {
                if (headOn) {
                    lookAhead.trySetSwitchState(SwitchState.fromSteerDirection(carriage.train.manualSteer, forward));
                } else targetState.ifPresent(lookAhead::trySetSwitchState);
            }
            boolean wrong = headOn ?
                    SwitchState.fromSteerDirection(carriage.train.manualSteer, forward) != lookAhead.getSwitchState() :
                    targetState.isPresent() && lookAhead.getSwitchState() != targetState.get();
            displayApproachSwitchMessage(player, lookAhead, wrong);
        } else
            cleanUpApproachSwitchMessage(player);
    }

    @Inject(method = "control", at = @At("TAIL"))
    private void railways$handcarHungerDepletion(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (((IHandcarTrain) this.carriage.train).railways$isHandcar()
                && !player.getItemInHand(InteractionHand.MAIN_HAND).is(AllItems.EXTENDO_GRIP.get()))
            player.causeFoodExhaustion((float) carriage.train.speed * CRConfigs.server().handcarHungerMultiplier.getF());
    }

    @Unique
    boolean railways$switchMessage = false;

    @Unique
    private void displayApproachSwitchMessage(Player player, TrackSwitch sw, boolean isWrong) {
        sendSwitchInfo(player, sw.getSwitchState(), sw.isAutomatic(), isWrong, sw.isLocked());
        railways$switchMessage = true;
    }

    @Unique
    private void cleanUpApproachSwitchMessage(Player player) {
        if (!railways$switchMessage)
            return;
        if (player instanceof ServerPlayer sp)
            CRPackets.PACKETS.sendTo(sp, SwitchDataUpdatePacket.clear());
        railways$switchMessage = false;
    }

    @Unique
    private void sendSwitchInfo(Player player, SwitchState state, boolean automatic, boolean isWrong, boolean isLocked) {
        if (player instanceof ServerPlayer sp)
            CRPackets.PACKETS.sendTo(sp, new SwitchDataUpdatePacket(state, automatic, isWrong, isLocked));
    }

    @Inject(method = "updateTrackGraph", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/entity/Train;graph:Lcom/simibubi/create/content/trains/graph/TrackGraph;", opcode = Opcodes.H_PUTFIELD, ordinal = 0), cancellable = true)
    private void cancelDerailing(CallbackInfo ci) {
        if (CRConfigs.client().skipClientDerailing.get())
            ci.cancel();
    }

    @Inject(method = "tickContraption", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;tickActors()V"))
    private void setupBufferDistanceData(CallbackInfo ci) {
        ICarriageBufferDistanceTracker distanceTracker = (ICarriageBufferDistanceTracker) carriage;
        if (level.isClientSide) return;

        if (distanceTracker.railways$getLeadingDistance() != null && distanceTracker.railways$getTrailingDistance() != null) return;

        BlockPos leadingBogeyPos = null;
        BlockPos trailingBogeyPos = null;

        CarriageContraption cc = (CarriageContraption) contraption;

        BlockPos maxPos = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        BlockPos minPos = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (Map.Entry<BlockPos, StructureBlockInfo> info : contraption.getBlocks().entrySet()) {
            minPos = BlockPosUtils.min(minPos, info.getKey());
            maxPos = BlockPosUtils.max(maxPos, info.getKey());
            if (info.getValue().state.getBlock() instanceof AbstractBogeyBlock<?>) {
                if (leadingBogeyPos == null) {
                    leadingBogeyPos = info.getKey();
                } else if (trailingBogeyPos == null) {
                    if (normalize(info.getKey().subtract(leadingBogeyPos)).equals(cc.getAssemblyDirection().getNormal())) {
                        trailingBogeyPos = info.getKey();
                    } else {
                        trailingBogeyPos = leadingBogeyPos;
                        leadingBogeyPos = info.getKey();
                    }
                }
            }
        }

        Direction.Axis axis = cc.getAssemblyDirection().getAxis();
        boolean forward = cc.getAssemblyDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE;

        int leadingBounds = (int) (forward ? minPos.get(axis) : maxPos.get(axis));
        int trailingBounds = (int) (forward ? maxPos.get(axis) : minPos.get(axis));

        int leadingDistance = leadingBogeyPos == null ? 0 : Math.abs(leadingBounds - leadingBogeyPos.get(axis));
        if (trailingBogeyPos == null)
            trailingBogeyPos = leadingBogeyPos;
        int trailingDistance = trailingBogeyPos == null ? 0 : Math.abs(trailingBounds - trailingBogeyPos.get(axis));
        distanceTracker.railways$setLeadingDistance(leadingDistance);
        distanceTracker.railways$setTrailingDistance(trailingDistance);
    }

    @Inject(method = "control", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;getCurrentStation()Lcom/simibubi/create/content/trains/station/GlobalStation;"))
    private void noBufferOverrun(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player,
                                 CallbackInfoReturnable<Boolean> cir, @Local(name="targetSpeed") LocalIntRef targetSpeedRef) {
        double targetSpeed = targetSpeedRef.get();
        if (targetSpeed == 0) return;

        IBufferBlockedTrain bufferBlockedTrain = (IBufferBlockedTrain) carriage.train;

        // The control blocked update in Navigation assumes the train is going forward if it is stationary, so we need an extra check here
        if (!bufferBlockedTrain.railways$isControlBlocked() && bufferBlockedTrain.railways$getBlockedSign() == 0 && targetSpeed < 0) {
            ((IBufferBlockCheckableNavigation) carriage.train.navigation).railways$updateControlsBlock(true);
        }

        if (bufferBlockedTrain.railways$isControlBlocked()) {
            double blockedSign = bufferBlockedTrain.railways$getBlockedSign();
            if ((blockedSign == 0 && targetSpeed > 0) || blockedSign == Mth.sign(targetSpeed)) {
                targetSpeedRef.set(0);
            }
        }
    }

    @Inject(method = "tickContraption", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/Couple;getFirst()Ljava/lang/Object;"))
    private void railways$storeDistanceTravelled(CallbackInfo ci, @Local(name = "distanceTo", ordinal = 0) double distanceTo) {
        railways$distanceTravelled = distanceTo;
    }

    @Override
    public double railways$getDistanceTravelled() {
        return railways$distanceTravelled;
    }
}
