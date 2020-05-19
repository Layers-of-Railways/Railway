package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SweetBerryBushBlock extends BushBlock implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;
   private static final VoxelShape field_220126_b = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
   private static final VoxelShape field_220127_c = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public SweetBerryBushBlock(Block.Properties p_i49971_1_) {
      super(p_i49971_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(Items.SWEET_BERRIES);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      if (state.get(AGE) == 0) {
         return field_220126_b;
      } else {
         return state.get(AGE) < 3 ? field_220127_c : super.getShape(state, worldIn, pos, context);
      }
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      super.tick(state, worldIn, pos, rand);
      int i = state.get(AGE);
      if (i < 3 && worldIn.getLightSubtracted(pos.up(), 0) >= 9 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt(5) == 0)) {
         worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
         net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
      }

   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (entityIn instanceof LivingEntity && entityIn.getType() != EntityType.FOX && entityIn.getType() != EntityType.BEE) {
         entityIn.setMotionMultiplier(state, new Vec3d((double)0.8F, 0.75D, (double)0.8F));
         if (!worldIn.isRemote && state.get(AGE) > 0 && (entityIn.lastTickPosX != entityIn.getPosX() || entityIn.lastTickPosZ != entityIn.getPosZ())) {
            double d0 = Math.abs(entityIn.getPosX() - entityIn.lastTickPosX);
            double d1 = Math.abs(entityIn.getPosZ() - entityIn.lastTickPosZ);
            if (d0 >= (double)0.003F || d1 >= (double)0.003F) {
               entityIn.attackEntityFrom(DamageSource.SWEET_BERRY_BUSH, 1.0F);
            }
         }

      }
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      int i = state.get(AGE);
      boolean flag = i == 3;
      if (!flag && player.getHeldItem(handIn).getItem() == Items.BONE_MEAL) {
         return ActionResultType.PASS;
      } else if (i > 1) {
         int j = 1 + worldIn.rand.nextInt(2);
         spawnAsEntity(worldIn, pos, new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0)));
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
         worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(1)), 2);
         return ActionResultType.SUCCESS;
      } else {
         return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
      return state.get(AGE) < 3;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
      int i = Math.min(3, state.get(AGE) + 1);
      worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i)), 2);
   }
}