package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.IResourcePack;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanguageManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final Locale CURRENT_LOCALE = new Locale();
   private String currentLanguage;
   private final Map<String, Language> languageMap = Maps.newHashMap();

   public LanguageManager(String p_i48112_1_) {
      this.currentLanguage = p_i48112_1_;
      I18n.setLocale(CURRENT_LOCALE);
   }

   public void parseLanguageMetadata(List<IResourcePack> resourcesPacks) {
      this.languageMap.clear();

      for(IResourcePack iresourcepack : resourcesPacks) {
         try {
            LanguageMetadataSection languagemetadatasection = iresourcepack.getMetadata(LanguageMetadataSection.field_195818_a);
            if (languagemetadatasection != null) {
               for(Language language : languagemetadatasection.getLanguages()) {
                  if (!this.languageMap.containsKey(language.getCode())) {
                     this.languageMap.put(language.getCode(), language);
                  }
               }
            }
         } catch (IOException | RuntimeException runtimeexception) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", iresourcepack.getName(), runtimeexception);
         }
      }

   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      List<String> list = Lists.newArrayList("en_us");
      if (!"en_us".equals(this.currentLanguage)) {
         list.add(this.currentLanguage);
      }

      CURRENT_LOCALE.func_195811_a(resourceManager, list);
      LanguageMap.replaceWith(CURRENT_LOCALE.properties);
   }

   public boolean isCurrentLanguageBidirectional() {
      return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
   }

   public void setCurrentLanguage(Language currentLanguageIn) {
      this.currentLanguage = currentLanguageIn.getCode();
   }

   public Language getCurrentLanguage() {
      String s = this.languageMap.containsKey(this.currentLanguage) ? this.currentLanguage : "en_us";
      return this.languageMap.get(s);
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languageMap.values());
   }

   public Language getLanguage(String p_191960_1_) {
      return this.languageMap.get(p_191960_1_);
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.LANGUAGES;
   }
}