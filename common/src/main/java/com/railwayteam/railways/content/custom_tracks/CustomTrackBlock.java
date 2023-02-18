package com.railwayteam.railways.content.custom_tracks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.multiloader.EntityUtils;
import com.railwayteam.railways.registry.CRTags;
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
import org.jetbrains.annotations.Nullable;

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

  @Nullable
  public static InteractionResult casingUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack handStack = player.getItemInHand(hand);
    if (handStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock slabBlock &&
        !CRTags.AllBlockTags.TRACK_CASING_BLACKLIST.matches(slabBlock)) {
      if (world.isClientSide) return InteractionResult.SUCCESS;
      SlabBlock currentCasing = IHasTrackCasing.getTrackCasing(world, pos);
      if (currentCasing == slabBlock) {
        return (IHasTrackCasing.setAlternateModel(world, pos, !IHasTrackCasing.isAlternate(world, pos))) ?
            InteractionResult.SUCCESS : InteractionResult.FAIL;
      } else {
        if (!player.isCreative()) {
          handStack.shrink(1);
          player.setItemInHand(hand, handStack);
          if (currentCasing != null) {
            ItemStack casingStack = new ItemStack(currentCasing);
            EntityUtils.givePlayerItem(player, casingStack);
          }
        }
        IHasTrackCasing.setTrackCasing(world, pos, slabBlock);
      }
      return InteractionResult.SUCCESS;
    } else if (handStack.isEmpty()) {
      SlabBlock currentCasing = IHasTrackCasing.getTrackCasing(world, pos);
      if (currentCasing != null) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        handStack = new ItemStack(currentCasing);
        IHasTrackCasing.setTrackCasing(world, pos, null);
        if (!player.isCreative())
          EntityUtils.givePlayerItem(player, handStack);
        return InteractionResult.SUCCESS;
      }
    }
    return null;
  }
}
