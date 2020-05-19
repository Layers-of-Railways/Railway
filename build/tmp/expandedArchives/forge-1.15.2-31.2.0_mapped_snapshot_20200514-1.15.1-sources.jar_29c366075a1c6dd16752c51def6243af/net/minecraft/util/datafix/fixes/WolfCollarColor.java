package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class WolfCollarColor extends NamedEntityFix {
   public WolfCollarColor(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "EntityWolfColorFix", TypeReferences.ENTITY, "minecraft:wolf");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209655_1_) {
      return p_209655_1_.update("CollarColor", (p_209654_0_) -> {
         return p_209654_0_.createByte((byte)(15 - p_209654_0_.asInt(0)));
      });
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }
}