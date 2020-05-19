package net.minecraft.util;

public class Tuple<A, B> {
   private A a;
   private B b;

   public Tuple(A aIn, B bIn) {
      this.a = aIn;
      this.b = bIn;
   }

   public A getA() {
      return this.a;
   }

   public B getB() {
      return this.b;
   }
}