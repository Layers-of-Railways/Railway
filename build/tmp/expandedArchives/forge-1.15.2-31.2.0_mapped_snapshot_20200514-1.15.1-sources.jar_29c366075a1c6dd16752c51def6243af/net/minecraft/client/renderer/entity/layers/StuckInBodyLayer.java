package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class StuckInBodyLayer<T extends LivingEntity, M extends PlayerModel<T>> extends LayerRenderer<T, M> {
   public StuckInBodyLayer(LivingRenderer<T, M> p_i226041_1_) {
      super(p_i226041_1_);
   }

   protected abstract int func_225631_a_(T p_225631_1_);

   protected abstract void func_225632_a_(MatrixStack p_225632_1_, IRenderTypeBuffer p_225632_2_, int p_225632_3_, Entity p_225632_4_, float p_225632_5_, float p_225632_6_, float p_225632_7_, float p_225632_8_);

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      int i = this.func_225631_a_(entitylivingbaseIn);
      Random random = new Random((long)entitylivingbaseIn.getEntityId());
      if (i > 0) {
         for(int j = 0; j < i; ++j) {
            matrixStackIn.push();
            ModelRenderer modelrenderer = ((PlayerModel)this.getEntityModel()).getRandomModelRenderer(random);
            ModelRenderer.ModelBox modelrenderer$modelbox = modelrenderer.getRandomCube(random);
            modelrenderer.translateRotate(matrixStackIn);
            float f = random.nextFloat();
            float f1 = random.nextFloat();
            float f2 = random.nextFloat();
            float f3 = MathHelper.lerp(f, modelrenderer$modelbox.posX1, modelrenderer$modelbox.posX2) / 16.0F;
            float f4 = MathHelper.lerp(f1, modelrenderer$modelbox.posY1, modelrenderer$modelbox.posY2) / 16.0F;
            float f5 = MathHelper.lerp(f2, modelrenderer$modelbox.posZ1, modelrenderer$modelbox.posZ2) / 16.0F;
            matrixStackIn.translate((double)f3, (double)f4, (double)f5);
            f = -1.0F * (f * 2.0F - 1.0F);
            f1 = -1.0F * (f1 * 2.0F - 1.0F);
            f2 = -1.0F * (f2 * 2.0F - 1.0F);
            this.func_225632_a_(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, f, f1, f2, partialTicks);
            matrixStackIn.pop();
         }

      }
   }
}