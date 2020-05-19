package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EvokerRenderer<T extends SpellcastingIllagerEntity> extends IllagerRenderer<T> {
   private static final ResourceLocation EVOKER_ILLAGER = new ResourceLocation("textures/entity/illager/evoker.png");

   public EvokerRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<T, IllagerModel<T>>(this) {
         public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isSpellcasting()) {
               super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }

         }
      });
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(T entity) {
      return EVOKER_ILLAGER;
   }
}