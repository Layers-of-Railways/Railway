package net.minecraft.test;

import java.util.Iterator;
import java.util.List;

public class TestList {
   private final TestTracker field_229564_a_;
   private final List<TestTickResult> field_229565_b_;
   private long field_229566_c_;

   private TestList(TestTracker p_TestList_1_, List<TestTickResult> p_TestList_2_) {
      this.field_229564_a_ = p_TestList_1_;
      this.field_229565_b_ = p_TestList_2_;
   }

   public void func_229567_a_(long p_229567_1_) {
      try {
         this.func_229569_c_(p_229567_1_);
      } catch (Exception var4) {
         ;
      }

   }

   public void func_229568_b_(long p_229568_1_) {
      try {
         this.func_229569_c_(p_229568_1_);
      } catch (Exception exception) {
         this.field_229564_a_.func_229506_a_(exception);
      }

   }

   private void func_229569_c_(long p_229569_1_) {
      Iterator<TestTickResult> iterator = this.field_229565_b_.iterator();

      while(iterator.hasNext()) {
         TestTickResult testtickresult = iterator.next();
         testtickresult.field_229486_b_.run();
         iterator.remove();
         long i = p_229569_1_ - this.field_229566_c_;
         long j = this.field_229566_c_;
         this.field_229566_c_ = p_229569_1_;
         if (testtickresult.field_229485_a_ != null && testtickresult.field_229485_a_ != i) {
            this.field_229564_a_.func_229506_a_(new TestRuntimeException("Succeeded in invalid tick: expected " + (j + testtickresult.field_229485_a_) + ", but current tick is " + p_229569_1_));
            break;
         }
      }

   }
}