package com.mojang.realmsclient.util;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextRenderingUtils {
   static List<String> func_225223_a(String p_225223_0_) {
      return Arrays.asList(p_225223_0_.split("\\n"));
   }

   public static List<TextRenderingUtils.Line> func_225224_a(String p_225224_0_, TextRenderingUtils.LineSegment... p_225224_1_) {
      return func_225225_a(p_225224_0_, Arrays.asList(p_225224_1_));
   }

   private static List<TextRenderingUtils.Line> func_225225_a(String p_225225_0_, List<TextRenderingUtils.LineSegment> p_225225_1_) {
      List<String> list = func_225223_a(p_225225_0_);
      return func_225222_a(list, p_225225_1_);
   }

   private static List<TextRenderingUtils.Line> func_225222_a(List<String> p_225222_0_, List<TextRenderingUtils.LineSegment> p_225222_1_) {
      int i = 0;
      List<TextRenderingUtils.Line> list = Lists.newArrayList();

      for(String s : p_225222_0_) {
         List<TextRenderingUtils.LineSegment> list1 = Lists.newArrayList();

         for(String s1 : func_225226_a(s, "%link")) {
            if (s1.equals("%link")) {
               list1.add(p_225222_1_.get(i++));
            } else {
               list1.add(TextRenderingUtils.LineSegment.func_225218_a(s1));
            }
         }

         list.add(new TextRenderingUtils.Line(list1));
      }

      return list;
   }

   public static List<String> func_225226_a(String p_225226_0_, String p_225226_1_) {
      if (p_225226_1_.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         List<String> list = Lists.newArrayList();

         int i;
         int j;
         for(i = 0; (j = p_225226_0_.indexOf(p_225226_1_, i)) != -1; i = j + p_225226_1_.length()) {
            if (j > i) {
               list.add(p_225226_0_.substring(i, j));
            }

            list.add(p_225226_1_);
         }

         if (i < p_225226_0_.length()) {
            list.add(p_225226_0_.substring(i));
         }

         return list;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Line {
      public final List<TextRenderingUtils.LineSegment> field_225213_a;

      Line(List<TextRenderingUtils.LineSegment> p_i51644_1_) {
         this.field_225213_a = p_i51644_1_;
      }

      public String toString() {
         return "Line{segments=" + this.field_225213_a + '}';
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            TextRenderingUtils.Line textrenderingutils$line = (TextRenderingUtils.Line)p_equals_1_;
            return Objects.equals(this.field_225213_a, textrenderingutils$line.field_225213_a);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.field_225213_a);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LineSegment {
      final String field_225219_a;
      final String field_225220_b;
      final String field_225221_c;

      private LineSegment(String p_i51642_1_) {
         this.field_225219_a = p_i51642_1_;
         this.field_225220_b = null;
         this.field_225221_c = null;
      }

      private LineSegment(String p_i51643_1_, String p_i51643_2_, String p_i51643_3_) {
         this.field_225219_a = p_i51643_1_;
         this.field_225220_b = p_i51643_2_;
         this.field_225221_c = p_i51643_3_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            TextRenderingUtils.LineSegment textrenderingutils$linesegment = (TextRenderingUtils.LineSegment)p_equals_1_;
            return Objects.equals(this.field_225219_a, textrenderingutils$linesegment.field_225219_a) && Objects.equals(this.field_225220_b, textrenderingutils$linesegment.field_225220_b) && Objects.equals(this.field_225221_c, textrenderingutils$linesegment.field_225221_c);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.field_225219_a, this.field_225220_b, this.field_225221_c);
      }

      public String toString() {
         return "Segment{fullText='" + this.field_225219_a + '\'' + ", linkTitle='" + this.field_225220_b + '\'' + ", linkUrl='" + this.field_225221_c + '\'' + '}';
      }

      public String func_225215_a() {
         return this.func_225217_b() ? this.field_225220_b : this.field_225219_a;
      }

      public boolean func_225217_b() {
         return this.field_225220_b != null;
      }

      public String func_225216_c() {
         if (!this.func_225217_b()) {
            throw new IllegalStateException("Not a link: " + this);
         } else {
            return this.field_225221_c;
         }
      }

      public static TextRenderingUtils.LineSegment func_225214_a(String p_225214_0_, String p_225214_1_) {
         return new TextRenderingUtils.LineSegment((String)null, p_225214_0_, p_225214_1_);
      }

      static TextRenderingUtils.LineSegment func_225218_a(String p_225218_0_) {
         return new TextRenderingUtils.LineSegment(p_225218_0_);
      }
   }
}