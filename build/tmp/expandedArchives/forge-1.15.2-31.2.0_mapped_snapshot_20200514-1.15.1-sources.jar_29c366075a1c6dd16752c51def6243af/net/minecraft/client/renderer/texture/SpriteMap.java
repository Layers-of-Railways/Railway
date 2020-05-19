package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteMap implements AutoCloseable {
   private final Map<ResourceLocation, AtlasTexture> atlasTextures;

   public SpriteMap(Collection<AtlasTexture> atlasTexturesIn) {
      this.atlasTextures = atlasTexturesIn.stream().collect(Collectors.toMap(AtlasTexture::getTextureLocation, Function.identity()));
   }

   public AtlasTexture getAtlasTexture(ResourceLocation locationIn) {
      return this.atlasTextures.get(locationIn);
   }

   public TextureAtlasSprite getSprite(Material materialIn) {
      return this.atlasTextures.get(materialIn.getAtlasLocation()).getSprite(materialIn.getTextureLocation());
   }

   public void close() {
      this.atlasTextures.values().forEach(AtlasTexture::clear);
      this.atlasTextures.clear();
   }
}