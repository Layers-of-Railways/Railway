package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.BlockStateUtils;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = TrackPlacement.class, remap = false)
public abstract class MixinTrackPlacement {
  @Inject(method = "tryConnect", at = @At(value = "FIELD",
      opcode = Opcodes.PUTFIELD,
      target = "Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;curve:Lcom/simibubi/create/content/logistics/trains/BezierConnection;",
      ordinal = 0, shift = At.Shift.AFTER),
      locals = LocalCapture.CAPTURE_FAILEXCEPTION)
  private static void setupMaterial_0(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, TrackPlacement.PlacementInfo info) {
    setupMaterial(level, player, pos2, state2, stack, girder, maximiseTurn, cir, lookVec, lookAngle, info);
  }

  @Inject(method = "tryConnect", at = @At(value = "FIELD",
      opcode = Opcodes.PUTFIELD,
      target = "Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;valid:Z",
      ordinal = 0),
      locals = LocalCapture.CAPTURE_FAILEXCEPTION)
  private static void setupMaterial_1(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, TrackPlacement.PlacementInfo info) {
    setupMaterial(level, player, pos2, state2, stack, girder, maximiseTurn, cir, lookVec, lookAngle, info);
  }

  private static void setupMaterial(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, TrackPlacement.PlacementInfo info) {
    if (((AccessorTrackPlacement_PlacementInfo) info).getCurve() != null) {
      if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IHasTrackMaterial hasTrackMaterial) {
        ((IHasTrackMaterial) ((AccessorTrackPlacement_PlacementInfo) info).getCurve()).setMaterial(hasTrackMaterial.getMaterial());
      }
    }
  }

  @Inject(method = "tryConnect", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, slice = @Slice(from = @At(value = "RETURN", ordinal = 1)))
  private static void setupMaterial_2(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, TrackPlacement.PlacementInfo info) {
    if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IHasTrackMaterial hasTrackMaterial) {
      ((IHasTrackMaterial) info).setMaterial(hasTrackMaterial.getMaterial());
    }
  }

  private static ItemStack stackArgument;

  @Inject(method = "tryConnect", at = @At("HEAD"))
  private static void saveStack(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    stackArgument = stack;
  }

  @Inject(method = "tryConnect", at = @At("RETURN"))
  private static void resetStack(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    stackArgument = null;
  }

  @Redirect(method = "tryConnect", at = @At(value = "INVOKE", opcode = Opcodes.GETSTATIC,
      target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"))
  private static boolean replaceTrack(BlockEntry<?> instance, ItemStack itemStack) {
    return CRTags.AllBlockTags.TRACKS.matches(stackArgument) && itemStack.is(stackArgument.getItem());
  }

  private static TrackPlacement.PlacementInfo infoArgument = null;

  @Inject(method = "placeTracks", at = @At("HEAD"))
  private static void saveInfo(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    infoArgument = info;
  }

  @Inject(method = "placeTracks", at = @At("RETURN"))
  private static void resetInfo(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    infoArgument = null;
  }

  @SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
  @ModifyVariable(method = "placeTracks", at = @At(value = "STORE", ordinal = 0), ordinal = 4, require = 1, remap = false)
  private static BlockState modifyToPlace(BlockState value) {
    return BlockStateUtils.trackWith(((IHasTrackMaterial) infoArgument).getMaterial().getTrackBlock().get(), value);
  }

  /*@ModifyArg(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), index = 1)
  private static BlockState modifySetBlock(BlockState value) {
    return BlockStateUtils.trackWith(((IHasTrackMaterial) infoArgument).getMaterial().getTrackBlock().get(), value);
  }*/

  private static BlockState relevantState = null;
  private static BlockState stateAtPosVar = null;
  private static TrackPlacement.PlacementInfo infoArgument2 = null;

  @Inject(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1))
  private static void setRelevantState1(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    relevantState = state1;
    infoArgument2 = info;
  }

  @Inject(method = "placeTracks", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 2))
  private static void setRelevantState2(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    relevantState = state2;
    infoArgument2 = info;
  }

  @Redirect(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
      slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1)))
  private static BlockState storeStateAtPos(Level level, BlockPos pos) {
    stateAtPosVar = level.getBlockState(pos);
    return stateAtPosVar;
  }

  @Inject(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 2, shift = At.Shift.AFTER))
  private static void resetStates(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    relevantState = null;
    stateAtPosVar = null;
    infoArgument2 = null;
  }

  @ModifyArg(method = "placeTracks", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/block/ProperWaterloggedBlock;withWater(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
      slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/block/ProperWaterloggedBlock;withWater(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1)), index = 1)
  private static BlockState modifyBaseBlock(BlockState value) {
//    Railways.LOGGER.info("modifyBaseBlock, relevantStatePre: " + relevantState);
    if (infoArgument2 == null) {
      return value;
    }
    relevantState = BlockStateUtils.trackWith(((IHasTrackMaterial) infoArgument2).getMaterial().getTrackBlock().get(), relevantState);
//    Railways.LOGGER.info("relevantStatePost: " + relevantState + ", stateAtPosVar: " + stateAtPosVar);
    return (CRTags.AllBlockTags.TRACKS.matches(stateAtPosVar.getBlock()) ? stateAtPosVar : relevantState).setValue(TrackBlock.HAS_TE, true);
  }
}
