package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CowRenderer extends MobRenderer<CowEntity, CowModel<CowEntity>> {
   private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

   public CowRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new CowModel<>(), 0.7F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(CowEntity entity) {
      return COW_TEXTURES;
   }
}