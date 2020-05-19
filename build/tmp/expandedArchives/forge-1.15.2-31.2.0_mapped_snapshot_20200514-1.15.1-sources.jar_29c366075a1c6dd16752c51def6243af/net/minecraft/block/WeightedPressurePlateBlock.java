package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   private final int maxWeight;

   protected WeightedPressurePlateBlock(int p_i48295_1_, Block.Properties p_i48295_2_) {
      super(p_i48295_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWER, Integer.valueOf(0)));
      this.maxWeight = p_i48295_1_;
   }

   protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
      int i = Math.min(worldIn.getEntitiesWithinAABB(Entity.class, PRESSURE_AABB.offset(pos)).size(), this.maxWeight);
      if (i > 0) {
         float f = (float)Math.min(this.maxWeight, i) / (float)this.maxWeight;
         return MathHelper.ceil(f * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
      worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
      worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
   }

   protected int getRedstoneStrength(BlockState state) {
      return state.get(POWER);
   }

   protected BlockState setRedstoneStrength(BlockState state, int strength) {
      return state.with(POWER, Integer.valueOf(strength));
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return 10;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(POWER);
   }
}