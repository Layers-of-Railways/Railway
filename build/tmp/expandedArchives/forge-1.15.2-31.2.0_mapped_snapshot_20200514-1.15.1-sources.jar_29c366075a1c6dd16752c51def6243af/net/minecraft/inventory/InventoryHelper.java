package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   private static final Random RANDOM = new Random();

   public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
      dropInventoryItems(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
   }

   public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
      dropInventoryItems(worldIn, entityAt.getPosX(), entityAt.getPosY(), entityAt.getPosZ(), inventory);
   }

   private static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory) {
      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         spawnItemStack(worldIn, x, y, z, inventory.getStackInSlot(i));
      }

   }

   public static void dropItems(World p_219961_0_, BlockPos p_219961_1_, NonNullList<ItemStack> p_219961_2_) {
      p_219961_2_.forEach((p_219962_2_) -> {
         spawnItemStack(p_219961_0_, (double)p_219961_1_.getX(), (double)p_219961_1_.getY(), (double)p_219961_1_.getZ(), p_219962_2_);
      });
   }

   public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
      double d0 = (double)EntityType.ITEM.getWidth();
      double d1 = 1.0D - d0;
      double d2 = d0 / 2.0D;
      double d3 = Math.floor(x) + RANDOM.nextDouble() * d1 + d2;
      double d4 = Math.floor(y) + RANDOM.nextDouble() * d1;
      double d5 = Math.floor(z) + RANDOM.nextDouble() * d1 + d2;

      while(!stack.isEmpty()) {
         ItemEntity itementity = new ItemEntity(worldIn, d3, d4, d5, stack.split(RANDOM.nextInt(21) + 10));
         float f = 0.05F;
         itementity.setMotion(RANDOM.nextGaussian() * (double)0.05F, RANDOM.nextGaussian() * (double)0.05F + (double)0.2F, RANDOM.nextGaussian() * (double)0.05F);
         worldIn.addEntity(itementity);
      }

   }
}