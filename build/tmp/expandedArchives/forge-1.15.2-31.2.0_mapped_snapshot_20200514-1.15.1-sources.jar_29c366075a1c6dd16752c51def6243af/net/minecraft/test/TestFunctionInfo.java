package net.minecraft.test;

import java.util.function.Consumer;

public class TestFunctionInfo {
   private final String field_229650_a_;
   private final String field_229651_b_;
   private final String field_229652_c_;
   private final boolean field_229653_d_;
   private final Consumer<TestTrackerHolder> field_229654_e_;
   private final int field_229655_f_;
   private final long field_229656_g_;

   private TestFunctionInfo() {
      this.field_229650_a_ = "";
      this.field_229651_b_ = "";
      this.field_229652_c_ = "";
      this.field_229653_d_ = true;
      this.field_229654_e_ = (p) -> {};
      this.field_229655_f_ = 0;
      this.field_229656_g_ = 0L;
   }

   public void func_229658_a_(TestTrackerHolder p_229658_1_) {
      this.field_229654_e_.accept(p_229658_1_);
   }

   public String func_229657_a_() {
      return this.field_229651_b_;
   }

   public String func_229659_b_() {
      return this.field_229652_c_;
   }

   public String toString() {
      return this.field_229651_b_;
   }

   public int func_229660_c_() {
      return this.field_229655_f_;
   }

   public boolean func_229661_d_() {
      return this.field_229653_d_;
   }

   public String func_229662_e_() {
      return this.field_229650_a_;
   }

   public long func_229663_f_() {
      return this.field_229656_g_;
   }
}