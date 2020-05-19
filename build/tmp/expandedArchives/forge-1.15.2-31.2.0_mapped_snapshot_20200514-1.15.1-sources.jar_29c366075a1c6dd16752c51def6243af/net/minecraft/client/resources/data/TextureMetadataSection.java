package net.minecraft.client.resources.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureMetadataSection {
   public static final TextureMetadataSectionSerializer SERIALIZER = new TextureMetadataSectionSerializer();
   private final boolean textureBlur;
   private final boolean textureClamp;

   public TextureMetadataSection(boolean textureBlurIn, boolean textureClampIn) {
      this.textureBlur = textureBlurIn;
      this.textureClamp = textureClampIn;
   }

   public boolean getTextureBlur() {
      return this.textureBlur;
   }

   public boolean getTextureClamp() {
      return this.textureClamp;
   }
}