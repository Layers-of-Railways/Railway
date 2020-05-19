package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.LlamaModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaDecorLayer extends LayerRenderer<LlamaEntity, LlamaModel<LlamaEntity>> {
   private static final ResourceLocation[] LLAMA_DECOR_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/decor/white.png"), new ResourceLocation("textures/entity/llama/decor/orange.png"), new ResourceLocation("textures/entity/llama/decor/magenta.png"), new ResourceLocation("textures/entity/llama/decor/light_blue.png"), new ResourceLocation("textures/entity/llama/decor/yellow.png"), new ResourceLocation("textures/entity/llama/decor/lime.png"), new ResourceLocation("textures/entity/llama/decor/pink.png"), new ResourceLocation("textures/entity/llama/decor/gray.png"), new ResourceLocation("textures/entity/llama/decor/light_gray.png"), new ResourceLocation("textures/entity/llama/decor/cyan.png"), new ResourceLocation("textures/entity/llama/decor/purple.png"), new ResourceLocation("textures/entity/llama/decor/blue.png"), new ResourceLocation("textures/entity/llama/decor/brown.png"), new ResourceLocation("textures/entity/llama/decor/green.png"), new ResourceLocation("textures/entity/llama/decor/red.png"), new ResourceLocation("textures/entity/llama/decor/black.png")};
   private static final ResourceLocation TRADER_LLAMA = new ResourceLocation("textures/entity/llama/decor/trader_llama.png");
   private final LlamaModel<LlamaEntity> model = new LlamaModel<>(0.5F);

   public LlamaDecorLayer(IEntityRenderer<LlamaEntity, LlamaModel<LlamaEntity>> p_i50933_1_) {
      super(p_i50933_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, LlamaEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      DyeColor dyecolor = entitylivingbaseIn.getColor();
      ResourceLocation resourcelocation;
      if (dyecolor != null) {
         resourcelocation = LLAMA_DECOR_TEXTURES[dyecolor.getId()];
      } else {
         if (!entitylivingbaseIn.isTraderLlama()) {
            return;
         }

         resourcelocation = TRADER_LLAMA;
      }

      this.getEntityModel().copyModelAttributesTo(this.model);
      this.model.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(resourcelocation));
      this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
   }
}