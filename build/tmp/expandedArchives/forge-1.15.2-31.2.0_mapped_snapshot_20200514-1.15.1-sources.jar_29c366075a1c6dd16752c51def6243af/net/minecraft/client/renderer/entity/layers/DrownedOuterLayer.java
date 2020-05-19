package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrownedOuterLayer<T extends ZombieEntity> extends LayerRenderer<T, DrownedModel<T>> {
   private static final ResourceLocation LOCATION_OUTER_LAYER = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
   private final DrownedModel<T> modelOuterLayer = new DrownedModel<>(0.25F, 0.0F, 64, 64);

   public DrownedOuterLayer(IEntityRenderer<T, DrownedModel<T>> p_i50943_1_) {
      super(p_i50943_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      renderCopyCutoutModel(this.getEntityModel(), this.modelOuterLayer, LOCATION_OUTER_LAYER, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
   }
}