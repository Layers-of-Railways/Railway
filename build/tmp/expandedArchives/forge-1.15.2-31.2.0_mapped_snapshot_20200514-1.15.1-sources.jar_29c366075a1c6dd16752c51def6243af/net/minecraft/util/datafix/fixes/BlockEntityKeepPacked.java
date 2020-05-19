package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class BlockEntityKeepPacked extends NamedEntityFix {
   public BlockEntityKeepPacked(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "BlockEntityKeepPacked", TypeReferences.BLOCK_ENTITY, "DUMMY");
   }

   private static Dynamic<?> fixTag(Dynamic<?> p_209645_0_) {
      return p_209645_0_.set("keepPacked", p_209645_0_.createBoolean(true));
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), BlockEntityKeepPacked::fixTag);
   }
}