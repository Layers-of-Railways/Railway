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

package com.railwayteam.railways.content.semaphore;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.distant_signals.IOverridableSignal;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRIcons;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.packet.OverridableSignalPacket;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

public class SemaphoreBlockEntity extends SmartBlockEntity implements IOverridableSignal {
    private WeakReference<SignalBlockEntity> cachedSignalTE;
    public SignalBlockEntity.SignalState signalState;
    public final LerpedFloat armPosition;
    public boolean isValid = false;
    public boolean isDistantSignal=false;
    protected boolean cachedWasUpsideDown = false;
    private int overrideLastingTicks = -1;

    public SemaphoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        cachedSignalTE = new WeakReference<>(null);
        armPosition = LerpedFloat.linear()
                .startWithValue(0);
        setLazyTickRate(10);
    }

    public boolean wasCachedSearchingUpsideDown() {
        return cachedWasUpsideDown;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        cachedWasUpsideDown = tag.getBoolean("CachedWasUpsideDown");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("CachedWasUpsideDown", cachedWasUpsideDown);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }
    @Override
    public void tick() {

        super.tick();
        if (!level.isClientSide)
            return;

        if (overrideLastingTicks > 0) {
            overrideLastingTicks--;
            SignalBlockEntity signalBE = cachedSignalTE.get();
            if (signalBE != null && !signalBE.isRemoved()) {
                signalState = signalBE.getState();
            }
            isValid = signalState != SignalBlockEntity.SignalState.INVALID;
            boolean isActive = false;
            if (isValid)
                isActive = (signalState == SignalBlockEntity.SignalState.YELLOW && !isDistantSignal) || signalState == SignalBlockEntity.SignalState.GREEN;

            float currentTarget = armPosition.getChaseTarget();
            int target = isActive ? 1 : 0;
            if (target != currentTarget) {
                armPosition.setValue(currentTarget);
                armPosition.chase(target, 0.05f, LerpedFloat.Chaser.LINEAR);
            }

            armPosition.tickChaser();
            return;
        } else if (overrideLastingTicks == 0) {
            overrideLastingTicks--;
            cachedSignalTE.clear();
            signalState = null;
            updateSignalConnection();
        }



        SignalBlockEntity signalBlockEntity = cachedSignalTE.get();




        // whether the arm is up (i.e. signalling that a train can pass)
        boolean isActive=false;

        if (signalBlockEntity != null && !signalBlockEntity.isRemoved() && isValid) {
            signalState = signalBlockEntity.getState();

            if(signalState == SignalBlockEntity.SignalState.INVALID)
                isValid=false;
            else
                isActive = (signalState == SignalBlockEntity.SignalState.YELLOW && !isDistantSignal) || signalState == SignalBlockEntity.SignalState.GREEN;
        }

        float currentTarget = armPosition.getChaseTarget();
        int target = isActive ? 1 : 0;
        if (target != currentTarget) {
            armPosition.setValue(currentTarget);
            armPosition.chase(target, 0.05f, LerpedFloat.Chaser.LINEAR);
        }

        armPosition.tickChaser();




    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (overrideLastingTicks > 0) return;
        signalState = null;

        updateSignalConnection();
    }

    boolean updateSignalConnection() {
        if (updateSignalConnection(cachedWasUpsideDown)) {
            return true;
        } else if (updateSignalConnection(!cachedWasUpsideDown)) {
            cachedWasUpsideDown = !cachedWasUpsideDown;
            return true;
        }
        return false;
    }

    boolean updateSignalConnection(boolean upsideDown)
    {
        isValid=false;
        isDistantSignal=false;
        BlockPos currentPos = upsideDown?worldPosition.above():worldPosition.below();
        int semaphoresBelow = 0;
        boolean constantOrder = !(getBlockState().getValue(SemaphoreBlock.UPSIDE_DOWN) && CRConfigs.server().semaphores.flipYellowOrder.get());
        //count downwards from the semaphore along the pole blocks, until a signal is reached
        for (int i = 0; i < 16; i++) {
            BlockState blockState = level.getBlockState(currentPos);
            BlockEntity blockEntity = level.getBlockEntity(currentPos);
            if (blockEntity instanceof SignalBlockEntity signal) {
                signalState = signal.getState();
                cachedSignalTE = new WeakReference<>(signal);
                isValid = true;
                SignalBlock.SignalType stateType = blockState.getValue(SignalBlock.TYPE);


                if (semaphoresBelow == 0 != (upsideDown && constantOrder)) {
                    currentPos = upsideDown?(constantOrder?currentPos:worldPosition).below():worldPosition.above();
                    //if the signal is a cross-signal, and this semaphore is at the bottom of the stack,
                    //count upwards to find other semaphores. if one is found this semaphore becomes caution-type
                    for (int j = i + 1; j < 16; j++) {
                        blockState = level.getBlockState(currentPos);
                        blockEntity = level.getBlockEntity(currentPos);
                        if (blockEntity instanceof SemaphoreBlockEntity semaphore && semaphore.wasCachedSearchingUpsideDown() == this.wasCachedSearchingUpsideDown()) {
                            isDistantSignal = true;
                            break;
                        }
                        if (!CRTags.AllBlockTags.SEMAPHORE_POLES.matches(blockState)) {
                            break;
                        }
                        currentPos = upsideDown?currentPos.below():currentPos.above();
                    }
                }
                //the semaphore is valid as a danger-type semaphore
                // if it has exactly one other semaphore below,
                //or if no signal was found above
                break;

            }
            if(blockEntity instanceof SemaphoreBlockEntity)
            {
                semaphoresBelow++;
                if(semaphoresBelow>1)
                    break;
            }else if(!CRTags.AllBlockTags.SEMAPHORE_POLES.matches(blockState))
            {
                break;
            }
            currentPos = upsideDown?currentPos.above():currentPos.below();
        }
        return isValid;
    }

    @Override
    public void railways$refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks, boolean distantSignal) {
        if (level == null) return;
        cachedSignalTE = new WeakReference<>(signalBE);
        signalState = state;
        overrideLastingTicks = ticks;
        isDistantSignal = distantSignal;
        if (!level.isClientSide) {
            CRPackets.PACKETS.sendTo(PlayerSelection.tracking(this),
                new OverridableSignalPacket(getBlockPos(),signalBE == null ? null : signalBE.getBlockPos(), state, ticks, distantSignal));
        }
    }

    @Override
    public Optional<SignalBlockEntity.SignalState> railways$getOverriddenState() {
        if (overrideLastingTicks > 0 && signalState != null)
            return Optional.of(signalState);
        return Optional.empty();
    }

    private static class SemaphoreValueBoxTransform extends CenteredSideValueBoxTransform {

        public SemaphoreValueBoxTransform() {
            super((state, d) -> {
                if (d.getAxis()
                    .isVertical())
                    return false;
                boolean flipped = state.getValue(SemaphoreBlock.FLIPPED);
                Direction facing = state.getValue(SemaphoreBlock.FACING);
                return d == facing || (facing.getCounterClockWise() == (flipped ? d.getOpposite() : d));
            });
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 11);
        }

    }

    public enum SearchMode implements INamedIconOptions {
        SEARCH_DOWN(CRIcons.I_SEARCH_DOWN),
        SEARCH_UP(CRIcons.I_SEARCH_UP)

        ;

        private final String translationKey;
        private final AllIcons icon;

        SearchMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "railways.semaphore.search_mode." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }

        public boolean isUpsideDown() {
            return this == SEARCH_UP;
        }

        public static SearchMode fromUpsideDown(boolean upsideDown) {
            return upsideDown ? SEARCH_UP : SEARCH_DOWN;
        }
    }
}
