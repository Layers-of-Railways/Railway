package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BellAttachment;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BellBlock extends ContainerBlock {
   public static final DirectionProperty field_220133_a = HorizontalBlock.HORIZONTAL_FACING;
   private static final EnumProperty<BellAttachment> ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final VoxelShape field_220135_c = Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
   private static final VoxelShape field_220136_d = Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   private static final VoxelShape field_220137_e = Block.makeCuboidShape(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
   private static final VoxelShape field_220138_f = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
   private static final VoxelShape field_220139_g = VoxelShapes.or(field_220138_f, field_220137_e);
   private static final VoxelShape field_220140_h = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
   private static final VoxelShape field_220141_i = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
   private static final VoxelShape field_220142_j = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
   private static final VoxelShape field_220143_k = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
   private static final VoxelShape field_220144_w = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
   private static final VoxelShape field_220145_x = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
   private static final VoxelShape field_220146_y = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));

   public BellBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(field_220133_a, Direction.NORTH).with(ATTACHMENT, BellAttachment.FLOOR).with(POWERED, Boolean.valueOf(false)));
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      boolean flag = worldIn.isBlockPowered(pos);
      if (flag != state.get(POWERED)) {
         if (flag) {
            this.func_226885_a_(worldIn, pos, (Direction)null);
         }

         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)), 3);
      }

   }

   public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, Entity projectile) {
      if (projectile instanceof AbstractArrowEntity) {
         Entity entity = ((AbstractArrowEntity)projectile).getShooter();
         PlayerEntity playerentity = entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
         this.func_226884_a_(worldIn, state, hit, playerentity, true);
      }

   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      return this.func_226884_a_(worldIn, state, hit, player, true) ? ActionResultType.SUCCESS : ActionResultType.PASS;
   }

   public boolean func_226884_a_(World p_226884_1_, BlockState p_226884_2_, BlockRayTraceResult p_226884_3_, @Nullable PlayerEntity p_226884_4_, boolean p_226884_5_) {
      Direction direction = p_226884_3_.getFace();
      BlockPos blockpos = p_226884_3_.getPos();
      boolean flag = !p_226884_5_ || this.canRingFrom(p_226884_2_, direction, p_226884_3_.getHitVec().y - (double)blockpos.getY());
      if (flag) {
         boolean flag1 = this.func_226885_a_(p_226884_1_, blockpos, direction);
         if (flag1 && p_226884_4_ != null) {
            p_226884_4_.addStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * Returns true if the bell can be rung from the given side and vertical position. For example, bells attached to
    * their northern neighbor cannot be rung from the south face, since it can't swing north-south.
    */
   private boolean canRingFrom(BlockState p_220129_1_, Direction p_220129_2_, double p_220129_3_) {
      if (p_220129_2_.getAxis() != Direction.Axis.Y && !(p_220129_3_ > (double)0.8124F)) {
         Direction direction = p_220129_1_.get(field_220133_a);
         BellAttachment bellattachment = p_220129_1_.get(ATTACHMENT);
         switch(bellattachment) {
         case FLOOR:
            return direction.getAxis() == p_220129_2_.getAxis();
         case SINGLE_WALL:
         case DOUBLE_WALL:
            return direction.getAxis() != p_220129_2_.getAxis();
         case CEILING:
            return true;
         default:
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean func_226885_a_(World p_226885_1_, BlockPos p_226885_2_, @Nullable Direction p_226885_3_) {
      TileEntity tileentity = p_226885_1_.getTileEntity(p_226885_2_);
      if (!p_226885_1_.isRemote && tileentity instanceof BellTileEntity) {
         if (p_226885_3_ == null) {
            p_226885_3_ = p_226885_1_.getBlockState(p_226885_2_).get(field_220133_a);
         }

         ((BellTileEntity)tileentity).ring(p_226885_3_);
         p_226885_1_.playSound((PlayerEntity)null, p_226885_2_, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShape getShape(BlockState p_220128_1_) {
      Direction direction = p_220128_1_.get(field_220133_a);
      BellAttachment bellattachment = p_220128_1_.get(ATTACHMENT);
      if (bellattachment == BellAttachment.FLOOR) {
         return direction != Direction.NORTH && direction != Direction.SOUTH ? field_220136_d : field_220135_c;
      } else if (bellattachment == BellAttachment.CEILING) {
         return field_220146_y;
      } else if (bellattachment == BellAttachment.DOUBLE_WALL) {
         return direction != Direction.NORTH && direction != Direction.SOUTH ? field_220141_i : field_220140_h;
      } else if (direction == Direction.NORTH) {
         return field_220144_w;
      } else if (direction == Direction.SOUTH) {
         return field_220145_x;
      } else {
         return direction == Direction.EAST ? field_220143_k : field_220142_j;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return this.getShape(state);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return this.getShape(state);
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      Direction direction = context.getFace();
      BlockPos blockpos = context.getPos();
      World world = context.getWorld();
      Direction.Axis direction$axis = direction.getAxis();
      if (direction$axis == Direction.Axis.Y) {
         BlockState blockstate = this.getDefaultState().with(ATTACHMENT, direction == Direction.DOWN ? BellAttachment.CEILING : BellAttachment.FLOOR).with(field_220133_a, context.getPlacementHorizontalFacing());
         if (blockstate.isValidPosition(context.getWorld(), blockpos)) {
            return blockstate;
         }
      } else {
         boolean flag = direction$axis == Direction.Axis.X && world.getBlockState(blockpos.west()).isSolidSide(world, blockpos.west(), Direction.EAST) && world.getBlockState(blockpos.east()).isSolidSide(world, blockpos.east(), Direction.WEST) || direction$axis == Direction.Axis.Z && world.getBlockState(blockpos.north()).isSolidSide(world, blockpos.north(), Direction.SOUTH) && world.getBlockState(blockpos.south()).isSolidSide(world, blockpos.south(), Direction.NORTH);
         BlockState blockstate1 = this.getDefaultState().with(field_220133_a, direction.getOpposite()).with(ATTACHMENT, flag ? BellAttachment.DOUBLE_WALL : BellAttachment.SINGLE_WALL);
         if (blockstate1.isValidPosition(context.getWorld(), context.getPos())) {
            return blockstate1;
         }

         boolean flag1 = world.getBlockState(blockpos.down()).isSolidSide(world, blockpos.down(), Direction.UP);
         blockstate1 = blockstate1.with(ATTACHMENT, flag1 ? BellAttachment.FLOOR : BellAttachment.CEILING);
         if (blockstate1.isValidPosition(context.getWorld(), context.getPos())) {
            return blockstate1;
         }
      }

      return null;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      BellAttachment bellattachment = stateIn.get(ATTACHMENT);
      Direction direction = func_220131_q(stateIn).getOpposite();
      if (direction == facing && !stateIn.isValidPosition(worldIn, currentPos) && bellattachment != BellAttachment.DOUBLE_WALL) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (facing.getAxis() == stateIn.get(field_220133_a).getAxis()) {
            if (bellattachment == BellAttachment.DOUBLE_WALL && !facingState.isSolidSide(worldIn, facingPos, facing)) {
               return stateIn.with(ATTACHMENT, BellAttachment.SINGLE_WALL).with(field_220133_a, facing.getOpposite());
            }

            if (bellattachment == BellAttachment.SINGLE_WALL && direction.getOpposite() == facing && facingState.isSolidSide(worldIn, facingPos, stateIn.get(field_220133_a))) {
               return stateIn.with(ATTACHMENT, BellAttachment.DOUBLE_WALL);
            }
         }

         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return HorizontalFaceBlock.func_220185_b(worldIn, pos, func_220131_q(state).getOpposite());
   }

   private static Direction func_220131_q(BlockState p_220131_0_) {
      switch((BellAttachment)p_220131_0_.get(ATTACHMENT)) {
      case FLOOR:
         return Direction.UP;
      case CEILING:
         return Direction.DOWN;
      default:
         return p_220131_0_.get(field_220133_a).getOpposite();
      }
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public PushReaction getPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(field_220133_a, ATTACHMENT, POWERED);
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new BellTileEntity();
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}