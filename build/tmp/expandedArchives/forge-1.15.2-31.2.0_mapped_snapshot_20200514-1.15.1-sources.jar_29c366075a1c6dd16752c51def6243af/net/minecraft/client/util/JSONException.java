package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class JSONException extends IOException {
   private final List<JSONException.Entry> entries = Lists.newArrayList();
   private final String message;

   public JSONException(String messageIn) {
      this.entries.add(new JSONException.Entry());
      this.message = messageIn;
   }

   public JSONException(String messageIn, Throwable cause) {
      super(cause);
      this.entries.add(new JSONException.Entry());
      this.message = messageIn;
   }

   public void prependJsonKey(String key) {
      this.entries.get(0).addJsonKey(key);
   }

   public void setFilenameAndFlush(String filenameIn) {
      (this.entries.get(0)).filename = filenameIn;
      this.entries.add(0, new JSONException.Entry());
   }

   public String getMessage() {
      return "Invalid " + this.entries.get(this.entries.size() - 1) + ": " + this.message;
   }

   public static JSONException forException(Exception exception) {
      if (exception instanceof JSONException) {
         return (JSONException)exception;
      } else {
         String s = exception.getMessage();
         if (exception instanceof FileNotFoundException) {
            s = "File not found";
         }

         return new JSONException(s, exception);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Entry {
      @Nullable
      private String filename;
      private final List<String> jsonKeys = Lists.newArrayList();

      private Entry() {
      }

      private void addJsonKey(String key) {
         this.jsonKeys.add(0, key);
      }

      public String getJsonKeys() {
         return StringUtils.join((Iterable<?>)this.jsonKeys, "->");
      }

      public String toString() {
         if (this.filename != null) {
            return this.jsonKeys.isEmpty() ? this.filename : this.filename + " " + this.getJsonKeys();
         } else {
            return this.jsonKeys.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.getJsonKeys();
         }
      }
   }
}