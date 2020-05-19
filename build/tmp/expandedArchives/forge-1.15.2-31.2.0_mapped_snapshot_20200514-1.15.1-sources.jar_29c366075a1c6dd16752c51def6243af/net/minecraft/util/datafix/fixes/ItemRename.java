package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public abstract class ItemRename extends DataFix {
   private final String name;

   public ItemRename(Schema outputSchema, String name) {
      super(outputSchema, false);
      this.name = name;
   }

   public TypeRewriteRule makeRule() {
      Type<Pair<String, String>> type = DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString());
      if (!Objects.equals(this.getInputSchema().getType(TypeReferences.ITEM_NAME), type)) {
         throw new IllegalStateException("item name type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, type, (p_211012_1_) -> {
            return (p_206354_1_) -> {
               return p_206354_1_.mapSecond(this::fixItem);
            };
         });
      }
   }

   protected abstract String fixItem(String p_206355_1_);

   public static DataFix create(Schema p_207476_0_, String p_207476_1_, final Function<String, String> p_207476_2_) {
      return new ItemRename(p_207476_0_, p_207476_1_) {
         protected String fixItem(String p_206355_1_) {
            return p_207476_2_.apply(p_206355_1_);
         }
      };
   }
}