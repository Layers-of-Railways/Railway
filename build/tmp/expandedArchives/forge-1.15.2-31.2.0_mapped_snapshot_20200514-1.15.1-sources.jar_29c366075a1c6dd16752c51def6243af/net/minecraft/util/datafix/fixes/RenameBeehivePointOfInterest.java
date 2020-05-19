package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;

public class RenameBeehivePointOfInterest extends PointOfInterestRename {
   public RenameBeehivePointOfInterest(Schema p_i225700_1_) {
      super(p_i225700_1_, false);
   }

   protected String func_225501_a_(String p_225501_1_) {
      return p_225501_1_.equals("minecraft:bee_hive") ? "minecraft:beehive" : p_225501_1_;
   }
}