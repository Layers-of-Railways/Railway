package net.minecraft.entity.player;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ChatVisibility {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final ChatVisibility[] field_221255_d = Arrays.stream(values()).sorted(Comparator.comparingInt(ChatVisibility::getId)).toArray((p_221253_0_) -> {
      return new ChatVisibility[p_221253_0_];
   });
   private final int id;
   private final String resourceKey;

   private ChatVisibility(int p_i50176_3_, String p_i50176_4_) {
      this.id = p_i50176_3_;
      this.resourceKey = p_i50176_4_;
   }

   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public String getResourceKey() {
      return this.resourceKey;
   }

   @OnlyIn(Dist.CLIENT)
   public static ChatVisibility getValue(int p_221252_0_) {
      return field_221255_d[MathHelper.normalizeAngle(p_221252_0_, field_221255_d.length)];
   }
}