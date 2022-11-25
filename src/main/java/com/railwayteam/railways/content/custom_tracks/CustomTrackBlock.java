package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CustomTrackBlock extends TrackBlock implements IHasTrackMaterial {

  protected final TrackMaterial material;

  public CustomTrackBlock(Properties properties, TrackMaterial material) {
    super(properties);
    this.material = material;
  }

  @Override
  public TrackMaterial getMaterial() {
    return this.material;
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack handStack = player.getItemInHand(hand);
    if (handStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock slabBlock) {
      if (world.isClientSide) return InteractionResult.SUCCESS;
      SlabBlock currentCasing = IHasTrackCasing.getTrackCasing(world, pos);
      handStack.shrink(1);
      if (currentCasing != null) {
        ItemStack casingStack = new ItemStack(currentCasing);
        if (handStack.isEmpty()) {
          handStack = casingStack;
        } else if (!player.addItem(casingStack)) {
          player.drop(casingStack, false);
        }
      }
      player.setItemInHand(hand, handStack);
      IHasTrackCasing.setTrackCasing(world, pos, slabBlock);
      return InteractionResult.SUCCESS;
    } else if (handStack.isEmpty()) {
      SlabBlock currentCasing = IHasTrackCasing.getTrackCasing(world, pos);
      if (currentCasing != null) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        handStack = new ItemStack(currentCasing);
        IHasTrackCasing.setTrackCasing(world, pos, null);
        player.setItemInHand(hand, handStack);
        return InteractionResult.SUCCESS;
      }
    }
    return super.use(state, world, pos, player, hand, hit);
  }
}
