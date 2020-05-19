package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class ElderGuardianSplit extends EntityRenameHelper {
   public ElderGuardianSplit(Schema outputSchema, boolean changesType) {
      super("EntityElderGuardianSplitFix", outputSchema, changesType);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String name, Dynamic<?> tag) {
      return Pair.of(Objects.equals(name, "Guardian") && tag.get("Elder").asBoolean(false) ? "ElderGuardian" : name, tag);
   }
}