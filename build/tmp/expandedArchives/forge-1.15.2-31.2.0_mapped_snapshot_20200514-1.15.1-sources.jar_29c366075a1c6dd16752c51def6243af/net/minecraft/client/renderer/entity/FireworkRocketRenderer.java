package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkRocketRenderer extends EntityRenderer<FireworkRocketEntity> {
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;

   public FireworkRocketRenderer(EntityRendererManager renderManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn) {
      super(renderManagerIn);
      this.itemRenderer = itemRendererIn;
   }

   public void render(FireworkRocketEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      matrixStackIn.rotate(this.renderManager.getCameraOrientation());
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      if (entityIn.func_213889_i()) {
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
      }

      this.itemRenderer.renderItem(entityIn.getItem(), ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(FireworkRocketEntity entity) {
      return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
   }
}