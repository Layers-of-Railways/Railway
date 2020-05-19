package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ArmorStandSilent extends NamedEntityFix {
   public ArmorStandSilent(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209650_1_) {
      return p_209650_1_.get("Silent").asBoolean(false) && !p_209650_1_.get("Marker").asBoolean(false) ? p_209650_1_.remove("Silent") : p_209650_1_;
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }
}