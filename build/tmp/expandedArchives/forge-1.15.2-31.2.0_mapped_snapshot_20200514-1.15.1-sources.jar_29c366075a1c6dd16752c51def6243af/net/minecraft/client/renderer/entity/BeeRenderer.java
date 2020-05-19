package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BeeModel;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeRenderer extends MobRenderer<BeeEntity, BeeModel<BeeEntity>> {
   private static final ResourceLocation field_229040_a_ = new ResourceLocation("textures/entity/bee/bee_angry.png");
   private static final ResourceLocation field_229041_g_ = new ResourceLocation("textures/entity/bee/bee_angry_nectar.png");
   private static final ResourceLocation field_229042_h_ = new ResourceLocation("textures/entity/bee/bee.png");
   private static final ResourceLocation field_229043_i_ = new ResourceLocation("textures/entity/bee/bee_nectar.png");

   public BeeRenderer(EntityRendererManager p_i226033_1_) {
      super(p_i226033_1_, new BeeModel<>(), 0.4F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(BeeEntity entity) {
      if (entity.isAngry()) {
         return entity.hasNectar() ? field_229041_g_ : field_229040_a_;
      } else {
         return entity.hasNectar() ? field_229043_i_ : field_229042_h_;
      }
   }
}