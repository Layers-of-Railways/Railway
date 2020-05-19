package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TippedArrowRenderer extends ArrowRenderer<ArrowEntity> {
   public static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");
   public static final ResourceLocation RES_TIPPED_ARROW = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

   public TippedArrowRenderer(EntityRendererManager manager) {
      super(manager);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ArrowEntity entity) {
      return entity.getColor() > 0 ? RES_TIPPED_ARROW : RES_ARROW;
   }
}