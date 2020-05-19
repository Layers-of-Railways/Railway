package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StayClothingLayer<T extends MobEntity & IRangedAttackMob, M extends EntityModel<T>> extends LayerRenderer<T, M> {
   private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
   private final SkeletonModel<T> layerModel = new SkeletonModel<>(0.25F, true);

   public StayClothingLayer(IEntityRenderer<T, M> p_i50919_1_) {
      super(p_i50919_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      renderCopyCutoutModel(this.getEntityModel(), this.layerModel, STRAY_CLOTHES_TEXTURES, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
   }
}