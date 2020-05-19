package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerRenderer extends MobRenderer<RavagerEntity, RavagerModel> {
   private static final ResourceLocation RAVAGER_TEXTURES = new ResourceLocation("textures/entity/illager/ravager.png");

   public RavagerRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new RavagerModel(), 1.1F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(RavagerEntity entity) {
      return RAVAGER_TEXTURES;
   }
}