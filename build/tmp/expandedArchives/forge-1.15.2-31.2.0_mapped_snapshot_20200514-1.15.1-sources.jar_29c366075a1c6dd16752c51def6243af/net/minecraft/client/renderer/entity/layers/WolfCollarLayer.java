package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfCollarLayer extends LayerRenderer<WolfEntity, WolfModel<WolfEntity>> {
   private static final ResourceLocation WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(IEntityRenderer<WolfEntity, WolfModel<WolfEntity>> rendererIn) {
      super(rendererIn);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, WolfEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
         float[] afloat = entitylivingbaseIn.getCollarColor().getColorComponentValues();
         renderCutoutModel(this.getEntityModel(), WOLF_COLLAR, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, afloat[0], afloat[1], afloat[2]);
      }
   }
}