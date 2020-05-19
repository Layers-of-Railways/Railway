package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TridentModel extends Model {
   public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/trident.png");
   private final ModelRenderer modelRenderer = new ModelRenderer(32, 32, 0, 6);

   public TridentModel() {
      super(RenderType::getEntitySolid);
      this.modelRenderer.addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F, 0.0F);
      ModelRenderer modelrenderer = new ModelRenderer(32, 32, 4, 0);
      modelrenderer.addBox(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F);
      this.modelRenderer.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(32, 32, 4, 3);
      modelrenderer1.addBox(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F);
      this.modelRenderer.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = new ModelRenderer(32, 32, 0, 0);
      modelrenderer2.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F);
      this.modelRenderer.addChild(modelrenderer2);
      ModelRenderer modelrenderer3 = new ModelRenderer(32, 32, 4, 3);
      modelrenderer3.mirror = true;
      modelrenderer3.addBox(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F);
      this.modelRenderer.addChild(modelrenderer3);
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      this.modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
   }
}