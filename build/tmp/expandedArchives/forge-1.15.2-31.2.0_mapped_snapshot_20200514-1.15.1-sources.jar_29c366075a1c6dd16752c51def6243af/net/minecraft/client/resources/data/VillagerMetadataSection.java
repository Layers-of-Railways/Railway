package net.minecraft.client.resources.data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.texture.TextureAtlasSpriteStitcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerMetadataSection {
   public static final TextureAtlasSpriteStitcher field_217827_a = new TextureAtlasSpriteStitcher();
   private final VillagerMetadataSection.HatType field_217828_b;

   public VillagerMetadataSection(VillagerMetadataSection.HatType p_i50904_1_) {
      this.field_217828_b = p_i50904_1_;
   }

   public VillagerMetadataSection.HatType func_217826_a() {
      return this.field_217828_b;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum HatType {
      NONE("none"),
      PARTIAL("partial"),
      FULL("full");

      private static final Map<String, VillagerMetadataSection.HatType> field_217824_d = Arrays.stream(values()).collect(Collectors.toMap(VillagerMetadataSection.HatType::func_217823_a, (p_217822_0_) -> {
         return p_217822_0_;
      }));
      private final String field_217825_e;

      private HatType(String p_i50447_3_) {
         this.field_217825_e = p_i50447_3_;
      }

      public String func_217823_a() {
         return this.field_217825_e;
      }

      public static VillagerMetadataSection.HatType func_217821_a(String p_217821_0_) {
         return field_217824_d.getOrDefault(p_217821_0_, NONE);
      }
   }
}