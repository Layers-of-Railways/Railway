package net.minecraft.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

public class FrostWalkerEnchantment extends Enchantment {
   public FrostWalkerEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.ARMOR_FEET, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return enchantmentLevel * 10;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 2;
   }

   public static void freezeNearby(LivingEntity living, World worldIn, BlockPos pos, int level) {
      if (living.onGround) {
         BlockState blockstate = Blocks.FROSTED_ICE.getDefaultState();
         float f = (float)Math.min(16, 2 + level);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add((double)(-f), -1.0D, (double)(-f)), pos.add((double)f, -1.0D, (double)f))) {
            if (blockpos.withinDistance(living.getPositionVec(), (double)f)) {
               blockpos$mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
               BlockState blockstate1 = worldIn.getBlockState(blockpos$mutable);
               if (blockstate1.isAir(worldIn, blockpos$mutable)) {
                  BlockState blockstate2 = worldIn.getBlockState(blockpos);
                  boolean isFull = blockstate2.getBlock() == Blocks.WATER && blockstate2.get(FlowingFluidBlock.LEVEL) == 0; //TODO: Forge, modded waters?
                  if (blockstate2.getMaterial() == Material.WATER && isFull && blockstate.isValidPosition(worldIn, blockpos) && worldIn.func_226663_a_(blockstate, blockpos, ISelectionContext.dummy()) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(living, new net.minecraftforge.common.util.BlockSnapshot(worldIn, blockpos, blockstate2), net.minecraft.util.Direction.UP)) {
                     worldIn.setBlockState(blockpos, blockstate);
                     worldIn.getPendingBlockTicks().scheduleTick(blockpos, Blocks.FROSTED_ICE, MathHelper.nextInt(living.getRNG(), 60, 120));
                  }
               }
            }
         }

      }
   }

   /**
    * Determines if the enchantment passed can be applyied together with this enchantment.
    */
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.DEPTH_STRIDER;
   }
}