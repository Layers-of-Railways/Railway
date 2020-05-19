package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ShulkerBoxEntityColor extends NamedEntityFix {
   public ShulkerBoxEntityColor(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "EntityShulkerColorFix", TypeReferences.ENTITY, "minecraft:shulker");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209653_1_) {
      return !p_209653_1_.get("Color").map(Dynamic::asNumber).isPresent() ? p_209653_1_.set("Color", p_209653_1_.createByte((byte)10)) : p_209653_1_;
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }
}