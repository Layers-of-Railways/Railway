package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(ResourceLocation p_i48164_1_) {
      super(p_i48164_1_, "", 3, 3, NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.FILLED_MAP), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER)), new ItemStack(Items.MAP));
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      if (!super.matches(inv, worldIn)) {
         return false;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;

         for(int i = 0; i < inv.getSizeInventory() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if (itemstack1.getItem() == Items.FILLED_MAP) {
               itemstack = itemstack1;
            }
         }

         if (itemstack.isEmpty()) {
            return false;
         } else {
            MapData mapdata = FilledMapItem.getMapData(itemstack, worldIn);
            if (mapdata == null) {
               return false;
            } else if (this.isExplorationMap(mapdata)) {
               return false;
            } else {
               return mapdata.scale < 4;
            }
         }
      }
   }

   private boolean isExplorationMap(MapData p_190934_1_) {
      if (p_190934_1_.mapDecorations != null) {
         for(MapDecoration mapdecoration : p_190934_1_.mapDecorations.values()) {
            if (mapdecoration.getType() == MapDecoration.Type.MANSION || mapdecoration.getType() == MapDecoration.Type.MONUMENT) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory() && itemstack.isEmpty(); ++i) {
         ItemStack itemstack1 = inv.getStackInSlot(i);
         if (itemstack1.getItem() == Items.FILLED_MAP) {
            itemstack = itemstack1;
         }
      }

      itemstack = itemstack.copy();
      itemstack.setCount(1);
      itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
      return itemstack;
   }

   /**
    * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
    * doLimitedCrafting gamerule)
    */
   public boolean isDynamic() {
      return true;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_MAPEXTENDING;
   }
}