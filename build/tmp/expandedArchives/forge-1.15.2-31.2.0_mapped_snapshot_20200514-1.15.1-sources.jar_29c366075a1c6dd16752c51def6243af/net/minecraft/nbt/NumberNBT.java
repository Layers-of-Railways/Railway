package net.minecraft.nbt;

public abstract class NumberNBT implements INBT {
   public abstract long getLong();

   public abstract int getInt();

   public abstract short getShort();

   public abstract byte getByte();

   public abstract double getDouble();

   public abstract float getFloat();

   public abstract Number getAsNumber();
}