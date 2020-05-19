package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IIntArray;

public class SmokerContainer extends AbstractFurnaceContainer {
   public SmokerContainer(int p_i50061_1_, PlayerInventory p_i50061_2_) {
      super(ContainerType.SMOKER, IRecipeType.SMOKING, p_i50061_1_, p_i50061_2_);
   }

   public SmokerContainer(int p_i50062_1_, PlayerInventory p_i50062_2_, IInventory p_i50062_3_, IIntArray p_i50062_4_) {
      super(ContainerType.SMOKER, IRecipeType.SMOKING, p_i50062_1_, p_i50062_2_, p_i50062_3_, p_i50062_4_);
   }
}