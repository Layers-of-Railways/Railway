package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TridentRenderer extends EntityRenderer<TridentEntity> {
   public static final ResourceLocation TRIDENT = new ResourceLocation("textures/entity/trident.png");
   private final TridentModel tridentModel = new TridentModel();

   public TridentRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(TridentEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
      matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch) + 90.0F));
      IVertexBuilder ivertexbuilder = net.minecraft.client.renderer.ItemRenderer.getBuffer(bufferIn, this.tridentModel.getRenderType(this.getEntityTexture(entityIn)), false, entityIn.func_226572_w_());
      this.tridentModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(TridentEntity entity) {
      return TRIDENT;
   }
}