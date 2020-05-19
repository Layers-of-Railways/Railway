package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.List;

public class ScheduleDuties {
   private final List<DutyTime> field_221396_a = Lists.newArrayList();
   private int field_221397_b;

   public ScheduleDuties func_221394_a(int duration, float p_221394_2_) {
      this.field_221396_a.add(new DutyTime(duration, p_221394_2_));
      this.func_221395_b();
      return this;
   }

   private void func_221395_b() {
      Int2ObjectSortedMap<DutyTime> int2objectsortedmap = new Int2ObjectAVLTreeMap<>();
      this.field_221396_a.forEach((p_221393_1_) -> {
         DutyTime dutytime = int2objectsortedmap.put(p_221393_1_.getDuration(), p_221393_1_);
      });
      this.field_221396_a.clear();
      this.field_221396_a.addAll(int2objectsortedmap.values());
      this.field_221397_b = 0;
   }

   public float func_221392_a(int p_221392_1_) {
      if (this.field_221396_a.size() <= 0) {
         return 0.0F;
      } else {
         DutyTime dutytime = this.field_221396_a.get(this.field_221397_b);
         DutyTime dutytime1 = this.field_221396_a.get(this.field_221396_a.size() - 1);
         boolean flag = p_221392_1_ < dutytime.getDuration();
         int i = flag ? 0 : this.field_221397_b;
         float f = flag ? dutytime1.func_221389_b() : dutytime.func_221389_b();

         for(int j = i; j < this.field_221396_a.size(); ++j) {
            DutyTime dutytime2 = this.field_221396_a.get(j);
            if (dutytime2.getDuration() > p_221392_1_) {
               break;
            }

            this.field_221397_b = j;
            f = dutytime2.func_221389_b();
         }

         return f;
      }
   }
}