package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryDumpReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator field_218434_c;

   public RegistryDumpReport(DataGenerator p_i50790_1_) {
      this.field_218434_c = p_i50790_1_;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      JsonObject jsonobject = new JsonObject();
      Registry.REGISTRY.keySet().forEach((p_218431_1_) -> {
         jsonobject.add(p_218431_1_.toString(), func_218432_a(Registry.REGISTRY.getOrDefault(p_218431_1_)));
      });
      Path path = this.field_218434_c.getOutputFolder().resolve("reports/registries.json");
      IDataProvider.save(GSON, cache, jsonobject, path);
   }

   private static <T> JsonElement func_218432_a(MutableRegistry<T> p_218432_0_) {
      JsonObject jsonobject = new JsonObject();
      if (p_218432_0_ instanceof DefaultedRegistry) {
         ResourceLocation resourcelocation = ((DefaultedRegistry)p_218432_0_).getDefaultKey();
         jsonobject.addProperty("default", resourcelocation.toString());
      }

      int j = Registry.REGISTRY.getId(p_218432_0_);
      jsonobject.addProperty("protocol_id", j);
      JsonObject jsonobject1 = new JsonObject();

      for(ResourceLocation resourcelocation1 : p_218432_0_.keySet()) {
         T t = p_218432_0_.getOrDefault(resourcelocation1);
         int i = p_218432_0_.getId(t);
         JsonObject jsonobject2 = new JsonObject();
         jsonobject2.addProperty("protocol_id", i);
         jsonobject1.add(resourcelocation1.toString(), jsonobject2);
      }

      jsonobject.add("entries", jsonobject1);
      return jsonobject;
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Registry Dump";
   }
}