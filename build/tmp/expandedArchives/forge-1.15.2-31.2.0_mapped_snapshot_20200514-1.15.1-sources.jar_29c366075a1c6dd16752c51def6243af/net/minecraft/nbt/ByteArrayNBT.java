package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayNBT extends CollectionNBT<ByteNBT> {
   public static final INBTType<ByteArrayNBT> TYPE = new INBTType<ByteArrayNBT>() {
      public ByteArrayNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(192L);
         int i = p_225649_1_.readInt();
         p_225649_3_.read(8L * (long)i);
         byte[] abyte = new byte[i];
         p_225649_1_.readFully(abyte);
         return new ByteArrayNBT(abyte);
      }

      public String func_225648_a_() {
         return "BYTE[]";
      }

      public String func_225650_b_() {
         return "TAG_Byte_Array";
      }
   };
   private byte[] data;

   public ByteArrayNBT(byte[] data) {
      this.data = data;
   }

   public ByteArrayNBT(List<Byte> p_i47529_1_) {
      this(toArray(p_i47529_1_));
   }

   private static byte[] toArray(List<Byte> p_193589_0_) {
      byte[] abyte = new byte[p_193589_0_.size()];

      for(int i = 0; i < p_193589_0_.size(); ++i) {
         Byte obyte = p_193589_0_.get(i);
         abyte[i] = obyte == null ? 0 : obyte;
      }

      return abyte;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.data.length);
      output.write(this.data);
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 7;
   }

   public INBTType<ByteArrayNBT> getType() {
      return TYPE;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[B;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append((int)this.data[i]).append('B');
      }

      return stringbuilder.append(']').toString();
   }

   /**
    * Creates a clone of the tag.
    */
   public INBT copy() {
      byte[] abyte = new byte[this.data.length];
      System.arraycopy(this.data, 0, abyte, 0, this.data.length);
      return new ByteArrayNBT(abyte);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof ByteArrayNBT && Arrays.equals(this.data, ((ByteArrayNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new StringTextComponent("B")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      ITextComponent itextcomponent1 = (new StringTextComponent("[")).appendSibling(itextcomponent).appendText(";");

      for(int i = 0; i < this.data.length; ++i) {
         ITextComponent itextcomponent2 = (new StringTextComponent(String.valueOf((int)this.data[i]))).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         itextcomponent1.appendText(" ").appendSibling(itextcomponent2).appendSibling(itextcomponent);
         if (i != this.data.length - 1) {
            itextcomponent1.appendText(",");
         }
      }

      itextcomponent1.appendText("]");
      return itextcomponent1;
   }

   public byte[] getByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public ByteNBT get(int p_get_1_) {
      return ByteNBT.valueOf(this.data[p_get_1_]);
   }

   public ByteNBT set(int p_set_1_, ByteNBT p_set_2_) {
      byte b0 = this.data[p_set_1_];
      this.data[p_set_1_] = p_set_2_.getByte();
      return ByteNBT.valueOf(b0);
   }

   public void add(int p_add_1_, ByteNBT p_add_2_) {
      this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getByte());
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.data[p_218659_1_] = ((NumberNBT)p_218659_2_).getByte();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.data = ArrayUtils.add(this.data, p_218660_1_, ((NumberNBT)p_218660_2_).getByte());
         return true;
      } else {
         return false;
      }
   }

   public ByteNBT remove(int p_remove_1_) {
      byte b0 = this.data[p_remove_1_];
      this.data = ArrayUtils.remove(this.data, p_remove_1_);
      return ByteNBT.valueOf(b0);
   }

   public void clear() {
      this.data = new byte[0];
   }
}