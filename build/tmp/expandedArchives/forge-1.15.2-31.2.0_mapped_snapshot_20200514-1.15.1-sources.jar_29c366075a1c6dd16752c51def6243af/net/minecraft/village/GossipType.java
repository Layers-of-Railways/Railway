package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum GossipType {
   MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
   MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
   MINOR_POSITIVE("minor_positive", 1, 200, 1, 5),
   MAJOR_POSITIVE("major_positive", 5, 100, 0, 100),
   TRADING("trading", 1, 25, 2, 20);

   public final String id;
   public final int weight;
   public final int max;
   public final int decayPerDay;
   public final int decayPerTransfer;
   private static final Map<String, GossipType> BY_ID = Stream.of(values()).collect(ImmutableMap.toImmutableMap((p_220930_0_) -> {
      return p_220930_0_.id;
   }, Function.identity()));

   private GossipType(String p_i50307_3_, int p_i50307_4_, int p_i50307_5_, int p_i50307_6_, int p_i50307_7_) {
      this.id = p_i50307_3_;
      this.weight = p_i50307_4_;
      this.max = p_i50307_5_;
      this.decayPerDay = p_i50307_6_;
      this.decayPerTransfer = p_i50307_7_;
   }

   @Nullable
   public static GossipType byId(String p_220929_0_) {
      return BY_ID.get(p_220929_0_);
   }
}