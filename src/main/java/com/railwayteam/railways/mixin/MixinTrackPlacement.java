package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.BlockStateUtils;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import com.simibubi.create.foundation.utility.Pair;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
public abstract class MixinTrackPlacement { //TODO _track api ALL OF IT (totally done)
  @Inject(method = "tryConnect", at = @At(value = "FIELD", // DONE
      opcode = Opcodes.PUTFIELD,
      target = "Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;curve:Lcom/simibubi/create/content/logistics/trains/BezierConnection;",
      ordinal = 0, shift = At.Shift.AFTER),
      locals = LocalCapture.CAPTURE_FAILEXCEPTION)
  private static void setupMaterial_0(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, int maxLength, TrackPlacement.PlacementInfo info, ITrackBlock track, Pair nearestTrackAxis, Vec3 axis2, Vec3 normal2, Vec3 normedAxis2, Vec3 end2, CompoundTag itemTag, CompoundTag selectionTag, BlockPos pos1, Vec3 axis1, Vec3 normedAxis1, Vec3 end1, Vec3 normal1, boolean front1, BlockState state1, double[] intersect, boolean parallel, boolean skipCurve, Vec3 cross2, double a1, double a2, double angle, double ascend, double absAscend, boolean slope, Vec3 offset1, Vec3 offset2, BlockPos targetPos1, BlockPos targetPos2) {
    setupMaterial(level, player, pos2, state2, stack, girder, maximiseTurn, cir, lookVec, lookAngle, info);
  }

  @Inject(method = "tryConnect", at = @At(value = "FIELD", // DONE
      opcode = Opcodes.PUTFIELD,
      target = "Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;valid:Z",
      ordinal = 0),
      locals = LocalCapture.CAPTURE_FAILEXCEPTION)
  private static void setupMaterial_1(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, int maxLength, TrackPlacement.PlacementInfo info, ITrackBlock track, Pair nearestTrackAxis, Vec3 axis2, Vec3 normal2, Vec3 normedAxis2, Vec3 end2, CompoundTag itemTag, CompoundTag selectionTag, BlockPos pos1, Vec3 axis1, Vec3 normedAxis1, Vec3 end1, Vec3 normal1, boolean front1, BlockState state1, double[] intersect, boolean parallel, boolean skipCurve, Vec3 cross2, double a1, double a2, double angle, double ascend, double absAscend, boolean slope, double dist, Vec3 offset1, Vec3 offset2, BlockPos targetPos1, BlockPos targetPos2) {
          setupMaterial(level, player, pos2, state2, stack, girder, maximiseTurn, cir, lookVec, lookAngle, info);
  }

