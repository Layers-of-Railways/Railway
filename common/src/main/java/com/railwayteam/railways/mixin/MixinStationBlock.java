package com.railwayteam.railways.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
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
        if (!pLevel.isClientSide && pPlayer instanceof DeployerFakePlayer deployerFakePlayer && pLevel.getBlockEntity(pPos) instanceof StationBlockEntity stationBe) {
            cir.setReturnValue(InteractionResult.CONSUME);
            GlobalStation station = stationBe.getStation();
            boolean isAssemblyMode = pState.getValue(StationBlock.ASSEMBLING);
            if (station != null && station.getPresentTrain() == null) {
                //assemble
                if (stationBe.isAssembling() || stationBe.tryEnterAssemblyMode()) {
                    //Need to fix blockstate
                    stationBe.assemble(deployerFakePlayer.getUUID());
                    cir.setReturnValue(InteractionResult.SUCCESS);

                    if (isAssemblyMode) {
                        pLevel.setBlock(pPos, pState.setValue(StationBlock.ASSEMBLING, false), 3);
                        stationBe.refreshBlockState();
                    }
                }
                return;
            }
            BlockState newState = null;
            if (!isAssemblyMode) {
                newState = pState.setValue(StationBlock.ASSEMBLING, true);
            }
            if (disassembleAndEnterMode(deployerFakePlayer, stationBe)) {
                if (newState != null) {
                    pLevel.setBlock(pPos, newState, 3);
                    stationBe.refreshBlockState();

                    stationBe.refreshAssemblyInfo();
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    private boolean disassembleAndEnterMode(ServerPlayer sender, StationBlockEntity te) {
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

    private void dropSchedule(ServerPlayer sender, StationBlockEntity te, ItemStack schedule) {
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
