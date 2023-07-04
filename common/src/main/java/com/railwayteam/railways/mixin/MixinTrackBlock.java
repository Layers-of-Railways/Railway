package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerServer;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.railwayteam.railways.content.roller_extensions.TrackReplacePaver;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = TrackBlock.class, remap = false)
public abstract class MixinTrackBlock extends Block {
  @Shadow public abstract void tick(BlockState state, ServerLevel level, BlockPos pos, Random p_60465_);

  public MixinTrackBlock(Properties pProperties) {
    super(pProperties);
  }

  @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
  private void extendedUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
    //noinspection ConstantValue
    if (!(((Object) this) instanceof MonorailTrackBlock)) {
      InteractionResult result = CustomTrackBlock.casingUse(state, world, pos, player, hand, hit);
      if (result != null) {
        cir.setReturnValue(result);
      }
    }
  }

  @Inject(method = "getBogeyAnchor", at = @At("HEAD"), cancellable = true)
  private void placeCustomStyle(BlockGetter world, BlockPos pos, BlockState state, CallbackInfoReturnable<BlockState> cir) {
    if (BogeyCategoryHandlerServer.currentPlayer == null)
      return;
    Pair<BogeyStyle, BogeySize> styleData = BogeyCategoryHandlerServer.getStyle(BogeyCategoryHandlerServer.currentPlayer);
    BogeyStyle style = styleData.getFirst();
    BogeySize selectedSize = styleData.getSecond();
    if (style == AllBogeyStyles.STANDARD)
      return;

    BogeySize size = selectedSize != null ? selectedSize : BogeySizes.getAllSizesSmallToLarge().get(0);
    int escape = BogeySizes.getAllSizesSmallToLarge().size();
    while (!style.validSizes().contains(size)) {
      if (escape < 0)
        return;
      size = size.increment();
      escape--;
    }
    Block block = style.getBlockOfSize(size);
    cir.setReturnValue(
            block.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_AXIS,
                            state.getValue(TrackBlock.SHAPE) == TrackShape.XO ? Direction.Axis.X : Direction.Axis.Z)
    );
  }

  @Redirect(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V", remap = true), remap = true)
  private void maybeMakeTickInstant(Level instance, BlockPos blockPos, Block block, int i) {
    if (TrackReplacePaver.tickInstantly)
      i = 0;
    instance.scheduleTick(blockPos, block, i);
  }
}
