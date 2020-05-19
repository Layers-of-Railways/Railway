package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.world.World;

public class ArrowItem extends Item {
   public ArrowItem(Item.Properties builder) {
      super(builder);
   }

   public AbstractArrowEntity createArrow(World worldIn, ItemStack stack, LivingEntity shooter) {
      ArrowEntity arrowentity = new ArrowEntity(worldIn, shooter);
      arrowentity.setPotionEffect(stack);
      return arrowentity;
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.PlayerEntity player) {
      int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.INFINITY, bow);
      return enchant <= 0 ? false : this.getClass() == ArrowItem.class;
   }
}