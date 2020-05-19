package net.minecraft.resources.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;

public class PackMetadataSectionSerializer implements IMetadataSectionSerializer<PackMetadataSection> {
   public PackMetadataSection deserialize(JsonObject json) {
      ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(json.get("description"));
      if (itextcomponent == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int i = JSONUtils.getInt(json, "pack_format");
         return new PackMetadataSection(itextcomponent, i);
      }
   }

   /**
    * The name of this section type as it appears in JSON.
    */
   public String getSectionName() {
      return "pack";
   }
}