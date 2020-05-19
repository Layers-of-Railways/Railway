package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class CatTypeFix extends NamedEntityFix {
   public CatTypeFix(Schema p_i50432_1_, boolean p_i50432_2_) {
      super(p_i50432_1_, p_i50432_2_, "CatTypeFix", TypeReferences.ENTITY, "minecraft:cat");
   }

   public Dynamic<?> func_219810_a(Dynamic<?> p_219810_1_) {
      return p_219810_1_.get("CatType").asInt(0) == 9 ? p_219810_1_.set("CatType", p_219810_1_.createInt(10)) : p_219810_1_;
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::func_219810_a);
   }
}