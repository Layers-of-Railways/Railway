package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IIntArray;

public class FurnaceContainer extends AbstractFurnaceContainer {
   public FurnaceContainer(int id, PlayerInventory playerInventoryIn) {
      super(ContainerType.FURNACE, IRecipeType.SMELTING, id, playerInventoryIn);
   }

   public FurnaceContainer(int id, PlayerInventory playerInventoryIn, IInventory furnaceInventoryIn, IIntArray p_i50083_4_) {
      super(ContainerType.FURNACE, IRecipeType.SMELTING, id, playerInventoryIn, furnaceInventoryIn, p_i50083_4_);
   }
}