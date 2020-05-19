package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ColorlessShulkerEntityFix extends NamedEntityFix {
   public ColorlessShulkerEntityFix(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "Colorless shulker entity fix", TypeReferences.ENTITY, "minecraft:shulker");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), (p_207421_0_) -> {
         return p_207421_0_.get("Color").asInt(0) == 10 ? p_207421_0_.set("Color", p_207421_0_.createByte((byte)16)) : p_207421_0_;
      });
   }
}