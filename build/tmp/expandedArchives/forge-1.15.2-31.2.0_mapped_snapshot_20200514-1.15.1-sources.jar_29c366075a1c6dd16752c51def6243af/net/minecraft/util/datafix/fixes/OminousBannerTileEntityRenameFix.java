package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class OminousBannerTileEntityRenameFix extends NamedEntityFix {
   public OminousBannerTileEntityRenameFix(Schema p_i51509_1_, boolean p_i51509_2_) {
      super(p_i51509_1_, p_i51509_2_, "OminousBannerBlockEntityRenameFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::func_222992_a);
   }

   private Dynamic<?> func_222992_a(Dynamic<?> p_222992_1_) {
      Optional<String> optional = p_222992_1_.get("CustomName").asString();
      if (optional.isPresent()) {
         String s = optional.get();
         s = s.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
         return p_222992_1_.set("CustomName", p_222992_1_.createString(s));
      } else {
         return p_222992_1_;
      }
   }
}