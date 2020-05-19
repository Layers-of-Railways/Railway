package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
    */
   public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
      if (!worldIn.isRemote) {
         stack.damageItem(1, entityLiving, (p_220036_0_) -> {
            p_220036_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
         });
      }

      Block block = state.getBlock();
      return !state.isIn(BlockTags.LEAVES) && block != Blocks.COBWEB && block != Blocks.GRASS && block != Blocks.FERN && block != Blocks.DEAD_BUSH && block != Blocks.VINE && block != Blocks.TRIPWIRE && !block.isIn(BlockTags.WOOL) ? super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving) : true;
   }

   /**
    * Check whether this Item can harvest the given Block
    */
   public boolean canHarvestBlock(BlockState blockIn) {
      Block block = blockIn.getBlock();
      return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack stack, BlockState state) {
      Block block = state.getBlock();
      if (block != Blocks.COBWEB && !state.isIn(BlockTags.LEAVES)) {
         return block.isIn(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(stack, state);
      } else {
         return 15.0F;
      }
   }

   /**
    * Returns true if the item can be used on the given entity, e.g. shears on sheep.
    */
   @SuppressWarnings("deprecation")
   @Override
   public boolean itemInteractionForEntity(ItemStack stack, net.minecraft.entity.player.PlayerEntity playerIn, LivingEntity entity, net.minecraft.util.Hand hand) {
      if (entity.world.isRemote) return false;
      if (entity instanceof net.minecraftforge.common.IShearable) {
         net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
         BlockPos pos = new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());
         if (target.isShearable(stack, entity.world, pos)) {
            java.util.List<ItemStack> drops = target.onSheared(stack, entity.world, pos,
                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, stack));
            java.util.Random rand = new java.util.Random();
            drops.forEach(d -> {
               net.minecraft.entity.item.ItemEntity ent = entity.entityDropItem(d, 1.0F);
               ent.setMotion(ent.getMotion().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
            });
            stack.damageItem(1, entity, e -> e.sendBreakAnimation(hand));
         }
         return true;
      }
      return false;
   }
}