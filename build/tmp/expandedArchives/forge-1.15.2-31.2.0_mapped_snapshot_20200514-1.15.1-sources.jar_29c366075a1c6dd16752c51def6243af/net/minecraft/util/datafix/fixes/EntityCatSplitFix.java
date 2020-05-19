package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityCatSplitFix extends EntityRenameHelper {
   public EntityCatSplitFix(Schema p_i50428_1_, boolean p_i50428_2_) {
      super("EntityCatSplitFix", p_i50428_1_, p_i50428_2_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String name, Dynamic<?> tag) {
      if (Objects.equals("minecraft:ocelot", name)) {
         int i = tag.get("CatType").asInt(0);
         if (i == 0) {
            String s = tag.get("Owner").asString("");
            String s1 = tag.get("OwnerUUID").asString("");
            if (s.length() > 0 || s1.length() > 0) {
               tag.set("Trusting", tag.createBoolean(true));
            }
         } else if (i > 0 && i < 4) {
            tag = tag.set("CatType", tag.createInt(i));
            tag = tag.set("OwnerUUID", tag.createString(tag.get("OwnerUUID").asString("")));
            return Pair.of("minecraft:cat", tag);
         }
      }

      return Pair.of(name, tag);
   }
}