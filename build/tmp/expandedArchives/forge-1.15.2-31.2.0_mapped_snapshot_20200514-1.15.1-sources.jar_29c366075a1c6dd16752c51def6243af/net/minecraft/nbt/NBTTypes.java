package net.minecraft.nbt;

public class NBTTypes {
   private static final INBTType<?>[] field_229709_a_ = new INBTType[]{EndNBT.TYPE, ByteNBT.TYPE, ShortNBT.TYPE, IntNBT.TYPE, LongNBT.TYPE, FloatNBT.TYPE, DoubleNBT.TYPE, ByteArrayNBT.TYPE, StringNBT.TYPE, ListNBT.TYPE, CompoundNBT.TYPE, IntArrayNBT.TYPE, LongArrayNBT.TYPE};

   public static INBTType<?> func_229710_a_(int p_229710_0_) {
      return p_229710_0_ >= 0 && p_229710_0_ < field_229709_a_.length ? field_229709_a_[p_229710_0_] : INBTType.func_229707_a_(p_229710_0_);
   }
}