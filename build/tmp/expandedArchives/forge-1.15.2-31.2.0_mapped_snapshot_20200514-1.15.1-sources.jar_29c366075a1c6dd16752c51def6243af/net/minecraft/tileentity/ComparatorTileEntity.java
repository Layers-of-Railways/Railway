package net.minecraft.tileentity;

import net.minecraft.nbt.CompoundNBT;

public class ComparatorTileEntity extends TileEntity {
   private int outputSignal;

   public ComparatorTileEntity() {
      super(TileEntityType.COMPARATOR);
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      compound.putInt("OutputSignal", this.outputSignal);
      return compound;
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.outputSignal = compound.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.outputSignal;
   }

   public void setOutputSignal(int outputSignalIn) {
      this.outputSignal = outputSignalIn;
   }
}