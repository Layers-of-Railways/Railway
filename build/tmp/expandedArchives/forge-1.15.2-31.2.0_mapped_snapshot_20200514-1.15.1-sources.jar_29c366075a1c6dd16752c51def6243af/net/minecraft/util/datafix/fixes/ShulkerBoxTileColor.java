package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ShulkerBoxTileColor extends NamedEntityFix {
   public ShulkerBoxTileColor(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "BlockEntityShulkerBoxColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:shulker_box");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), (p_207420_0_) -> {
         return p_207420_0_.remove("Color");
      });
   }
}