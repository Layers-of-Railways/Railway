package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationBlock;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StationBlock.class, remap = false)
public abstract class MixinStationBlock {
    @Inject(method = "use", at = @At(value = "RETURN", ordinal = 1), cancellable = true, remap = true)
    private void deployersAssemble(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!pLevel.isClientSide && pPlayer instanceof DeployerFakePlayer deployerFakePlayer && pLevel.getBlockEntity(pPos) instanceof StationTileEntity stationTe) {
            cir.setReturnValue(InteractionResult.CONSUME);
            GlobalStation station = stationTe.getStation();
            boolean isAssemblyMode = pState.getValue(StationBlock.ASSEMBLING);
            if (station != null && station.getPresentTrain() == null) {
                //assemble
                if (stationTe.isAssembling() || stationTe.tryEnterAssemblyMode()) {
                    //Need to fix blockstate
                    stationTe.assemble(deployerFakePlayer.getUUID());
                    cir.setReturnValue(InteractionResult.SUCCESS);

                    if (isAssemblyMode) {
                        pLevel.setBlock(pPos, pState.setValue(StationBlock.ASSEMBLING, false), 3);
                        stationTe.refreshBlockState();
                    }
                }
                return;
            }
            BlockState newState = null;
            if (!isAssemblyMode) {
                newState = pState.setValue(StationBlock.ASSEMBLING, true);
            }
            if (disassembleAndEnterMode(deployerFakePlayer, stationTe)) {
                if (newState != null) {
                    pLevel.setBlock(pPos, newState, 3);
                    stationTe.refreshBlockState();

                    stationTe.refreshAssemblyInfo();
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    private boolean disassembleAndEnterMode(ServerPlayer sender, StationTileEntity te) {
        GlobalStation station = te.getStation();
        if (station != null) {
            Train train = station.getPresentTrain();
            BlockPos trackPosition = te.edgePoint.getGlobalPosition();
            ItemStack schedule = train == null ? ItemStack.EMPTY : train.runtime.returnSchedule();
            if (train != null && !train.disassemble(te.getAssemblyDirection(), trackPosition.above()))
                return false;
            dropSchedule(sender, te, schedule);
        }
        return te.tryEnterAssemblyMode();
    }

    private void dropSchedule(ServerPlayer sender, StationTileEntity te, ItemStack schedule) {
        if (schedule.isEmpty())
            return;
        if (sender.getMainHandItem()
            .isEmpty()) {
            sender.getInventory()
                .placeItemBackInInventory(schedule);
            return;
        }
        Vec3 v = VecHelper.getCenterOf(te.getBlockPos());
        ItemEntity itemEntity = new ItemEntity(te.getLevel(), v.x, v.y, v.z, schedule);
        itemEntity.setDeltaMovement(Vec3.ZERO);
        te.getLevel()
            .addFreshEntity(itemEntity);
    }
}
