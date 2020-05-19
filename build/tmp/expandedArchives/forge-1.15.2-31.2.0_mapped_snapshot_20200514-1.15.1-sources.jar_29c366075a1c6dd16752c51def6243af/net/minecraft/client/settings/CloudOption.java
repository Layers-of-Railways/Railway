package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum CloudOption {
   OFF(0, "options.off"),
   FAST(1, "options.clouds.fast"),
   FANCY(2, "options.clouds.fancy");

   private static final CloudOption[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(CloudOption::getId)).toArray((p_216805_0_) -> {
      return new CloudOption[p_216805_0_];
   });
   private final int id;
   private final String key;

   private CloudOption(int id, String keyIn) {
      this.id = id;
      this.key = keyIn;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static CloudOption byId(int id) {
      return BY_ID[MathHelper.normalizeAngle(id, BY_ID.length)];
   }
}