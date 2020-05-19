package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.ResourceLocation;

public interface ICriterionInstance {
   ResourceLocation getId();

   default JsonElement serialize() {
      return JsonNull.INSTANCE;
   }
}