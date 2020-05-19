package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class PaintingDirection extends DataFix {
   private static final int[][] DIRECTIONS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

   public PaintingDirection(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   private Dynamic<?> doFix(Dynamic<?> p_209748_1_, boolean p_209748_2_, boolean p_209748_3_) {
      if ((p_209748_2_ || p_209748_3_) && !p_209748_1_.get("Facing").asNumber().isPresent()) {
         int i;
         if (p_209748_1_.get("Direction").asNumber().isPresent()) {
            i = p_209748_1_.get("Direction").asByte((byte)0) % DIRECTIONS.length;
            int[] aint = DIRECTIONS[i];
            p_209748_1_ = p_209748_1_.set("TileX", p_209748_1_.createInt(p_209748_1_.get("TileX").asInt(0) + aint[0]));
            p_209748_1_ = p_209748_1_.set("TileY", p_209748_1_.createInt(p_209748_1_.get("TileY").asInt(0) + aint[1]));
            p_209748_1_ = p_209748_1_.set("TileZ", p_209748_1_.createInt(p_209748_1_.get("TileZ").asInt(0) + aint[2]));
            p_209748_1_ = p_209748_1_.remove("Direction");
            if (p_209748_3_ && p_209748_1_.get("ItemRotation").asNumber().isPresent()) {
               p_209748_1_ = p_209748_1_.set("ItemRotation", p_209748_1_.createByte((byte)(p_209748_1_.get("ItemRotation").asByte((byte)0) * 2)));
            }
         } else {
            i = p_209748_1_.get("Dir").asByte((byte)0) % DIRECTIONS.length;
            p_209748_1_ = p_209748_1_.remove("Dir");
         }

         p_209748_1_ = p_209748_1_.set("Facing", p_209748_1_.createByte((byte)i));
      }

      return p_209748_1_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "Painting");
      OpticFinder<?> opticfinder = DSL.namedChoice("Painting", type);
      Type<?> type1 = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "ItemFrame");
      OpticFinder<?> opticfinder1 = DSL.namedChoice("ItemFrame", type1);
      Type<?> type2 = this.getInputSchema().getType(TypeReferences.ENTITY);
      TypeRewriteRule typerewriterule = this.fixTypeEverywhereTyped("EntityPaintingFix", type2, (p_206332_3_) -> {
         return p_206332_3_.updateTyped(opticfinder, type, (p_206330_1_) -> {
            return p_206330_1_.update(DSL.remainderFinder(), (p_207457_1_) -> {
               return this.doFix(p_207457_1_, true, false);
            });
         });
      });
      TypeRewriteRule typerewriterule1 = this.fixTypeEverywhereTyped("EntityItemFrameFix", type2, (p_206331_3_) -> {
         return p_206331_3_.updateTyped(opticfinder1, type1, (p_206329_1_) -> {
            return p_206329_1_.update(DSL.remainderFinder(), (p_207455_1_) -> {
               return this.doFix(p_207455_1_, false, true);
            });
         });
      });
      return TypeRewriteRule.seq(typerewriterule, typerewriterule1);
   }
}