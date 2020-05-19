package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfRenderer extends MobRenderer<WolfEntity, WolfModel<WolfEntity>> {
   private static final ResourceLocation WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf.png");
   private static final ResourceLocation TAMED_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
   private static final ResourceLocation ANGRY_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

   public WolfRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new WolfModel<>(), 0.5F);
      this.addLayer(new WolfCollarLayer(this));
   }

   /**
    * Defines what float the third param in setRotationAngles of ModelBase is
    */
   protected float handleRotationFloat(WolfEntity livingBase, float partialTicks) {
      return livingBase.getTailRotation();
   }

   public void render(WolfEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      if (entityIn.isWolfWet()) {
         float f = entityIn.getBrightness() * entityIn.getShadingWhileWet(partialTicks);
         this.entityModel.setTint(f, f, f);
      }

      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      if (entityIn.isWolfWet()) {
         this.entityModel.setTint(1.0F, 1.0F, 1.0F);
      }

   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(WolfEntity entity) {
      if (entity.isTamed()) {
         return TAMED_WOLF_TEXTURES;
      } else {
         return entity.isAngry() ? ANGRY_WOLF_TEXTURES : WOLF_TEXTURES;
      }
   }
}