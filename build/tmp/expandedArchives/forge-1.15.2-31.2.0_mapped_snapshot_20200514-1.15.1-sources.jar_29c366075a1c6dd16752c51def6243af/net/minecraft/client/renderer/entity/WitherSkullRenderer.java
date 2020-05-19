package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitherSkullRenderer extends EntityRenderer<WitherSkullEntity> {
   private static final ResourceLocation INVULNERABLE_WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither.png");
   private final GenericHeadModel skeletonHeadModel = new GenericHeadModel();

   public WitherSkullRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected int getBlockLight(WitherSkullEntity entityIn, float partialTicks) {
      return 15;
   }

   public void render(WitherSkullEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
      float f = MathHelper.rotLerp(entityIn.prevRotationYaw, entityIn.rotationYaw, partialTicks);
      float f1 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.skeletonHeadModel.getRenderType(this.getEntityTexture(entityIn)));
      this.skeletonHeadModel.func_225603_a_(0.0F, f, f1);
      this.skeletonHeadModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(WitherSkullEntity entity) {
      return entity.isSkullInvulnerable() ? INVULNERABLE_WITHER_TEXTURES : WITHER_TEXTURES;
   }
}