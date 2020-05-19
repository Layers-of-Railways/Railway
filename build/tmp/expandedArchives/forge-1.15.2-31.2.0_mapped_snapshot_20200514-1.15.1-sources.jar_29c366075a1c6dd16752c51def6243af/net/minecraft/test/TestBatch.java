package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.server.ServerWorld;

public class TestBatch {
   private final String field_229460_a_;
   private final Collection<TestFunctionInfo> field_229461_b_;
   @Nullable
   private final Consumer<ServerWorld> field_229462_c_;

   public TestBatch(String p_i226065_1_, Collection<TestFunctionInfo> p_i226065_2_, @Nullable Consumer<ServerWorld> p_i226065_3_) {
      if (p_i226065_2_.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
      } else {
         this.field_229460_a_ = p_i226065_1_;
         this.field_229461_b_ = p_i226065_2_;
         this.field_229462_c_ = p_i226065_3_;
      }
   }

   public String func_229463_a_() {
      return this.field_229460_a_;
   }

   public Collection<TestFunctionInfo> func_229465_b_() {
      return this.field_229461_b_;
   }

   public void func_229464_a_(ServerWorld p_229464_1_) {
      if (this.field_229462_c_ != null) {
         this.field_229462_c_.accept(p_229464_1_);
      }

   }
}