package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayNBT extends CollectionNBT<LongNBT> {
   public static final INBTType<LongArrayNBT> TYPE = new INBTType<LongArrayNBT>() {
      public LongArrayNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(192L);
         int i = p_225649_1_.readInt();
         p_225649_3_.read(64L * (long)i);
         long[] along = new long[i];

         for(int j = 0; j < i; ++j) {
            along[j] = p_225649_1_.readLong();
         }

         return new LongArrayNBT(along);
      }

      public String func_225648_a_() {
         return "LONG[]";
      }

      public String func_225650_b_() {
         return "TAG_Long_Array";
      }
   };
   private long[] data;

   public LongArrayNBT(long[] p_i47524_1_) {
      this.data = p_i47524_1_;
   }

   public LongArrayNBT(LongSet p_i48736_1_) {
      this.data = p_i48736_1_.toLongArray();
   }

   public LongArrayNBT(List<Long> p_i47525_1_) {
      this(toArray(p_i47525_1_));
   }

   private static long[] toArray(List<Long> p_193586_0_) {
      long[] along = new long[p_193586_0_.size()];

      for(int i = 0; i < p_193586_0_.size(); ++i) {
         Long olong = p_193586_0_.get(i);
         along[i] = olong == null ? 0L : olong;
      }

      return along;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.data.length);

      for(long i : this.data) {
         output.writeLong(i);
      }

   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 12;
   }

   public INBTType<LongArrayNBT> getType() {
      return TYPE;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[L;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.data[i]).append('L');
      }

      return stringbuilder.append(']').toString();
   }

   /**
    * Creates a clone of the tag.
    */
   public LongArrayNBT copy() {
      long[] along = new long[this.data.length];
      System.arraycopy(this.data, 0, along, 0, this.data.length);
      return new LongArrayNBT(along);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof LongArrayNBT && Arrays.equals(this.data, ((LongArrayNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new StringTextComponent("L")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      ITextComponent itextcomponent1 = (new StringTextComponent("[")).appendSibling(itextcomponent).appendText(";");

      for(int i = 0; i < this.data.length; ++i) {
         ITextComponent itextcomponent2 = (new StringTextComponent(String.valueOf(this.data[i]))).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         itextcomponent1.appendText(" ").appendSibling(itextcomponent2).appendSibling(itextcomponent);
         if (i != this.data.length - 1) {
            itextcomponent1.appendText(",");
         }
      }

      itextcomponent1.appendText("]");
      return itextcomponent1;
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public LongNBT get(int p_get_1_) {
      return LongNBT.valueOf(this.data[p_get_1_]);
   }

   public LongNBT set(int p_set_1_, LongNBT p_set_2_) {
      long i = this.data[p_set_1_];
      this.data[p_set_1_] = p_set_2_.getLong();
      return LongNBT.valueOf(i);
   }

   public void add(int p_add_1_, LongNBT p_add_2_) {
      this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getLong());
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.data[p_218659_1_] = ((NumberNBT)p_218659_2_).getLong();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.data = ArrayUtils.add(this.data, p_218660_1_, ((NumberNBT)p_218660_2_).getLong());
         return true;
      } else {
         return false;
      }
   }

   public LongNBT remove(int p_remove_1_) {
      long i = this.data[p_remove_1_];
      this.data = ArrayUtils.remove(this.data, p_remove_1_);
      return LongNBT.valueOf(i);
   }

   public void clear() {
      this.data = new long[0];
   }
}