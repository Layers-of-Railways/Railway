package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteRenderer<T extends Entity & IRendersAsItem> extends EntityRenderer<T> {
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
   private final float scale;
   private final boolean field_229126_f_;

   public SpriteRenderer(EntityRendererManager p_i226035_1_, net.minecraft.client.renderer.ItemRenderer p_i226035_2_, float p_i226035_3_, boolean p_i226035_4_) {
      super(p_i226035_1_);
      this.itemRenderer = p_i226035_2_;
      this.scale = p_i226035_3_;
      this.field_229126_f_ = p_i226035_4_;
   }

   public SpriteRenderer(EntityRendererManager renderManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn) {
      this(renderManagerIn, itemRendererIn, 1.0F, false);
   }

   protected int getBlockLight(T entityIn, float partialTicks) {
      return this.field_229126_f_ ? 15 : super.getBlockLight(entityIn, partialTicks);
   }

   public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      matrixStackIn.scale(this.scale, this.scale, this.scale);
      matrixStackIn.rotate(this.renderManager.getCameraOrientation());
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      this.itemRenderer.renderItem(((IRendersAsItem)entityIn).getItem(), ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(Entity entity) {
      return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
   }
}