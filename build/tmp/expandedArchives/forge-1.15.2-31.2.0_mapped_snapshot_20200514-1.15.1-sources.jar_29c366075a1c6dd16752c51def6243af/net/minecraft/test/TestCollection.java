package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;

public class TestCollection {
   public static final TestCollection field_229570_a_ = new TestCollection();
   private final Collection<TestTracker> field_229571_b_ = Lists.newCopyOnWriteArrayList();

   public void func_229573_a_(TestTracker p_229573_1_) {
      this.field_229571_b_.add(p_229573_1_);
   }

   public void func_229572_a_() {
      this.field_229571_b_.clear();
   }

   public void func_229574_b_() {
      this.field_229571_b_.forEach(TestTracker::func_229507_b_);
      this.field_229571_b_.removeIf(TestTracker::func_229518_k_);
   }
}