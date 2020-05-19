package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BedBlock extends HorizontalBlock implements ITileEntityProvider {
   public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
   public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
   protected static final VoxelShape field_220176_c = Block.makeCuboidShape(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   protected static final VoxelShape field_220177_d = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
   protected static final VoxelShape field_220178_e = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
   protected static final VoxelShape field_220179_f = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
   protected static final VoxelShape field_220180_g = Block.makeCuboidShape(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape field_220181_h = VoxelShapes.or(field_220176_c, field_220177_d, field_220179_f);
   protected static final VoxelShape field_220182_i = VoxelShapes.or(field_220176_c, field_220178_e, field_220180_g);
   protected static final VoxelShape field_220183_j = VoxelShapes.or(field_220176_c, field_220177_d, field_220178_e);
   protected static final VoxelShape field_220184_k = VoxelShapes.or(field_220176_c, field_220179_f, field_220180_g);
   private final DyeColor color;

   public BedBlock(DyeColor colorIn, Block.Properties properties) {
      super(properties);
      this.color = colorIn;
      this.setDefaultState(this.stateContainer.getBaseState().with(PART, BedPart.FOOT).with(OCCUPIED, Boolean.valueOf(false)));
   }

   /**
    * Get the MapColor for this Block and the given BlockState
    * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public MaterialColor getMaterialColor(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(PART) == BedPart.FOOT ? this.color.getMapColor() : MaterialColor.WOOL;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Direction func_220174_a(IBlockReader p_220174_0_, BlockPos p_220174_1_) {
      BlockState blockstate = p_220174_0_.getBlockState(p_220174_1_);
      return blockstate.getBlock() instanceof BedBlock ? blockstate.get(HORIZONTAL_FACING) : null;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.CONSUME;
      } else {
         if (state.get(PART) != BedPart.HEAD) {
            pos = pos.offset(state.get(HORIZONTAL_FACING));
            state = worldIn.getBlockState(pos);
            if (state.getBlock() != this) {
               return ActionResultType.CONSUME;
            }
         }

         net.minecraftforge.common.extensions.IForgeDimension.SleepResult sleepResult = worldIn.dimension.canSleepAt(player, pos);
         if (sleepResult != net.minecraftforge.common.extensions.IForgeDimension.SleepResult.BED_EXPLODES) {
            if (sleepResult == net.minecraftforge.common.extensions.IForgeDimension.SleepResult.DENY) return ActionResultType.SUCCESS;
            if (state.get(OCCUPIED)) {
               if (!this.func_226861_a_(worldIn, pos)) {
                  player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.occupied"), true);
               }

               return ActionResultType.SUCCESS;
            } else {
               player.trySleep(pos).ifLeft((p_220173_1_) -> {
                  if (p_220173_1_ != null) {
                     player.sendStatusMessage(p_220173_1_.getMessage(), true);
                  }

               });
               return ActionResultType.SUCCESS;
            }
         } else {
            worldIn.removeBlock(pos, false);
            BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
            if (worldIn.getBlockState(blockpos).getBlock() == this) {
               worldIn.removeBlock(blockpos, false);
            }

            worldIn.createExplosion((Entity)null, DamageSource.netherBedExplosion(), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
            return ActionResultType.SUCCESS;
         }
      }
   }

   private boolean func_226861_a_(World p_226861_1_, BlockPos p_226861_2_) {
      List<VillagerEntity> list = p_226861_1_.getEntitiesWithinAABB(VillagerEntity.class, new AxisAlignedBB(p_226861_2_), LivingEntity::isSleeping);
      if (list.isEmpty()) {
         return false;
      } else {
         list.get(0).wakeUp();
         return true;
      }
   }

   /**
    * Block's chance to react to a living entity falling on it.
    */
   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.5F);
   }

   /**
    * Called when an Entity lands on this Block. This method *must* update motionY because the entity will not do that
    * on its own
    */
   public void onLanded(IBlockReader worldIn, Entity entityIn) {
      if (entityIn.isSuppressingBounce()) {
         super.onLanded(worldIn, entityIn);
      } else {
         this.func_226860_a_(entityIn);
      }

   }

   private void func_226860_a_(Entity p_226860_1_) {
      Vec3d vec3d = p_226860_1_.getMotion();
      if (vec3d.y < 0.0D) {
         double d0 = p_226860_1_ instanceof LivingEntity ? 1.0D : 0.8D;
         p_226860_1_.setMotion(vec3d.x, -vec3d.y * (double)0.66F * d0, vec3d.z);
      }

   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(HORIZONTAL_FACING))) {
         return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn.with(OCCUPIED, facingState.get(OCCUPIED)) : Blocks.AIR.getDefaultState();
      } else {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   /**
    * Given a bed part and the direction it's facing, find the direction to move to get the other bed part
    */
   private static Direction getDirectionToOther(BedPart p_208070_0_, Direction p_208070_1_) {
      return p_208070_0_ == BedPart.FOOT ? p_208070_1_ : p_208070_1_.getOpposite();
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      BedPart bedpart = state.get(PART);
      BlockPos blockpos = pos.offset(getDirectionToOther(bedpart, state.get(HORIZONTAL_FACING)));
      BlockState blockstate = worldIn.getBlockState(blockpos);
      if (blockstate.getBlock() == this && blockstate.get(PART) != bedpart) {
         worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
         if (!worldIn.isRemote && !player.isCreative()) {
            ItemStack itemstack = player.getHeldItemMainhand();
            spawnDrops(state, worldIn, pos, (TileEntity)null, player, itemstack);
            spawnDrops(blockstate, worldIn, blockpos, (TileEntity)null, player, itemstack);
         }

         player.addStat(Stats.BLOCK_MINED.get(this));
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      Direction direction = context.getPlacementHorizontalFacing();
      BlockPos blockpos = context.getPos();
      BlockPos blockpos1 = blockpos.offset(direction);
      return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, direction) : null;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      Direction direction = func_226862_h_(state).getOpposite();
      switch(direction) {
      case NORTH:
         return field_220181_h;
      case SOUTH:
         return field_220182_i;
      case WEST:
         return field_220183_j;
      default:
         return field_220184_k;
      }
   }

   public static Direction func_226862_h_(BlockState p_226862_0_) {
      Direction direction = p_226862_0_.get(HORIZONTAL_FACING);
      return p_226862_0_.get(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
   }

   @OnlyIn(Dist.CLIENT)
   public static TileEntityMerger.Type func_226863_i_(BlockState p_226863_0_) {
      BedPart bedpart = p_226863_0_.get(PART);
      return bedpart == BedPart.HEAD ? TileEntityMerger.Type.FIRST : TileEntityMerger.Type.SECOND;
   }

   public static Optional<Vec3d> func_220172_a(EntityType<?> p_220172_0_, IWorldReader p_220172_1_, BlockPos p_220172_2_, int p_220172_3_) {
      Direction direction = p_220172_1_.getBlockState(p_220172_2_).get(HORIZONTAL_FACING);
      int i = p_220172_2_.getX();
      int j = p_220172_2_.getY();
      int k = p_220172_2_.getZ();

      for(int l = 0; l <= 1; ++l) {
         int i1 = i - direction.getXOffset() * l - 1;
         int j1 = k - direction.getZOffset() * l - 1;
         int k1 = i1 + 2;
         int l1 = j1 + 2;

         for(int i2 = i1; i2 <= k1; ++i2) {
            for(int j2 = j1; j2 <= l1; ++j2) {
               BlockPos blockpos = new BlockPos(i2, j, j2);
               Optional<Vec3d> optional = func_220175_a(p_220172_0_, p_220172_1_, blockpos);
               if (optional.isPresent()) {
                  if (p_220172_3_ <= 0) {
                     return optional;
                  }

                  --p_220172_3_;
               }
            }
         }
      }

      return Optional.empty();
   }

   protected static Optional<Vec3d> func_220175_a(EntityType<?> p_220175_0_, IWorldReader p_220175_1_, BlockPos p_220175_2_) {
      VoxelShape voxelshape = p_220175_1_.getBlockState(p_220175_2_).getCollisionShape(p_220175_1_, p_220175_2_);
      if (voxelshape.getEnd(Direction.Axis.Y) > 0.4375D) {
         return Optional.empty();
      } else {
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_220175_2_);

         while(blockpos$mutable.getY() >= 0 && p_220175_2_.getY() - blockpos$mutable.getY() <= 2 && p_220175_1_.getBlockState(blockpos$mutable).getCollisionShape(p_220175_1_, blockpos$mutable).isEmpty()) {
            blockpos$mutable.move(Direction.DOWN);
         }

         VoxelShape voxelshape1 = p_220175_1_.getBlockState(blockpos$mutable).getCollisionShape(p_220175_1_, blockpos$mutable);
         if (voxelshape1.isEmpty()) {
            return Optional.empty();
         } else {
            double d0 = (double)blockpos$mutable.getY() + voxelshape1.getEnd(Direction.Axis.Y) + 2.0E-7D;
            if ((double)p_220175_2_.getY() - d0 > 2.0D) {
               return Optional.empty();
            } else {
               float f = p_220175_0_.getWidth() / 2.0F;
               Vec3d vec3d = new Vec3d((double)blockpos$mutable.getX() + 0.5D, d0, (double)blockpos$mutable.getZ() + 0.5D);
               return p_220175_1_.hasNoCollisions(new AxisAlignedBB(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + (double)p_220175_0_.getHeight(), vec3d.z + (double)f)) ? Optional.of(vec3d) : Optional.empty();
            }
         }
      }
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public PushReaction getPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HORIZONTAL_FACING, PART, OCCUPIED);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new BedTileEntity(this.color);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      if (!worldIn.isRemote) {
         BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING));
         worldIn.setBlockState(blockpos, state.with(PART, BedPart.HEAD), 3);
         worldIn.notifyNeighbors(pos, Blocks.AIR);
         state.updateNeighbors(worldIn, pos, 3);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      return this.color;
   }

   /**
    * Return a random long to be passed to {@link IBakedModel#getQuads}, used for random model rotations
    */
   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(BlockState state, BlockPos pos) {
      BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
      return MathHelper.getCoordinateRandom(blockpos.getX(), pos.getY(), blockpos.getZ());
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}