  // DONE
  private static void setupMaterial(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, TrackPlacement.PlacementInfo info) {
    if (((AccessorTrackPlacement_PlacementInfo) info).getCurve() != null) {
      if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IHasTrackMaterial hasTrackMaterial) {
        ((IHasTrackMaterial) ((AccessorTrackPlacement_PlacementInfo) info).getCurve()).setMaterial(hasTrackMaterial.getMaterial());
      }
    }
  }

  // DONE
  @Inject(method = "tryConnect", at = @At(value = "RETURN", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, slice = @Slice(from = @At(value = "RETURN", ordinal = 1)))
  private static void setupMaterial_2(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, int maxLength, TrackPlacement.PlacementInfo info, ITrackBlock track, Pair nearestTrackAxis, Vec3 axis2, Vec3 normal2, Vec3 normedAxis2, Vec3 end2, CompoundTag itemTag, CompoundTag selectionTag, BlockPos pos1, Vec3 axis1, Vec3 normedAxis1, Vec3 end1, Vec3 normal1, boolean front1, BlockState state1) {
    if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IHasTrackMaterial hasTrackMaterial) {
      ((IHasTrackMaterial) info).setMaterial(hasTrackMaterial.getMaterial());
    } else {
      Railways.LOGGER.info("Weird stack for tryConnect: "+stack);
    }
  }

  // DONE
  @Inject(method = "tryConnect", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target="Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement;placeTracks(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Z)Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, slice = @Slice(from = @At(value = "RETURN", ordinal = 1)))
  private static void setupMaterial_before_place(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, int maxLength, TrackPlacement.PlacementInfo info, ITrackBlock track, Pair nearestTrackAxis, Vec3 axis2, Vec3 normal2, Vec3 normedAxis2, Vec3 end2, CompoundTag itemTag, CompoundTag selectionTag, BlockPos pos1, Vec3 axis1, Vec3 normedAxis1, Vec3 end1, Vec3 normal1, boolean front1, BlockState state1, double[] intersect, boolean parallel, boolean skipCurve, Vec3 cross2, double a1, double a2, double angle, double ascend, double absAscend, boolean slope, double dist, Vec3 offset1, Vec3 offset2, BlockPos targetPos1, BlockPos targetPos2) {
    if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IHasTrackMaterial hasTrackMaterial) {
      ((IHasTrackMaterial) info).setMaterial(hasTrackMaterial.getMaterial());
    } else {
      Railways.LOGGER.info("Weird stack for tryConnect: "+stack);
    }
  }

  private static final ThreadLocal<ItemStack> stackArgument = new ThreadLocal<>(); // DONE

  @Inject(method = "tryConnect", at = @At("HEAD")) // DONE
  private static void saveStack(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    stackArgument.set(stack);
  }

  @Inject(method = "tryConnect", at = @At("RETURN")) // DONE
  private static void resetStack(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    stackArgument.set(null);
  }

  @Redirect(method = "tryConnect", at = @At(value = "INVOKE", opcode = Opcodes.GETSTATIC, //TODO _track api
      target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"))
  private static boolean replaceTrack(BlockEntry<?> instance, ItemStack itemStack) {
    return CRTags.AllBlockTags.TRACKS.matches(stackArgument.get()) && itemStack.is(stackArgument.get().getItem());
  }

  // DONE
  private static final ThreadLocal<TrackPlacement.PlacementInfo> infoArgument = new ThreadLocal<>();

  // DONE
  @Inject(method = "placeTracks", at = @At("HEAD"))
  private static void saveInfo(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    infoArgument.set(info);
  }

  // DONE
  @Inject(method = "placeTracks", at = @At("RETURN"))
  private static void resetInfo(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    infoArgument.set(null);
  }

  // DONE
  @SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
  @ModifyVariable(method = "placeTracks", at = @At(value = "STORE", ordinal = 0), ordinal = 4, require = 1, remap = false)
  private static BlockState modifyToPlace(BlockState value) {
    return BlockStateUtils.trackWith(((IHasTrackMaterial) infoArgument.get()).getMaterial().getTrackBlock().get(), value);
  }

  private static final ThreadLocal<BlockState> relevantState = new ThreadLocal<>();
  private static final ThreadLocal<BlockState> stateAtPosVar = new ThreadLocal<>();
  private static final ThreadLocal<TrackPlacement.PlacementInfo> infoArgument2 = new ThreadLocal<>();

  @Inject(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1, remap = true))
  private static void setRelevantState1(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    relevantState.set(state1);
    infoArgument2.set(info);
  }

  @Inject(method = "placeTracks", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 2, remap = true))
  private static void setRelevantState2(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    relevantState.set(state2);
    infoArgument2.set(info);
  }

  @Redirect(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", remap = true),
      slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1, remap = true)))
  private static BlockState storeStateAtPos(Level level, BlockPos pos) {
    stateAtPosVar.set(level.getBlockState(pos));
    return stateAtPosVar.get();
  }

  @Inject(method = "placeTracks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
      ordinal = 2, shift = At.Shift.AFTER, remap = true))
  private static void resetStates(Level level, TrackPlacement.PlacementInfo info, BlockState state1, BlockState state2, BlockPos targetPos1, BlockPos targetPos2, boolean simulate, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir) {
    relevantState.set(null);
    stateAtPosVar.set(null);
    infoArgument2.set(null);
  }

  @ModifyArg(method = "placeTracks", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/block/ProperWaterloggedBlock;withWater(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
      slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/block/ProperWaterloggedBlock;withWater(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1)), index = 1)
  private static BlockState modifyBaseBlock(BlockState value) {
//    Railways.LOGGER.info("modifyBaseBlock, relevantStatePre: " + relevantState);
    if (infoArgument2.get() == null) {
      return value;
    }
    relevantState.set(BlockStateUtils.trackWith(((IHasTrackMaterial) infoArgument2.get()).getMaterial().getTrackBlock().get(), relevantState.get()));
//    Railways.LOGGER.info("relevantStatePost: " + relevantState + ", stateAtPosVar: " + stateAtPosVar);
    // keep original block if it is already a track!
    return (CRTags.AllBlockTags.TRACKS.matches(stateAtPosVar.get().getBlock()) ? stateAtPosVar.get() : relevantState.get()).setValue(TrackBlock.HAS_TE, true);
  }
}
