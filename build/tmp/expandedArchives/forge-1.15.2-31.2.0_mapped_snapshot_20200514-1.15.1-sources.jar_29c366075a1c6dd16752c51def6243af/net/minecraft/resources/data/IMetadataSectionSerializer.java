package net.minecraft.resources.data;

import com.google.gson.JsonObject;

public interface IMetadataSectionSerializer<T> {
   /**
    * The name of this section type as it appears in JSON.
    */
   String getSectionName();

   T deserialize(JsonObject json);
}