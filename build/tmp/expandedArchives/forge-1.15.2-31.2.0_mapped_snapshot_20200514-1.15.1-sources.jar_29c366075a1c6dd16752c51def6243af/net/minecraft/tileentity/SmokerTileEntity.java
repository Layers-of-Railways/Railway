package net.minecraft.tileentity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SmokerTileEntity extends AbstractFurnaceTileEntity {
   public SmokerTileEntity() {
      super(TileEntityType.SMOKER, IRecipeType.SMOKING);
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.smoker");
   }

   protected int getBurnTime(ItemStack fuel) {
      return super.getBurnTime(fuel) / 2;
   }

   protected Container createMenu(int id, PlayerInventory player) {
      return new SmokerContainer(id, player, this, this.furnaceData);
   }
}