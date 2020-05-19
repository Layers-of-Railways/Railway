package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AgeableModel<E extends Entity> extends EntityModel<E> {
   private final boolean isChildHeadScaled;
   private final float childHeadOffsetY;
   private final float childHeadOffsetZ;
   private final float childHeadScale;
   private final float childBodyScale;
   private final float childBodyOffsetY;

   protected AgeableModel(boolean p_i225943_1_, float p_i225943_2_, float p_i225943_3_) {
      this(p_i225943_1_, p_i225943_2_, p_i225943_3_, 2.0F, 2.0F, 24.0F);
   }

   protected AgeableModel(boolean p_i225944_1_, float p_i225944_2_, float p_i225944_3_, float p_i225944_4_, float p_i225944_5_, float p_i225944_6_) {
      this(RenderType::getEntityCutoutNoCull, p_i225944_1_, p_i225944_2_, p_i225944_3_, p_i225944_4_, p_i225944_5_, p_i225944_6_);
   }

   protected AgeableModel(Function<ResourceLocation, RenderType> p_i225942_1_, boolean p_i225942_2_, float p_i225942_3_, float p_i225942_4_, float p_i225942_5_, float p_i225942_6_, float p_i225942_7_) {
      super(p_i225942_1_);
      this.isChildHeadScaled = p_i225942_2_;
      this.childHeadOffsetY = p_i225942_3_;
      this.childHeadOffsetZ = p_i225942_4_;
      this.childHeadScale = p_i225942_5_;
      this.childBodyScale = p_i225942_6_;
      this.childBodyOffsetY = p_i225942_7_;
   }

   protected AgeableModel() {
      this(false, 5.0F, 2.0F);
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      if (this.isChild) {
         matrixStackIn.push();
         if (this.isChildHeadScaled) {
            float f = 1.5F / this.childHeadScale;
            matrixStackIn.scale(f, f, f);
         }

         matrixStackIn.translate(0.0D, (double)(this.childHeadOffsetY / 16.0F), (double)(this.childHeadOffsetZ / 16.0F));
         this.getHeadParts().forEach((p_228230_8_) -> {
            p_228230_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
         matrixStackIn.pop();
         matrixStackIn.push();
         float f1 = 1.0F / this.childBodyScale;
         matrixStackIn.scale(f1, f1, f1);
         matrixStackIn.translate(0.0D, (double)(this.childBodyOffsetY / 16.0F), 0.0D);
         this.getBodyParts().forEach((p_228229_8_) -> {
            p_228229_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
         matrixStackIn.pop();
      } else {
         this.getHeadParts().forEach((p_228228_8_) -> {
            p_228228_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
         this.getBodyParts().forEach((p_228227_8_) -> {
            p_228227_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
      }

   }

   protected abstract Iterable<ModelRenderer> getHeadParts();

   protected abstract Iterable<ModelRenderer> getBodyParts();
}