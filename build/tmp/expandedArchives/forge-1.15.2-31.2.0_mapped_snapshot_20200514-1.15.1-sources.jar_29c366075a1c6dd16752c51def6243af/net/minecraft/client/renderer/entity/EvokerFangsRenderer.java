package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EvokerFangsModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EvokerFangsRenderer extends EntityRenderer<EvokerFangsEntity> {
   private static final ResourceLocation EVOKER_ILLAGER_FANGS = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final EvokerFangsModel<EvokerFangsEntity> model = new EvokerFangsModel<>();

   public EvokerFangsRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(EvokerFangsEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      float f = entityIn.getAnimationProgress(partialTicks);
      if (f != 0.0F) {
         float f1 = 2.0F;
         if (f > 0.9F) {
            f1 = (float)((double)f1 * ((1.0D - (double)f) / (double)0.1F));
         }

         matrixStackIn.push();
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F - entityIn.rotationYaw));
         matrixStackIn.scale(-f1, -f1, f1);
         float f2 = 0.03125F;
         matrixStackIn.translate(0.0D, (double)-0.626F, 0.0D);
         matrixStackIn.scale(0.5F, 0.5F, 0.5F);
         this.model.setRotationAngles(entityIn, f, 0.0F, 0.0F, entityIn.rotationYaw, entityIn.rotationPitch);
         IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(EVOKER_ILLAGER_FANGS));
         this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.pop();
         super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      }
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(EvokerFangsEntity entity) {
      return EVOKER_ILLAGER_FANGS;
   }
}