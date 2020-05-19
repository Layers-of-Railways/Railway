package net.minecraft.client.renderer.texture;

import java.util.stream.Stream;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PotionSpriteUploader extends SpriteUploader {
   public PotionSpriteUploader(TextureManager textureManagerIn) {
      super(textureManagerIn, new ResourceLocation("textures/atlas/mob_effects.png"), "mob_effect");
   }

   protected Stream<ResourceLocation> getResourceLocations() {
      return Registry.EFFECTS.keySet().stream();
   }

   public TextureAtlasSprite getSprite(Effect effectIn) {
      return this.getSprite(Registry.EFFECTS.getKey(effectIn));
   }
}