package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CSVWriter {
   private final Writer field_225429_a;
   private final int field_225430_b;

   private CSVWriter(Writer p_i51695_1_, List<String> p_i51695_2_) throws IOException {
      this.field_225429_a = p_i51695_1_;
      this.field_225430_b = p_i51695_2_.size();
      this.func_225427_a(p_i51695_2_.stream());
   }

   public static CSVWriter.Builder func_225428_a() {
      return new CSVWriter.Builder();
   }

   public void func_225426_a(Object... p_225426_1_) throws IOException {
      if (p_225426_1_.length != this.field_225430_b) {
         throw new IllegalArgumentException("Invalid number of columns, expected " + this.field_225430_b + ", but got " + p_225426_1_.length);
      } else {
         this.func_225427_a(Stream.of(p_225426_1_));
      }
   }

   private void func_225427_a(Stream<?> p_225427_1_) throws IOException {
      this.field_225429_a.write((String)p_225427_1_.map(CSVWriter::func_225425_a).collect(Collectors.joining(",")) + "\r\n");
   }

   private static String func_225425_a(@Nullable Object p_225425_0_) {
      return StringEscapeUtils.escapeCsv(p_225425_0_ != null ? p_225425_0_.toString() : "[null]");
   }

   public static class Builder {
      private final List<String> field_225424_a = Lists.newArrayList();

      public CSVWriter.Builder func_225423_a(String p_225423_1_) {
         this.field_225424_a.add(p_225423_1_);
         return this;
      }

      public CSVWriter func_225422_a(Writer p_225422_1_) throws IOException {
         return new CSVWriter(p_225422_1_, this.field_225424_a);
      }
   }
}