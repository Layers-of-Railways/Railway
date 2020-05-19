package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GlyphProviderTypes {
   BITMAP("bitmap", TextureGlyphProvider.Factory::deserialize),
   TTF("ttf", TrueTypeGlyphProviderFactory::deserialize),
   LEGACY_UNICODE("legacy_unicode", UnicodeTextureGlyphProvider.Factory::deserialize);

   private static final Map<String, GlyphProviderTypes> TYPES_BY_NAME = Util.make(Maps.newHashMap(), (p_211639_0_) -> {
      for(GlyphProviderTypes glyphprovidertypes : values()) {
         p_211639_0_.put(glyphprovidertypes.name, glyphprovidertypes);
      }

   });
   private final String name;
   private final Function<JsonObject, IGlyphProviderFactory> factoryDeserializer;

   private GlyphProviderTypes(String typeIn, Function<JsonObject, IGlyphProviderFactory> factoryIn) {
      this.name = typeIn;
      this.factoryDeserializer = factoryIn;
   }

   public static GlyphProviderTypes byName(String typeIn) {
      GlyphProviderTypes glyphprovidertypes = TYPES_BY_NAME.get(typeIn);
      if (glyphprovidertypes == null) {
         throw new IllegalArgumentException("Invalid type: " + typeIn);
      } else {
         return glyphprovidertypes;
      }
   }

   public IGlyphProviderFactory getFactory(JsonObject jsonIn) {
      return this.factoryDeserializer.apply(jsonIn);
   }
}