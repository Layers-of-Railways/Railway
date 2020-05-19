package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ListNBT extends CollectionNBT<INBT> {
   public static final INBTType<ListNBT> TYPE = new INBTType<ListNBT>() {
      public ListNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(296L);
         if (p_225649_2_ > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
         } else {
            byte b0 = p_225649_1_.readByte();
            int i = p_225649_1_.readInt();
            if (b0 == 0 && i > 0) {
               throw new RuntimeException("Missing type on ListTag");
            } else {
               p_225649_3_.read(32L * (long)i);
               INBTType<?> inbttype = NBTTypes.func_229710_a_(b0);
               List<INBT> list = Lists.newArrayListWithCapacity(i);

               for(int j = 0; j < i; ++j) {
                  list.add(inbttype.func_225649_b_(p_225649_1_, p_225649_2_ + 1, p_225649_3_));
               }

               return new ListNBT(list, b0);
            }
         }
      }

      public String func_225648_a_() {
         return "LIST";
      }

      public String func_225650_b_() {
         return "TAG_List";
      }
   };
   private static final ByteSet field_229695_b_ = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
   private final List<INBT> tagList;
   private byte tagType;

   private ListNBT(List<INBT> p_i226078_1_, byte p_i226078_2_) {
      this.tagList = p_i226078_1_;
      this.tagType = p_i226078_2_;
   }

   public ListNBT() {
      this(Lists.newArrayList(), (byte)0);
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      if (this.tagList.isEmpty()) {
         this.tagType = 0;
      } else {
         this.tagType = this.tagList.get(0).getId();
      }

      output.writeByte(this.tagType);
      output.writeInt(this.tagList.size());

      for(INBT inbt : this.tagList) {
         inbt.write(output);
      }

   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 9;
   }

   public INBTType<ListNBT> getType() {
      return TYPE;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[");

      for(int i = 0; i < this.tagList.size(); ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.tagList.get(i));
      }

      return stringbuilder.append(']').toString();
   }

   private void func_218663_f() {
      if (this.tagList.isEmpty()) {
         this.tagType = 0;
      }

   }

   public INBT remove(int p_remove_1_) {
      INBT inbt = this.tagList.remove(p_remove_1_);
      this.func_218663_f();
      return inbt;
   }

   public boolean isEmpty() {
      return this.tagList.isEmpty();
   }

   /**
    * Retrieves the NBTTagCompound at the specified index in the list
    */
   public CompoundNBT getCompound(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBT inbt = this.tagList.get(i);
         if (inbt.getId() == 10) {
            return (CompoundNBT)inbt;
         }
      }

      return new CompoundNBT();
   }

   public ListNBT getList(int iIn) {
      if (iIn >= 0 && iIn < this.tagList.size()) {
         INBT inbt = this.tagList.get(iIn);
         if (inbt.getId() == 9) {
            return (ListNBT)inbt;
         }
      }

      return new ListNBT();
   }

   public short getShort(int iIn) {
      if (iIn >= 0 && iIn < this.tagList.size()) {
         INBT inbt = this.tagList.get(iIn);
         if (inbt.getId() == 2) {
            return ((ShortNBT)inbt).getShort();
         }
      }

      return 0;
   }

   public int getInt(int iIn) {
      if (iIn >= 0 && iIn < this.tagList.size()) {
         INBT inbt = this.tagList.get(iIn);
         if (inbt.getId() == 3) {
            return ((IntNBT)inbt).getInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBT inbt = this.tagList.get(i);
         if (inbt.getId() == 11) {
            return ((IntArrayNBT)inbt).getIntArray();
         }
      }

      return new int[0];
   }

   public double getDouble(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBT inbt = this.tagList.get(i);
         if (inbt.getId() == 6) {
            return ((DoubleNBT)inbt).getDouble();
         }
      }

      return 0.0D;
   }

   public float getFloat(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBT inbt = this.tagList.get(i);
         if (inbt.getId() == 5) {
            return ((FloatNBT)inbt).getFloat();
         }
      }

      return 0.0F;
   }

   /**
    * Retrieves the tag String value at the specified index in the list
    */
   public String getString(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         INBT inbt = this.tagList.get(i);
         return inbt.getId() == 8 ? inbt.getString() : inbt.toString();
      } else {
         return "";
      }
   }

   public int size() {
      return this.tagList.size();
   }

   public INBT get(int p_get_1_) {
      return this.tagList.get(p_get_1_);
   }

   public INBT set(int p_set_1_, INBT p_set_2_) {
      INBT inbt = this.get(p_set_1_);
      if (!this.func_218659_a(p_set_1_, p_set_2_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_set_2_.getId(), this.tagType));
      } else {
         return inbt;
      }
   }

   public void add(int p_add_1_, INBT p_add_2_) {
      if (!this.func_218660_b(p_add_1_, p_add_2_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_add_2_.getId(), this.tagType));
      }
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (this.func_218661_a(p_218659_2_)) {
         this.tagList.set(p_218659_1_, p_218659_2_);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (this.func_218661_a(p_218660_2_)) {
         this.tagList.add(p_218660_1_, p_218660_2_);
         return true;
      } else {
         return false;
      }
   }

   private boolean func_218661_a(INBT p_218661_1_) {
      if (p_218661_1_.getId() == 0) {
         return false;
      } else if (this.tagType == 0) {
         this.tagType = p_218661_1_.getId();
         return true;
      } else {
         return this.tagType == p_218661_1_.getId();
      }
   }

   /**
    * Creates a clone of the tag.
    */
   public ListNBT copy() {
      Iterable<INBT> iterable = (Iterable<INBT>)(NBTTypes.func_229710_a_(this.tagType).func_225651_c_() ? this.tagList : Iterables.transform(this.tagList, INBT::copy));
      List<INBT> list = Lists.newArrayList(iterable);
      return new ListNBT(list, this.tagType);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof ListNBT && Objects.equals(this.tagList, ((ListNBT)p_equals_1_).tagList);
      }
   }

   public int hashCode() {
      return this.tagList.hashCode();
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      if (this.isEmpty()) {
         return new StringTextComponent("[]");
      } else if (field_229695_b_.contains(this.tagType) && this.size() <= 8) {
         String s1 = ", ";
         ITextComponent itextcomponent2 = new StringTextComponent("[");

         for(int j = 0; j < this.tagList.size(); ++j) {
            if (j != 0) {
               itextcomponent2.appendText(", ");
            }

            itextcomponent2.appendSibling(this.tagList.get(j).toFormattedComponent());
         }

         itextcomponent2.appendText("]");
         return itextcomponent2;
      } else {
         ITextComponent itextcomponent = new StringTextComponent("[");
         if (!indentation.isEmpty()) {
            itextcomponent.appendText("\n");
         }

         String s = String.valueOf(',');

         for(int i = 0; i < this.tagList.size(); ++i) {
            ITextComponent itextcomponent1 = new StringTextComponent(Strings.repeat(indentation, indentDepth + 1));
            itextcomponent1.appendSibling(this.tagList.get(i).toFormattedComponent(indentation, indentDepth + 1));
            if (i != this.tagList.size() - 1) {
               itextcomponent1.appendText(s).appendText(indentation.isEmpty() ? " " : "\n");
            }

            itextcomponent.appendSibling(itextcomponent1);
         }

         if (!indentation.isEmpty()) {
            itextcomponent.appendText("\n").appendText(Strings.repeat(indentation, indentDepth));
         }

         itextcomponent.appendText("]");
         return itextcomponent;
      }
   }

   public int getTagType() {
      return this.tagType;
   }

   public void clear() {
      this.tagList.clear();
      this.tagType = 0;
   }
}