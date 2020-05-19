package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class ZombieSplit extends EntityRenameHelper {
   public ZombieSplit(Schema outputSchema, boolean changesType) {
      super("EntityZombieSplitFix", outputSchema, changesType);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String name, Dynamic<?> tag) {
      if (Objects.equals("Zombie", name)) {
         String s = "Zombie";
         int i = tag.get("ZombieType").asInt(0);
         switch(i) {
         case 0:
         default:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            s = "ZombieVillager";
            tag = tag.set("Profession", tag.createInt(i - 1));
            break;
         case 6:
            s = "Husk";
         }

         tag = tag.remove("ZombieType");
         return Pair.of(s, tag);
      } else {
         return Pair.of(name, tag);
      }
   }
}