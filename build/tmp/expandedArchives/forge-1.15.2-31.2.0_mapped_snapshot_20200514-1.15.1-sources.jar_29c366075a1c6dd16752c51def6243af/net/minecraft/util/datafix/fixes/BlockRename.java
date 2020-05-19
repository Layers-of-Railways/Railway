package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public abstract class BlockRename extends DataFix {
   private final String name;

   public BlockRename(Schema outputSchema, String name) {
      super(outputSchema, false);
      this.name = name;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
      Type<Pair<String, String>> type1 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.namespacedString());
      if (!Objects.equals(type, type1)) {
         throw new IllegalStateException("block type is not what was expected.");
      } else {
         TypeRewriteRule typerewriterule = this.fixTypeEverywhere(this.name + " for block", type1, (p_209705_1_) -> {
            return (p_206308_1_) -> {
               return p_206308_1_.mapSecond(this::fixBlock);
            };
         });
         TypeRewriteRule typerewriterule1 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), (p_209704_1_) -> {
            return p_209704_1_.update(DSL.remainderFinder(), (p_207439_1_) -> {
               Optional<String> optional = p_207439_1_.get("Name").asString();
               return optional.isPresent() ? p_207439_1_.set("Name", p_207439_1_.createString(this.fixBlock(optional.get()))) : p_207439_1_;
            });
         });
         return TypeRewriteRule.seq(typerewriterule, typerewriterule1);
      }
   }

   protected abstract String fixBlock(String p_206309_1_);

   public static DataFix create(Schema p_207437_0_, String p_207437_1_, final Function<String, String> p_207437_2_) {
      return new BlockRename(p_207437_0_, p_207437_1_) {
         protected String fixBlock(String p_206309_1_) {
            return p_207437_2_.apply(p_206309_1_);
         }
      };
   }
}