package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_bogeys.monobogey.IPotentiallyUpsideDownBogey;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.logistics.trains.*;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraption;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mixin(value = StationTileEntity.class, remap = false)
public abstract class MixinStationTileEntity extends SmartTileEntity {
    @Shadow
    int[] bogeyLocations;

    @Shadow
    IBogeyBlock[] bogeyTypes;

    @Shadow
    Direction assemblyDirection;
    boolean[] upsideDownBogeys;

    private boolean bogeyIndexAdd = false;

    private MixinStationTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "refreshAssemblyInfo", at = @At("HEAD"))
    private void resetBogeyIndexAdditionsHead(CallbackInfo ci) {
        bogeyIndexAdd = false;
    }

    @Inject(method = "refreshAssemblyInfo", at = @At("RETURN"))
    private void resetBogeyIndexAdditionsReturn(CallbackInfo ci) {
        bogeyIndexAdd = false;
    }

    @ModifyVariable(method = "refreshAssemblyInfo", at = @At(value = "LOAD"), name = "bogeyIndex")
    private int getRealBogeyIndex(int value) {
        int newValue = value + (bogeyIndexAdd ? 1 : 0);
        bogeyIndexAdd = false;
        return newValue;
    }

    @ModifyVariable(method = "refreshAssemblyInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;move(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;", ordinal = 1, shift = At.Shift.AFTER),
        name = "bogeyIndex")
    private int setRealBogeyIndex(int value) {
        int newValue = value + (bogeyIndexAdd ? 1 : 0);
        bogeyIndexAdd = false;
        return newValue;
    }

    @Inject(method = "refreshAssemblyInfo", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;fill([II)V"))
    private void resetUpsideDownBogeyList(CallbackInfo ci) {
        if (upsideDownBogeys == null)
            upsideDownBogeys = new boolean[AllConfigs.SERVER.trains.maxBogeyCount.get()];
        Arrays.fill(upsideDownBogeys, false);
    }

    @Redirect(method = "refreshAssemblyInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private Block preventUpsideDownBogeyOnTop(BlockState instance) {
        Block block = instance.getBlock();
        return (block instanceof IPotentiallyUpsideDownBogey pubd && pubd.isUpsideDown()) ? Blocks.AIR : block;
    }

    @Inject(method = "refreshAssemblyInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;move(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void includeUpsideDownBogeys(CallbackInfo ci, int prevLength, BlockPos targetPosition, BlockState trackState, ITrackBlock track, BlockPos.MutableBlockPos currentPos, BlockPos bogeyOffset, int MAX_LENGTH, int MAX_BOGEY_COUNT, int bogeyIndex, int maxBogeyCount, int i, BlockState potentialBogeyState) {
        if (potentialBogeyState.getBlock() instanceof IBogeyBlock bogey && !(potentialBogeyState.getBlock() instanceof IPotentiallyUpsideDownBogey pubd && pubd.isUpsideDown()) && bogeyIndex < bogeyLocations.length) {
            /*bogeyTypes[bogeyIndex] = bogey;
            bogeyLocations[bogeyIndex] = i;
            bogeyIndex++;*/
        } else {
            BlockPos upsideDownBogeyOffset = new BlockPos(bogeyOffset.getX(), bogeyOffset.getY() * -1, bogeyOffset.getZ());
            potentialBogeyState = level.getBlockState(upsideDownBogeyOffset.offset(currentPos));
            if (potentialBogeyState.getBlock() instanceof IBogeyBlock bogey && bogey instanceof IPotentiallyUpsideDownBogey potentiallyUpsideDownBogey &&
                potentiallyUpsideDownBogey.isUpsideDown() && bogeyIndex < bogeyLocations.length) {
                bogeyTypes[bogeyIndex] = bogey;
                bogeyLocations[bogeyIndex] = i;
                upsideDownBogeys[bogeyIndex] = true;
                bogeyIndexAdd = true;
            }
        }
    }

    private BlockPos overridenBogeyPosOffset = null;
    private int storedBogeyIdx = 0;

    @Inject(method = "assemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/CarriageContraption;assemble(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void storeBogeyData(UUID playerUUID, CallbackInfo ci, BlockPos trackPosition, BlockState trackState, ITrackBlock track, BlockPos bogeyOffset, TrackNodeLocation location, Vec3 centre, Collection ends, Vec3 targetOffset, List pointOffsets, int iPrevious, List points, Vec3 directionVec, TrackGraph graph, TrackNode secondNode, List<?> contraptions, List<?> carriages, List<?> spacing, boolean atLeastOneForwardControls, int bogeyIndex) {
        if (upsideDownBogeys[bogeyIndex])
            overridenBogeyPosOffset = trackPosition.offset(new BlockPos(bogeyOffset.getX(), bogeyOffset.getY() * -1, bogeyOffset.getZ()));
        storedBogeyIdx = bogeyIndex;
    }

    @Redirect(method = "assemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/CarriageContraption;assemble(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean assembleCorrectBogey(CarriageContraption instance, Level level, BlockPos bogeyPos) throws AssemblyException {
        boolean success = instance.assemble(level, overridenBogeyPosOffset != null ? overridenBogeyPosOffset.relative(assemblyDirection, bogeyLocations[storedBogeyIdx] + 1) : bogeyPos);
        overridenBogeyPosOffset = null;
        return success;
    }

    @Redirect(method = "assemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/entity/CarriageContraption;getSecondBogeyPos()Lnet/minecraft/core/BlockPos;"))
    private BlockPos storeBogeyDataForOrderCheck(CarriageContraption instance) {
        BlockPos pos = instance.getSecondBogeyPos();
        return pos == null ? null : pos.above((storedBogeyIdx + 1 < upsideDownBogeys.length && upsideDownBogeys[storedBogeyIdx + 1]) ? 2 : 0);
    }

    @Inject(method = "trackClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;", ordinal = 1), cancellable = true)
    private void placeUpsideDownBogey(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState bogeyAnchor;
        if (player.getViewXRot(1.0F) < 0 && (bogeyAnchor = track.getBogeyAnchor(level, pos, state)).getBlock() instanceof IPotentiallyUpsideDownBogey pudb) {
            BlockPos targetPos = pos.offset(new BlockPos(track.getUpNormal(level, pos, state).multiply(-1, -1, -1)));
            if (level.getBlockState(targetPos)
                .getDestroySpeed(level, targetPos) == -1) {
                cir.setReturnValue(false);
                return;
            }

            level.destroyBlock(targetPos, true);

            bogeyAnchor = ProperWaterloggedBlock.withWater(level, pudb.getVersion(bogeyAnchor, true), pos);
            level.setBlock(targetPos, bogeyAnchor, 3);
            player.displayClientMessage(Lang.translateDirect("train_assembly.bogey_created"), true);
            SoundType soundtype = bogeyAnchor.getBlock()
                .getSoundType(state, level, pos, player);
            level.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F,
                soundtype.getPitch() * 0.8F);

            if (!player.isCreative()) {
                ItemStack itemInHand = player.getItemInHand(hand);
                itemInHand.shrink(1);
                if (itemInHand.isEmpty())
                    player.setItemInHand(hand, ItemStack.EMPTY);
            }

            cir.setReturnValue(true);
            return;
        }
    }
}
