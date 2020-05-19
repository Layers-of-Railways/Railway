package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTropicalFishModel<E extends Entity> extends SegmentedModel<E> {
   private float field_228254_a_ = 1.0F;
   private float field_228255_b_ = 1.0F;
   private float field_228256_f_ = 1.0F;

   public void func_228257_a_(float p_228257_1_, float p_228257_2_, float p_228257_3_) {
      this.field_228254_a_ = p_228257_1_;
      this.field_228255_b_ = p_228257_2_;
      this.field_228256_f_ = p_228257_3_;
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, this.field_228254_a_ * red, this.field_228255_b_ * green, this.field_228256_f_ * blue, alpha);
   }
}