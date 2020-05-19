package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShieldModel extends Model {
   private final ModelRenderer plate;
   private final ModelRenderer handle;

   public ShieldModel() {
      super(RenderType::getEntitySolid);
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.plate = new ModelRenderer(this, 0, 0);
      this.plate.addBox(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F, 0.0F);
      this.handle = new ModelRenderer(this, 26, 0);
      this.handle.addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, 0.0F);
   }

   public ModelRenderer func_228293_a_() {
      return this.plate;
   }

   public ModelRenderer func_228294_b_() {
      return this.handle;
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      this.plate.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      this.handle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
   }
}