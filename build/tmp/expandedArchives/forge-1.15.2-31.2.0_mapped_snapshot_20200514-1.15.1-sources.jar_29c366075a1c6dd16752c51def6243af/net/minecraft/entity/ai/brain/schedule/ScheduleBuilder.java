package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleBuilder {
   private final Schedule schedule;
   private final List<ScheduleBuilder.ActivityEntry> entries = Lists.newArrayList();

   public ScheduleBuilder(Schedule p_i50135_1_) {
      this.schedule = p_i50135_1_;
   }

   public ScheduleBuilder add(int duration, Activity activityIn) {
      this.entries.add(new ScheduleBuilder.ActivityEntry(duration, activityIn));
      return this;
   }

   public Schedule build() {
      this.entries.stream().map(ScheduleBuilder.ActivityEntry::getActivity).collect(Collectors.toSet()).forEach(this.schedule::createDutiesFor);
      this.entries.forEach((p_221405_1_) -> {
         Activity activity = p_221405_1_.getActivity();
         this.schedule.getAllDutiesExcept(activity).forEach((p_221403_1_) -> {
            p_221403_1_.func_221394_a(p_221405_1_.getDuration(), 0.0F);
         });
         this.schedule.getDutiesFor(activity).func_221394_a(p_221405_1_.getDuration(), 1.0F);
      });
      return this.schedule;
   }

   static class ActivityEntry {
      private final int duration;
      private final Activity activity;

      public ActivityEntry(int durationIn, Activity activityIn) {
         this.duration = durationIn;
         this.activity = activityIn;
      }

      public int getDuration() {
         return this.duration;
      }

      public Activity getActivity() {
         return this.activity;
      }
   }
}