package com.railwayteam.railways.content.semaphore;

import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalBlock;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.ref.WeakReference;
import java.util.List;

public class SemaphoreBlockEntity extends SmartTileEntity {
    private WeakReference<SignalTileEntity> cachedSignalTE;
    public SignalTileEntity.SignalState signalState;
    public final LerpedFloat armPosition;
    public boolean isValid = false;
    public boolean isDistantSignal=false;
    public SemaphoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        cachedSignalTE = new WeakReference<>(null);
        armPosition = LerpedFloat.linear()
                .startWithValue(0);
        setLazyTickRate(10);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
    }
    @Override
    public void tick() {

        super.tick();
        if (!level.isClientSide)
            return;



        SignalTileEntity signalTileEntity = cachedSignalTE.get();




        boolean isActive=false;

        if (signalTileEntity != null && !signalTileEntity.isRemoved() && isValid) {
            signalState = signalTileEntity.getState();

            if(signalState == SignalTileEntity.SignalState.INVALID)
                isValid=false;
            else
                isActive = (signalState == SignalTileEntity.SignalState.YELLOW && !isDistantSignal) || signalState == SignalTileEntity.SignalState.GREEN;
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
        signalState = null;

        updateSignalConnection();
    }

    void updateSignalConnection()
    {
        isValid=false;
        isDistantSignal=false;
        BlockPos currentPos = worldPosition.below();
        int semaphoresBelow = 0;
        //count downwards from the semaphore along the pole blocks, until a signal is reached
        for (int i = 0; i < 16; i++) {
            BlockState blockState = level.getBlockState(currentPos);
            BlockEntity blockEntity = level.getBlockEntity(currentPos);
            if (blockEntity instanceof SignalTileEntity signal) {
                signalState = signal.getState();
                cachedSignalTE = new WeakReference<>(signal);
                isValid = true;
                SignalBlock.SignalType stateType = blockState.getValue(SignalBlock.TYPE);


                if (semaphoresBelow == 0) {
                    currentPos = worldPosition.above();
                    //if the signal is a cross-signal, and this semaphore is at the bottom of the stack,
                    //count upwards to find other semaphores. if one is found this semaphore becomes caution-type
                    for (int j = i + 1; j < 16; j++) {
                        blockState = level.getBlockState(currentPos);
                        blockEntity = level.getBlockEntity(currentPos);
                        if (blockEntity instanceof SemaphoreBlockEntity) {
                            isDistantSignal = true;
                            break;
                        }
                        if (!CRTags.AllBlockTags.SEMAPHORE_POLES.matches(blockState)) {
                            break;
                        }
                        currentPos = currentPos.above();
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
            currentPos = currentPos.below();
        }
    }
}
