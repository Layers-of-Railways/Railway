package com.railwayteam.railways.content.blocks;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.content.items.SignalItem;
import com.railwayteam.railways.content.tiles.tiles.SignalTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class SignalBlock extends Block {
  public static final String name = "basic_signal";

  /*
  private static final VoxelShape SHAPE = Block.makeCuboidShape(
  2d, 0d, 2d,
  14, 14d, 16d
  );
  //*/
  public SignalBlock(Properties props) {
    super(props);
    this.setDefaultState(this.stateContainer.getBaseState()
      .with(BlockStateProperties.POWERED, false)
      .with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
    );
  }

  @Override
  public boolean hasTileEntity(BlockState state) { return true; }

  @Override
  public TileEntity createTileEntity (final BlockState state, final IBlockReader world) {
    return ModSetup.R_TE_SIGNAL.create();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, entity, stack);
    CompoundNBT tag = stack.getOrCreateTag();
    if (tag.contains(SignalItem.TAG) && world.getTileEntity(pos) != null) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof SignalTileEntity) {
        ((SignalTileEntity) te).setTarget(NBTUtil.readBlockPos(tag.getCompound(SignalItem.TAG)));
      }
    }
  }

  @Override
  public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState oldState, IWorld world, BlockPos pos, BlockPos oldPos) {
    boolean power = false;
    if(!world.isRemote()) {
      power = ((ServerWorld) world).isBlockPowered(pos);
    }
    if (state.get(BlockStateProperties.POWERED) != power) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof SignalTileEntity) {
        BlockPos target = ((SignalTileEntity) te).getTarget();
        if (world.getBlockState(target).getBlock() instanceof LargeTrackBlock) {
          world.setBlockState(target, world.getBlockState(target).with(LargeTrackBlock.TRACK_SIDE, // this is just for demonstration
            power ? LargeTrackSide.NORTH_SOUTH : LargeTrackSide.EAST_WEST), 1|2
          );
        }
      }
    }
    return state
      .with(BlockStateProperties.POWERED, power)
    ;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return getDefaultState()
      .with(BlockStateProperties.POWERED, context.getWorld().isBlockPowered(context.getPos()))
      .with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing())
    ;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.POWERED);
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
  }

  @Override
  public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
    return (side != null) && (!side.getAxis().equals(Direction.Axis.Y));
  }
  /*
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }
  //*/
}
