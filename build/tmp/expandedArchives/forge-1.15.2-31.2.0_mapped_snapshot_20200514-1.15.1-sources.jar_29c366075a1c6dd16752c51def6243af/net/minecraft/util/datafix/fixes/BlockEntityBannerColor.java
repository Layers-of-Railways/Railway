package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class BlockEntityBannerColor extends NamedEntityFix {
   public BlockEntityBannerColor(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209643_1_) {
      p_209643_1_ = p_209643_1_.update("Base", (p_209642_0_) -> {
         return p_209642_0_.createInt(15 - p_209642_0_.asInt(0));
      });
      p_209643_1_ = p_209643_1_.update("Patterns", (p_209641_0_) -> {
         return DataFixUtils.orElse(p_209641_0_.asStreamOpt().map((p_209639_0_) -> {
            return p_209639_0_.map((p_210561_0_) -> {
               return p_210561_0_.update("Color", (p_209644_0_) -> {
                  return p_209644_0_.createInt(15 - p_209644_0_.asInt(0));
               });
            });
         }).map(p_209641_0_::createList), p_209641_0_);
      });
      return p_209643_1_;
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }
}