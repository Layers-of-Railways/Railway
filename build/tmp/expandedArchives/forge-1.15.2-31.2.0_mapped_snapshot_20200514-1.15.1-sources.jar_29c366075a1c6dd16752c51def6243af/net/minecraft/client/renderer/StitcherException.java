package net.minecraft.client.renderer;

import java.util.Collection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StitcherException extends RuntimeException {
   private final Collection<TextureAtlasSprite.Info> spriteInfos;

   public StitcherException(TextureAtlasSprite.Info spriteInfoIn, Collection<TextureAtlasSprite.Info> spriteInfosIn) {
      super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", spriteInfoIn.getSpriteLocation(), spriteInfoIn.getSpriteWidth(), spriteInfoIn.getSpriteHeight()));
      this.spriteInfos = spriteInfosIn;
   }

   public Collection<TextureAtlasSprite.Info> getSpriteInfos() {
      return this.spriteInfos;
   }
}