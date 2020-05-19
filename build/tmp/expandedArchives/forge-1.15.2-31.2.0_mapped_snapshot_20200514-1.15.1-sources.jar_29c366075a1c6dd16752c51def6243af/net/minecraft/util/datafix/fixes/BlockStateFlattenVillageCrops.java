package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class BlockStateFlattenVillageCrops extends DataFix {
   public BlockStateFlattenVillageCrops(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this::fixTag);
   }

   private <T> Dynamic<T> fixTag(Dynamic<T> p_209677_1_) {
      return p_209677_1_.update("Children", BlockStateFlattenVillageCrops::updateChildren);
   }

   private static <T> Dynamic<T> updateChildren(Dynamic<T> p_210590_0_) {
      return p_210590_0_.asStreamOpt().map(BlockStateFlattenVillageCrops::updateChildren).map(p_210590_0_::createList).orElse(p_210590_0_);
   }

   private static Stream<? extends Dynamic<?>> updateChildren(Stream<? extends Dynamic<?>> p_210586_0_) {
      return p_210586_0_.map((Dynamic<?> p_210587_0_) -> {
         String s = p_210587_0_.get("id").asString("");
         if ("ViF".equals(s)) {
            return updateSingleField(p_210587_0_);
         } else {
            return "ViDF".equals(s) ? updateDoubleField(p_210587_0_) : p_210587_0_;
         }
      });
   }

   private static <T> Dynamic<T> updateSingleField(Dynamic<T> p_210588_0_) {
      p_210588_0_ = updateCrop(p_210588_0_, "CA");
      return updateCrop(p_210588_0_, "CB");
   }

   private static <T> Dynamic<T> updateDoubleField(Dynamic<T> p_210589_0_) {
      p_210589_0_ = updateCrop(p_210589_0_, "CA");
      p_210589_0_ = updateCrop(p_210589_0_, "CB");
      p_210589_0_ = updateCrop(p_210589_0_, "CC");
      return updateCrop(p_210589_0_, "CD");
   }

   private static <T> Dynamic<T> updateCrop(Dynamic<T> p_209676_0_, String p_209676_1_) {
      return p_209676_0_.get(p_209676_1_).asNumber().isPresent() ? p_209676_0_.set(p_209676_1_, BlockStateFlatteningMap.getFixedNBTForID(p_209676_0_.get(p_209676_1_).asInt(0) << 4)) : p_209676_0_;
   }
}