package net.minecraft.util;

public abstract class IntReferenceHolder {
   private int lastKnownValue;

   public static IntReferenceHolder create(final IIntArray p_221493_0_, final int idx) {
      return new IntReferenceHolder() {
         public int get() {
            return p_221493_0_.get(idx);
         }

         public void set(int p_221494_1_) {
            p_221493_0_.set(idx, p_221494_1_);
         }
      };
   }

   public static IntReferenceHolder create(final int[] p_221497_0_, final int idx) {
      return new IntReferenceHolder() {
         public int get() {
            return p_221497_0_[idx];
         }

         public void set(int p_221494_1_) {
            p_221497_0_[idx] = p_221494_1_;
         }
      };
   }

   public static IntReferenceHolder single() {
      return new IntReferenceHolder() {
         private int value;

         public int get() {
            return this.value;
         }

         public void set(int p_221494_1_) {
            this.value = p_221494_1_;
         }
      };
   }

   public abstract int get();

   public abstract void set(int p_221494_1_);

   public boolean isDirty() {
      int i = this.get();
      boolean flag = i != this.lastKnownValue;
      this.lastKnownValue = i;
      return flag;
   }
}