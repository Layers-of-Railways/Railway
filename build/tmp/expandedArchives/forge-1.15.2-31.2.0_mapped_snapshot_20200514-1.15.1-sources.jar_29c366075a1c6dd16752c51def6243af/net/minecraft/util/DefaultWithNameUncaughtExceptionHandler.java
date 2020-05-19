package net.minecraft.util;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class DefaultWithNameUncaughtExceptionHandler implements UncaughtExceptionHandler {
   private final Logger logger;

   public DefaultWithNameUncaughtExceptionHandler(Logger p_i48771_1_) {
      this.logger = p_i48771_1_;
   }

   public void uncaughtException(Thread p_uncaughtException_1_, Throwable p_uncaughtException_2_) {
      this.logger.error("Caught previously unhandled exception :");
      this.logger.error(p_uncaughtException_1_.getName(), p_uncaughtException_2_);
   }
}