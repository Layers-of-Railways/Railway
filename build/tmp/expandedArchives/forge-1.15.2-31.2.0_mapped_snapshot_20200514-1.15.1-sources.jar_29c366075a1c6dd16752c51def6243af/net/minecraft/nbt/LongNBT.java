package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class LongNBT extends NumberNBT {
   public static final INBTType<LongNBT> TYPE = new INBTType<LongNBT>() {
      public LongNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(128L);
         return LongNBT.valueOf(p_225649_1_.readLong());
      }

      public String func_225648_a_() {
         return "LONG";
      }

      public String func_225650_b_() {
         return "TAG_Long";
      }

      public boolean func_225651_c_() {
         return true;
      }
   };
   private final long data;

   private LongNBT(long data) {
      this.data = data;
   }

   public static LongNBT valueOf(long p_229698_0_) {
      return p_229698_0_ >= -128L && p_229698_0_ <= 1024L ? LongNBT.Cache.CACHE[(int)p_229698_0_ + 128] : new LongNBT(p_229698_0_);
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeLong(this.data);
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 4;
   }

   public INBTType<LongNBT> getType() {
      return TYPE;
   }

   public String toString() {
      return this.data + "L";
   }

   /**
    * Creates a clone of the tag.
    */
   public LongNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof LongNBT && this.data == ((LongNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new StringTextComponent("L")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new StringTextComponent(String.valueOf(this.data))).appendSibling(itextcomponent).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return this.data;
   }

   public int getInt() {
      return (int)(this.data & -1L);
   }

   public short getShort() {
      return (short)((int)(this.data & 65535L));
   }

   public byte getByte() {
      return (byte)((int)(this.data & 255L));
   }

   public double getDouble() {
      return (double)this.data;
   }

   public float getFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   static class Cache {
      static final LongNBT[] CACHE = new LongNBT[1153];

      static {
         for(int i = 0; i < CACHE.length; ++i) {
            CACHE[i] = new LongNBT((long)(-128 + i));
         }

      }
   }
}