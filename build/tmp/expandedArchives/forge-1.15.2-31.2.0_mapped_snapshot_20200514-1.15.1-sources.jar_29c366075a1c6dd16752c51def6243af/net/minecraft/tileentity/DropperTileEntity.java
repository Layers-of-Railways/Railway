package net.minecraft.tileentity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DropperTileEntity extends DispenserTileEntity {
   public DropperTileEntity() {
      super(TileEntityType.DROPPER);
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.dropper");
   }
}