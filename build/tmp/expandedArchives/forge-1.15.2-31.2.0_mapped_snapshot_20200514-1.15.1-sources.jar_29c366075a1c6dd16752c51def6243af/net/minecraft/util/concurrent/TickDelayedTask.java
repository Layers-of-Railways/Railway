package net.minecraft.util.concurrent;

public class TickDelayedTask implements Runnable {
   private final int field_218824_a;
   private final Runnable field_218825_b;

   public TickDelayedTask(int p_i50745_1_, Runnable p_i50745_2_) {
      this.field_218824_a = p_i50745_1_;
      this.field_218825_b = p_i50745_2_;
   }

   /**
    * Get the server time when this task was scheduled
    */
   public int getScheduledTime() {
      return this.field_218824_a;
   }

   public void run() {
      this.field_218825_b.run();
   }
}