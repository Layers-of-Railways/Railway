package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BookModel extends Model {
   private final ModelRenderer coverRight = (new ModelRenderer(64, 32, 0, 0)).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
   private final ModelRenderer coverLeft = (new ModelRenderer(64, 32, 16, 0)).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
   private final ModelRenderer pagesRight;
   private final ModelRenderer pagesLeft;
   private final ModelRenderer flippingPageRight;
   private final ModelRenderer flippingPageLeft;
   private final ModelRenderer bookSpine = (new ModelRenderer(64, 32, 12, 0)).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F);
   private final List<ModelRenderer> field_228246_h_;

   public BookModel() {
      super(RenderType::getEntitySolid);
      this.pagesRight = (new ModelRenderer(64, 32, 0, 10)).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F);
      this.pagesLeft = (new ModelRenderer(64, 32, 12, 10)).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F);
      this.flippingPageRight = (new ModelRenderer(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
      this.flippingPageLeft = (new ModelRenderer(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
      this.field_228246_h_ = ImmutableList.of(this.coverRight, this.coverLeft, this.bookSpine, this.pagesRight, this.pagesLeft, this.flippingPageRight, this.flippingPageLeft);
      this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
      this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.bookSpine.rotateAngleY = ((float)Math.PI / 2F);
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      this.func_228249_b_(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
   }

   public void func_228249_b_(MatrixStack p_228249_1_, IVertexBuilder p_228249_2_, int p_228249_3_, int p_228249_4_, float p_228249_5_, float p_228249_6_, float p_228249_7_, float p_228249_8_) {
      this.field_228246_h_.forEach((p_228248_8_) -> {
         p_228248_8_.render(p_228249_1_, p_228249_2_, p_228249_3_, p_228249_4_, p_228249_5_, p_228249_6_, p_228249_7_, p_228249_8_);
      });
   }

   public void func_228247_a_(float p_228247_1_, float p_228247_2_, float p_228247_3_, float p_228247_4_) {
      float f = (MathHelper.sin(p_228247_1_ * 0.02F) * 0.1F + 1.25F) * p_228247_4_;
      this.coverRight.rotateAngleY = (float)Math.PI + f;
      this.coverLeft.rotateAngleY = -f;
      this.pagesRight.rotateAngleY = f;
      this.pagesLeft.rotateAngleY = -f;
      this.flippingPageRight.rotateAngleY = f - f * 2.0F * p_228247_2_;
      this.flippingPageLeft.rotateAngleY = f - f * 2.0F * p_228247_3_;
      this.pagesRight.rotationPointX = MathHelper.sin(f);
      this.pagesLeft.rotationPointX = MathHelper.sin(f);
      this.flippingPageRight.rotationPointX = MathHelper.sin(f);
      this.flippingPageLeft.rotationPointX = MathHelper.sin(f);
   }
}