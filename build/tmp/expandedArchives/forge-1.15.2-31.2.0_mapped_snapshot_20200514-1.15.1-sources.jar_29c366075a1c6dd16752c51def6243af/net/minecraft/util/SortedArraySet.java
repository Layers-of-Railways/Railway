package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SortedArraySet<T> extends AbstractSet<T> {
   private final Comparator<T> comparator;
   private T[] storage;
   private int maxIndex;

   private SortedArraySet(int p_i225697_1_, Comparator<T> p_i225697_2_) {
      this.comparator = p_i225697_2_;
      if (p_i225697_1_ < 0) {
         throw new IllegalArgumentException("Initial capacity (" + p_i225697_1_ + ") is negative");
      } else {
         this.storage = (T[])cast(new Object[p_i225697_1_]);
      }
   }

   public static <T extends Comparable<T>> SortedArraySet<T> newSet(int p_226172_0_) {
      return new SortedArraySet<>(p_226172_0_, Comparator.<T>naturalOrder());
   }

   private static <T> T[] cast(Object[] p_226177_0_) {
      return (T[])(p_226177_0_);
   }

   private int binarySearch(T p_226182_1_) {
      return Arrays.binarySearch(this.storage, 0, this.maxIndex, p_226182_1_, this.comparator);
   }

   private static int func_226179_b_(int p_226179_0_) {
      return -p_226179_0_ - 1;
   }

   public boolean add(T p_add_1_) {
      int i = this.binarySearch(p_add_1_);
      if (i >= 0) {
         return false;
      } else {
         int j = func_226179_b_(i);
         this.func_226176_a_(p_add_1_, j);
         return true;
      }
   }

   private void func_226181_c_(int p_226181_1_) {
      if (p_226181_1_ > this.storage.length) {
         if (this.storage != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            p_226181_1_ = (int)Math.max(Math.min((long)this.storage.length + (long)(this.storage.length >> 1), 2147483639L), (long)p_226181_1_);
         } else if (p_226181_1_ < 10) {
            p_226181_1_ = 10;
         }

         Object[] aobject = new Object[p_226181_1_];
         System.arraycopy(this.storage, 0, aobject, 0, this.maxIndex);
         this.storage = (T[])cast(aobject);
      }
   }

   private void func_226176_a_(T p_226176_1_, int p_226176_2_) {
      this.func_226181_c_(this.maxIndex + 1);
      if (p_226176_2_ != this.maxIndex) {
         System.arraycopy(this.storage, p_226176_2_, this.storage, p_226176_2_ + 1, this.maxIndex - p_226176_2_);
      }

      this.storage[p_226176_2_] = p_226176_1_;
      ++this.maxIndex;
   }

   private void func_226183_d_(int p_226183_1_) {
      --this.maxIndex;
      if (p_226183_1_ != this.maxIndex) {
         System.arraycopy(this.storage, p_226183_1_ + 1, this.storage, p_226183_1_, this.maxIndex - p_226183_1_);
      }

      this.storage[this.maxIndex] = null;
   }

   private T func_226184_e_(int p_226184_1_) {
      return this.storage[p_226184_1_];
   }

   public T func_226175_a_(T p_226175_1_) {
      int i = this.binarySearch(p_226175_1_);
      if (i >= 0) {
         return (T)this.func_226184_e_(i);
      } else {
         this.func_226176_a_(p_226175_1_, func_226179_b_(i));
         return p_226175_1_;
      }
   }

   public boolean remove(Object p_remove_1_) {
      int i = this.binarySearch((T)p_remove_1_);
      if (i >= 0) {
         this.func_226183_d_(i);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Gets the smallest element in the set
    */
   public T getSmallest() {
      return (T)this.func_226184_e_(0);
   }

   public boolean contains(Object p_contains_1_) {
      int i = this.binarySearch((T)p_contains_1_);
      return i >= 0;
   }

   public Iterator<T> iterator() {
      return new SortedArraySet.Itr();
   }

   public int size() {
      return this.maxIndex;
   }

   public Object[] toArray() {
      return this.storage.clone();
   }

   public <U> U[] toArray(U[] p_toArray_1_) {
      if (p_toArray_1_.length < this.maxIndex) {
         return (U[])(Arrays.copyOf(this.storage, this.maxIndex, p_toArray_1_.getClass()));
      } else {
         System.arraycopy(this.storage, 0, p_toArray_1_, 0, this.maxIndex);
         if (p_toArray_1_.length > this.maxIndex) {
            p_toArray_1_[this.maxIndex] = null;
         }

         return p_toArray_1_;
      }
   }

   public void clear() {
      Arrays.fill(this.storage, 0, this.maxIndex, (Object)null);
      this.maxIndex = 0;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         if (p_equals_1_ instanceof SortedArraySet) {
            SortedArraySet<?> sortedarrayset = (SortedArraySet)p_equals_1_;
            if (this.comparator.equals(sortedarrayset.comparator)) {
               return this.maxIndex == sortedarrayset.maxIndex && Arrays.equals(this.storage, sortedarrayset.storage);
            }
         }

         return super.equals(p_equals_1_);
      }
   }

   class Itr implements Iterator<T> {
      private int field_226186_b_;
      private int field_226187_c_ = -1;

      private Itr() {
      }

      public boolean hasNext() {
         return this.field_226186_b_ < SortedArraySet.this.maxIndex;
      }

      public T next() {
         if (this.field_226186_b_ >= SortedArraySet.this.maxIndex) {
            throw new NoSuchElementException();
         } else {
            this.field_226187_c_ = this.field_226186_b_++;
            return SortedArraySet.this.storage[this.field_226187_c_];
         }
      }

      public void remove() {
         if (this.field_226187_c_ == -1) {
            throw new IllegalStateException();
         } else {
            SortedArraySet.this.func_226183_d_(this.field_226187_c_);
            --this.field_226186_b_;
            this.field_226187_c_ = -1;
         }
      }
   }
}