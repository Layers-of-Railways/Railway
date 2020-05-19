package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;

public class TestResultList {
   private final Collection<TestTracker> field_229576_a_ = Lists.newArrayList();
   @Nullable
   private ITestCallback field_229577_b_;

   public TestResultList() {
   }

   public TestResultList(Collection<TestTracker> p_i226072_1_) {
      this.field_229576_a_.addAll(p_i226072_1_);
   }

   public void func_229579_a_(TestTracker p_229579_1_) {
      this.field_229576_a_.add(p_229579_1_);
      if (this.field_229577_b_ != null) {
         p_229579_1_.func_229504_a_(this.field_229577_b_);
      }

   }

   public void func_229580_a_(ITestCallback p_229580_1_) {
      this.field_229577_b_ = p_229580_1_;
      this.field_229576_a_.forEach((p_229581_1_) -> {
         p_229581_1_.func_229504_a_(p_229580_1_);
      });
   }

   public int func_229578_a_() {
      return (int)this.field_229576_a_.stream().filter(TestTracker::func_229516_i_).filter(TestTracker::func_229520_q_).count();
   }

   public int func_229583_b_() {
      return (int)this.field_229576_a_.stream().filter(TestTracker::func_229516_i_).filter(TestTracker::func_229521_r_).count();
   }

   public int func_229584_c_() {
      return (int)this.field_229576_a_.stream().filter(TestTracker::func_229518_k_).count();
   }

   public boolean func_229585_d_() {
      return this.func_229578_a_() > 0;
   }

   public boolean func_229586_e_() {
      return this.func_229583_b_() > 0;
   }

   public int func_229587_h_() {
      return this.field_229576_a_.size();
   }

   public boolean func_229588_i_() {
      return this.func_229584_c_() == this.func_229587_h_();
   }

   public String func_229589_j_() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append('[');
      this.field_229576_a_.forEach((p_229582_1_) -> {
         if (!p_229582_1_.func_229517_j_()) {
            stringbuffer.append(' ');
         } else if (p_229582_1_.func_229515_h_()) {
            stringbuffer.append('+');
         } else if (p_229582_1_.func_229516_i_()) {
            stringbuffer.append((char)(p_229582_1_.func_229520_q_() ? 'X' : 'x'));
         } else {
            stringbuffer.append('_');
         }

      });
      stringbuffer.append(']');
      return stringbuffer.toString();
   }

   public String toString() {
      return this.func_229589_j_();
   }
}