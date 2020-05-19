package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageMetadataSectionSerializer implements IMetadataSectionSerializer<LanguageMetadataSection> {
   public LanguageMetadataSection deserialize(JsonObject json) {
      Set<Language> set = Sets.newHashSet();

      for(Entry<String, JsonElement> entry : json.entrySet()) {
         String s = entry.getKey();
         if (s.length() > 16) {
            throw new JsonParseException("Invalid language->'" + s + "': language code must not be more than " + 16 + " characters long");
         }

         JsonObject jsonobject = JSONUtils.getJsonObject(entry.getValue(), "language");
         String s1 = JSONUtils.getString(jsonobject, "region");
         String s2 = JSONUtils.getString(jsonobject, "name");
         boolean flag = JSONUtils.getBoolean(jsonobject, "bidirectional", false);
         if (s1.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + s + "'->region: empty value");
         }

         if (s2.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + s + "'->name: empty value");
         }

         if (!set.add(new Language(s, s1, s2, flag))) {
            throw new JsonParseException("Duplicate language->'" + s + "' defined");
         }
      }

      return new LanguageMetadataSection(set);
   }

   /**
    * The name of this section type as it appears in JSON.
    */
   public String getSectionName() {
      return "language";
   }
}