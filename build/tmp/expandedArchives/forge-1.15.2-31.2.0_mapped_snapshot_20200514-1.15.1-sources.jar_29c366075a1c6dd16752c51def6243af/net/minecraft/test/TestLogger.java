package net.minecraft.test;

import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLogger implements ITestLogger {
   private static final Logger field_229575_a_ = LogManager.getLogger();

   public void func_225646_a_(TestTracker p_225646_1_) {
      if (p_225646_1_.func_229520_q_()) {
         field_229575_a_.error(p_225646_1_.func_229510_c_() + " failed! " + Util.getMessage(p_225646_1_.func_229519_n_()));
      } else {
         field_229575_a_.warn("(optional) " + p_225646_1_.func_229510_c_() + " failed. " + Util.getMessage(p_225646_1_.func_229519_n_()));
      }

   }
}