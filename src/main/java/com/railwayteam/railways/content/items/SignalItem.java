package com.railwayteam.railways.content.items;

import com.railwayteam.railways.content.blocks.AbstractLargeTrackBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SignalItem extends BlockItem {
  public static final String NAME = "signal_item";
  public static final String TAG  = "targetPos";

  public SignalItem(Block block, Properties props) {
    super(block, props);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    if (world.getBlockState(pos).getBlock() instanceof AbstractLargeTrackBlock) {
      CompoundNBT tag = context.getItem().getOrCreateTag();
      if (tag.contains(TAG)) {
        // clear the old selection so we don't display a bunch... is that necessary? TODO render signal target
      }
      tag.put(TAG, NBTUtil.writeBlockPos(pos));
    //  context.getPlayer().sendMessage(new StringTextComponent("Signal target set"));
      return ActionResultType.SUCCESS;
    }
    return super.onItemUse(context);
  }

  @Override
  protected boolean onBlockPlaced (BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState state) {
    if (world.isRemote) {
      // clear display of target location
    }
    return super.onBlockPlaced(pos, world, player, stack, state);
  }

  @Override
  public boolean canPlayerBreakBlockWhileHolding (BlockState state, World world, BlockPos pos, PlayerEntity player) {
    return !(state.getBlock() instanceof AbstractLargeTrackBlock);
  }
}
