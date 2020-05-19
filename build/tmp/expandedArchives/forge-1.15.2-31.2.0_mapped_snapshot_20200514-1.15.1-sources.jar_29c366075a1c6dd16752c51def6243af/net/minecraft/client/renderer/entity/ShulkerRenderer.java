package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.layers.ShulkerColorLayer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerRenderer extends MobRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>> {
   public static final ResourceLocation field_204402_a = new ResourceLocation("textures/" + Atlases.DEFAULT_SHULKER_TEXTURE.getTextureLocation().getPath() + ".png");
   public static final ResourceLocation[] SHULKER_ENDERGOLEM_TEXTURE = Atlases.SHULKER_TEXTURES.stream().map((p_229125_0_) -> {
      return new ResourceLocation("textures/" + p_229125_0_.getTextureLocation().getPath() + ".png");
   }).toArray((p_229124_0_) -> {
      return new ResourceLocation[p_229124_0_];
   });

   public ShulkerRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new ShulkerModel<>(), 0.0F);
      this.addLayer(new ShulkerColorLayer(this));
   }

   public Vec3d getRenderOffset(ShulkerEntity entityIn, float partialTicks) {
      int i = entityIn.getClientTeleportInterp();
      if (i > 0 && entityIn.isAttachedToBlock()) {
         BlockPos blockpos = entityIn.getAttachmentPos();
         BlockPos blockpos1 = entityIn.getOldAttachPos();
         double d0 = (double)((float)i - partialTicks) / 6.0D;
         d0 = d0 * d0;
         double d1 = (double)(blockpos.getX() - blockpos1.getX()) * d0;
         double d2 = (double)(blockpos.getY() - blockpos1.getY()) * d0;
         double d3 = (double)(blockpos.getZ() - blockpos1.getZ()) * d0;
         return new Vec3d(-d1, -d2, -d3);
      } else {
         return super.getRenderOffset(entityIn, partialTicks);
      }
   }

   public boolean shouldRender(ShulkerEntity livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
         return true;
      } else {
         if (livingEntityIn.getClientTeleportInterp() > 0 && livingEntityIn.isAttachedToBlock()) {
            Vec3d vec3d = new Vec3d(livingEntityIn.getAttachmentPos());
            Vec3d vec3d1 = new Vec3d(livingEntityIn.getOldAttachPos());
            if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y, vec3d.z))) {
               return true;
            }
         }

         return false;
      }
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ShulkerEntity entity) {
      return entity.getColor() == null ? field_204402_a : SHULKER_ENDERGOLEM_TEXTURE[entity.getColor().getId()];
   }

   protected void applyRotations(ShulkerEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      matrixStackIn.translate(0.0D, 0.5D, 0.0D);
      matrixStackIn.rotate(entityLiving.getAttachmentFacing().getOpposite().getRotation());
      matrixStackIn.translate(0.0D, -0.5D, 0.0D);
   }

   protected void preRenderCallback(ShulkerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      float f = 0.999F;
      matrixStackIn.scale(0.999F, 0.999F, 0.999F);
   }
}