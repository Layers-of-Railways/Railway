package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.LeashKnotModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeashKnotRenderer extends EntityRenderer<LeashKnotEntity> {
   private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("textures/entity/lead_knot.png");
   private final LeashKnotModel<LeashKnotEntity> leashKnotModel = new LeashKnotModel<>();

   public LeashKnotRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(LeashKnotEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
      this.leashKnotModel.setRotationAngles(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.leashKnotModel.getRenderType(LEASH_KNOT_TEXTURES));
      this.leashKnotModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(LeashKnotEntity entity) {
      return LEASH_KNOT_TEXTURES;
   }
}