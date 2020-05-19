package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;

public class BookPagesStrictJSON extends DataFix {
   public BookPagesStrictJSON(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209633_1_) {
      return p_209633_1_.update("pages", (p_212821_1_) -> {
         return DataFixUtils.orElse(p_212821_1_.asStreamOpt().map((p_209630_0_) -> {
            return p_209630_0_.map((p_209631_0_) -> {
               if (!p_209631_0_.asString().isPresent()) {
                  return p_209631_0_;
               } else {
                  String s = p_209631_0_.asString("");
                  ITextComponent itextcomponent = null;
                  if (!"null".equals(s) && !StringUtils.isEmpty(s)) {
                     if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' || s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}') {
                        try {
                           itextcomponent = JSONUtils.fromJson(SignStrictJSON.GSON, s, ITextComponent.class, true);
                           if (itextcomponent == null) {
                              itextcomponent = new StringTextComponent("");
                           }
                        } catch (JsonParseException var6) {
                           ;
                        }

                        if (itextcomponent == null) {
                           try {
                              itextcomponent = ITextComponent.Serializer.fromJson(s);
                           } catch (JsonParseException var5) {
                              ;
                           }
                        }

                        if (itextcomponent == null) {
                           try {
                              itextcomponent = ITextComponent.Serializer.fromJsonLenient(s);
                           } catch (JsonParseException var4) {
                              ;
                           }
                        }

                        if (itextcomponent == null) {
                           itextcomponent = new StringTextComponent(s);
                        }
                     } else {
                        itextcomponent = new StringTextComponent(s);
                     }
                  } else {
                     itextcomponent = new StringTextComponent("");
                  }

                  return p_209631_0_.createString(ITextComponent.Serializer.toJson(itextcomponent));
               }
            });
         }).map(p_209633_1_::createList), p_209633_1_.emptyList());
      });
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<?> opticfinder = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", type, (p_207415_2_) -> {
         return p_207415_2_.updateTyped(opticfinder, (p_207417_1_) -> {
            return p_207417_1_.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}