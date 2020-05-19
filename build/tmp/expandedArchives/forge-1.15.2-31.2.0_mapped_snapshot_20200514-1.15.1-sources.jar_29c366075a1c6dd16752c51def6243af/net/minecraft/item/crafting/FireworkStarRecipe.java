package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class FireworkStarRecipe extends SpecialRecipe {
   private static final Ingredient INGREDIENT_SHAPE = Ingredient.fromItems(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
   private static final Ingredient INGREDIENT_FLICKER = Ingredient.fromItems(Items.DIAMOND);
   private static final Ingredient INGREDIENT_TRAIL = Ingredient.fromItems(Items.GLOWSTONE_DUST);
   private static final Map<Item, FireworkRocketItem.Shape> ITEM_SHAPE_MAP = Util.make(Maps.newHashMap(), (p_209352_0_) -> {
      p_209352_0_.put(Items.FIRE_CHARGE, FireworkRocketItem.Shape.LARGE_BALL);
      p_209352_0_.put(Items.FEATHER, FireworkRocketItem.Shape.BURST);
      p_209352_0_.put(Items.GOLD_NUGGET, FireworkRocketItem.Shape.STAR);
      p_209352_0_.put(Items.SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
      p_209352_0_.put(Items.WITHER_SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
      p_209352_0_.put(Items.CREEPER_HEAD, FireworkRocketItem.Shape.CREEPER);
      p_209352_0_.put(Items.PLAYER_HEAD, FireworkRocketItem.Shape.CREEPER);
      p_209352_0_.put(Items.DRAGON_HEAD, FireworkRocketItem.Shape.CREEPER);
      p_209352_0_.put(Items.ZOMBIE_HEAD, FireworkRocketItem.Shape.CREEPER);
   });
   private static final Ingredient INGREDIENT_GUNPOWDER = Ingredient.fromItems(Items.GUNPOWDER);

   public FireworkStarRecipe(ResourceLocation p_i48166_1_) {
      super(p_i48166_1_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (INGREDIENT_SHAPE.test(itemstack)) {
               if (flag2) {
                  return false;
               }

               flag2 = true;
            } else if (INGREDIENT_TRAIL.test(itemstack)) {
               if (flag4) {
                  return false;
               }

               flag4 = true;
            } else if (INGREDIENT_FLICKER.test(itemstack)) {
               if (flag3) {
                  return false;
               }

               flag3 = true;
            } else if (INGREDIENT_GUNPOWDER.test(itemstack)) {
               if (flag) {
                  return false;
               }

               flag = true;
            } else {
               if (!(itemstack.getItem() instanceof DyeItem)) {
                  return false;
               }

               flag1 = true;
            }
         }
      }

      return flag && flag1;
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
      CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("Explosion");
      FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.SMALL_BALL;
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack1 = inv.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            if (INGREDIENT_SHAPE.test(itemstack1)) {
               fireworkrocketitem$shape = ITEM_SHAPE_MAP.get(itemstack1.getItem());
            } else if (INGREDIENT_TRAIL.test(itemstack1)) {
               compoundnbt.putBoolean("Flicker", true);
            } else if (INGREDIENT_FLICKER.test(itemstack1)) {
               compoundnbt.putBoolean("Trail", true);
            } else if (itemstack1.getItem() instanceof DyeItem) {
               list.add(((DyeItem)itemstack1.getItem()).getDyeColor().getFireworkColor());
            }
         }
      }

      compoundnbt.putIntArray("Colors", list);
      compoundnbt.putByte("Type", (byte)fireworkrocketitem$shape.getIndex());
      return itemstack;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   public ItemStack getRecipeOutput() {
      return new ItemStack(Items.FIREWORK_STAR);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_STAR;
   }
}