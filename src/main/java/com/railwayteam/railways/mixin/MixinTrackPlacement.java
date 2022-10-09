package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = TrackPlacement.class, remap = false)
public abstract class MixinTrackPlacement {
  @Inject(method = "tryConnect", at = @At(value = "INVOKE",
      target = "Lcom/simibubi/create/content/logistics/trains/BezierConnection;<init>(Lcom/simibubi/create/foundation/utility/Couple;Lcom/simibubi/create/foundation/utility/Couple;Lcom/simibubi/create/foundation/utility/Couple;Lcom/simibubi/create/foundation/utility/Couple;ZZ)V",
      shift = At.Shift.BY, by=2, ordinal = 0),
      locals = LocalCapture.CAPTURE_FAILEXCEPTION)
  private static void setupMaterial_0(Level level, Player player, BlockPos pos2, BlockState state2, ItemStack stack, boolean girder, boolean maximiseTurn, CallbackInfoReturnable<TrackPlacement.PlacementInfo> cir, Vec3 lookVec, int lookAngle, TrackPlacement.PlacementInfo info) {
    setupMaterial(level, player, pos2, state2, stack, girder, maximiseTurn, cir, lookVec, lookAngle, info);
  }

  @Inject(method = "tryConnect", at = @At(value = "INVOKE",
      target = "Lcom/simibubi/create/content/logistics/trains/BezierConnection;<init>(Lcom/simibubi/create/foundation/utility/Couple;Lcom/simibubi/create/foundation/utility/Couple;Lcom/simibubi/create/foundation/utility/Couple;Lcom/simibubi/create/foundation/utility/Couple;ZZ)V",
      shift = At.Shift.BY, by=4, ordinal = 1),
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
}
