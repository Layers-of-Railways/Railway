package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectralArrowRenderer extends ArrowRenderer<SpectralArrowEntity> {
   public static final ResourceLocation RES_SPECTRAL_ARROW = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowRenderer(EntityRendererManager manager) {
      super(manager);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(SpectralArrowEntity entity) {
      return RES_SPECTRAL_ARROW;
   }
}