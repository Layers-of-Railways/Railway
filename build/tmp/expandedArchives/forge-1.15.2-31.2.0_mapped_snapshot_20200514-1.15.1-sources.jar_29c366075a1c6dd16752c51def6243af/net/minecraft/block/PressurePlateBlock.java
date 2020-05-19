package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   protected PressurePlateBlock(PressurePlateBlock.Sensitivity sensitivityIn, Block.Properties propertiesIn) {
      super(propertiesIn);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)));
      this.sensitivity = sensitivityIn;
   }

   protected int getRedstoneStrength(BlockState state) {
      return state.get(POWERED) ? 15 : 0;
   }

   protected BlockState setRedstoneStrength(BlockState state, int strength) {
      return state.with(POWERED, Boolean.valueOf(strength > 0));
   }

   protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
      if (this.material == Material.WOOD) {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
      } else {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
      if (this.material == Material.WOOD) {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
      } else {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
      }

   }

   protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
      AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
      List<? extends Entity> list;
      switch(this.sensitivity) {
      case EVERYTHING:
         list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
         break;
      case MOBS:
         list = worldIn.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);
         break;
      default:
         return 0;
      }

      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(POWERED);
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}