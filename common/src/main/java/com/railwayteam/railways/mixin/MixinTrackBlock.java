package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackBlock.class, remap = false)
public abstract class MixinTrackBlock extends Block {
  public MixinTrackBlock(Properties pProperties) {
    super(pProperties);
  }

  @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
  private void extendedUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
    if (!(((Object) this) instanceof MonorailTrackBlock)) {
      InteractionResult result = CustomTrackBlock.casingUse(state, world, pos, player, hand, hit);
      if (result != null) {
        cir.setReturnValue(result);
      }
    }
  }
}
