package net.minecraft.client.resources.data;

import java.util.Collection;
import net.minecraft.client.resources.Language;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageMetadataSection {
   public static final LanguageMetadataSectionSerializer field_195818_a = new LanguageMetadataSectionSerializer();
   private final Collection<Language> languages;

   public LanguageMetadataSection(Collection<Language> languagesIn) {
      this.languages = languagesIn;
   }

   public Collection<Language> getLanguages() {
      return this.languages;
   }
}