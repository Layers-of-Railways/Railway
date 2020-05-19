package net.minecraft.server;

import java.io.OutputStream;
import net.minecraft.util.LoggingPrintStream;

public class DebugLoggingPrintStream extends LoggingPrintStream {
   public DebugLoggingPrintStream(String domainIn, OutputStream outStream) {
      super(domainIn, outStream);
   }

   protected void logString(String string) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
      StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.domain, stacktraceelement.getFileName(), stacktraceelement.getLineNumber(), string);
   }
}