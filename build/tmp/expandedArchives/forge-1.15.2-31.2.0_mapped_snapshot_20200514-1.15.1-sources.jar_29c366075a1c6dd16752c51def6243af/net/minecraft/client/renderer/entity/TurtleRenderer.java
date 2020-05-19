package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.TurtleModel;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TurtleRenderer extends MobRenderer<TurtleEntity, TurtleModel<TurtleEntity>> {
   private static final ResourceLocation BIG_SEA_TURTLE = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public TurtleRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new TurtleModel<>(0.0F), 0.7F);
   }

   public void render(TurtleEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      if (entityIn.isChild()) {
         this.shadowSize *= 0.5F;
      }

      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(TurtleEntity entity) {
      return BIG_SEA_TURTLE;
   }
}