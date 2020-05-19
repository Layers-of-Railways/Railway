package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayNBT extends CollectionNBT<IntNBT> {
   public static final INBTType<IntArrayNBT> TYPE = new INBTType<IntArrayNBT>() {
      public IntArrayNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(192L);
         int i = p_225649_1_.readInt();
         p_225649_3_.read(32L * (long)i);
         int[] aint = new int[i];

         for(int j = 0; j < i; ++j) {
            aint[j] = p_225649_1_.readInt();
         }

         return new IntArrayNBT(aint);
      }

      public String func_225648_a_() {
         return "INT[]";
      }

      public String func_225650_b_() {
         return "TAG_Int_Array";
      }
   };
   private int[] intArray;

   public IntArrayNBT(int[] p_i45132_1_) {
      this.intArray = p_i45132_1_;
   }

   public IntArrayNBT(List<Integer> p_i47528_1_) {
      this(toArray(p_i47528_1_));
   }

   private static int[] toArray(List<Integer> p_193584_0_) {
      int[] aint = new int[p_193584_0_.size()];

      for(int i = 0; i < p_193584_0_.size(); ++i) {
         Integer integer = p_193584_0_.get(i);
         aint[i] = integer == null ? 0 : integer;
      }

      return aint;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.intArray.length);

      for(int i : this.intArray) {
         output.writeInt(i);
      }

   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 11;
   }

   public INBTType<IntArrayNBT> getType() {
      return TYPE;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[I;");

      for(int i = 0; i < this.intArray.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.intArray[i]);
      }

      return stringbuilder.append(']').toString();
   }

   /**
    * Creates a clone of the tag.
    */
   public IntArrayNBT copy() {
      int[] aint = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
      return new IntArrayNBT(aint);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof IntArrayNBT && Arrays.equals(this.intArray, ((IntArrayNBT)p_equals_1_).intArray);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.intArray);
   }

   public int[] getIntArray() {
      return this.intArray;
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new StringTextComponent("I")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      ITextComponent itextcomponent1 = (new StringTextComponent("[")).appendSibling(itextcomponent).appendText(";");

      for(int i = 0; i < this.intArray.length; ++i) {
         itextcomponent1.appendText(" ").appendSibling((new StringTextComponent(String.valueOf(this.intArray[i]))).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (i != this.intArray.length - 1) {
            itextcomponent1.appendText(",");
         }
      }

      itextcomponent1.appendText("]");
      return itextcomponent1;
   }

   public int size() {
      return this.intArray.length;
   }

   public IntNBT get(int p_get_1_) {
      return IntNBT.valueOf(this.intArray[p_get_1_]);
   }

   public IntNBT set(int p_set_1_, IntNBT p_set_2_) {
      int i = this.intArray[p_set_1_];
      this.intArray[p_set_1_] = p_set_2_.getInt();
      return IntNBT.valueOf(i);
   }

   public void add(int p_add_1_, IntNBT p_add_2_) {
      this.intArray = ArrayUtils.add(this.intArray, p_add_1_, p_add_2_.getInt());
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.intArray[p_218659_1_] = ((NumberNBT)p_218659_2_).getInt();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.intArray = ArrayUtils.add(this.intArray, p_218660_1_, ((NumberNBT)p_218660_2_).getInt());
         return true;
      } else {
         return false;
      }
   }

   public IntNBT remove(int p_remove_1_) {
      int i = this.intArray[p_remove_1_];
      this.intArray = ArrayUtils.remove(this.intArray, p_remove_1_);
      return IntNBT.valueOf(i);
   }

   public void clear() {
      this.intArray = new int[0];
   }
}