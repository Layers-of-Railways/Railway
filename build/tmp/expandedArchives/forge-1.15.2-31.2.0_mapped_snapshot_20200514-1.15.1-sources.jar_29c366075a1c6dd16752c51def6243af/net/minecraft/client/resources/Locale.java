package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Locale {
   private static final Gson GSON = new Gson();
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   protected final Map<String, String> properties = Maps.newHashMap();

   public synchronized void func_195811_a(IResourceManager p_195811_1_, List<String> p_195811_2_) {
      this.properties.clear();

      for(String s : p_195811_2_) {
         String s1 = String.format("lang/%s.json", s);

         for(String s2 : p_195811_1_.getResourceNamespaces()) {
            try {
               ResourceLocation resourcelocation = new ResourceLocation(s2, s1);
               this.loadLocaleData(p_195811_1_.getAllResources(resourcelocation));
            } catch (FileNotFoundException var9) {
               ;
            } catch (Exception exception) {
               LOGGER.warn("Skipped language file: {}:{} ({})", s2, s1, exception.toString());
            }
         }
      }

   }

   /**
    * Loads the locale data for the list of resources.
    */
   private void loadLocaleData(List<IResource> resourcesList) {
      for(IResource iresource : resourcesList) {
         InputStream inputstream = iresource.getInputStream();

         try {
            this.loadLocaleData(inputstream);
         } finally {
            IOUtils.closeQuietly(inputstream);
         }
      }

   }

   private void loadLocaleData(InputStream inputStreamIn) {
      JsonElement jsonelement = GSON.fromJson(new InputStreamReader(inputStreamIn, StandardCharsets.UTF_8), JsonElement.class);
      JsonObject jsonobject = JSONUtils.getJsonObject(jsonelement, "strings");

      for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
         String s = PATTERN.matcher(JSONUtils.getString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
         this.properties.put(entry.getKey(), s);
      }

   }

   /**
    * Returns the translation, or the key itself if the key could not be translated.
    */
   private String translateKeyPrivate(String translateKey) {
      String s = this.properties.get(translateKey);
      return s == null ? translateKey : s;
   }

   /**
    * Calls String.format(translateKey(key), params)
    */
   public String formatMessage(String translateKey, Object[] parameters) {
      String s = this.translateKeyPrivate(translateKey);

      try {
         return String.format(s, parameters);
      } catch (IllegalFormatException var5) {
         return "Format error: " + s;
      }
   }

   public boolean hasKey(String key) {
      return this.properties.containsKey(key);
   }
}