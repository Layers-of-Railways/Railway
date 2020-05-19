package net.minecraft.entity.ai.brain.schedule;

public class DutyTime {
   private final int duration;
   private final float field_221391_b;

   public DutyTime(int durationIn, float p_i50139_2_) {
      this.duration = durationIn;
      this.field_221391_b = p_i50139_2_;
   }

   public int getDuration() {
      return this.duration;
   }

   public float func_221389_b() {
      return this.field_221391_b;
   }
}