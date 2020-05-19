package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.CatModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatCollarLayer extends LayerRenderer<CatEntity, CatModel<CatEntity>> {
   private static final ResourceLocation CAT_COLLAR = new ResourceLocation("textures/entity/cat/cat_collar.png");
   private final CatModel<CatEntity> model = new CatModel<>(0.01F);

   public CatCollarLayer(IEntityRenderer<CatEntity, CatModel<CatEntity>> p_i50948_1_) {
      super(p_i50948_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CatEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (entitylivingbaseIn.isTamed()) {
         float[] afloat = entitylivingbaseIn.getCollarColor().getColorComponentValues();
         renderCopyCutoutModel(this.getEntityModel(), this.model, CAT_COLLAR, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, afloat[0], afloat[1], afloat[2]);
      }
   }
}