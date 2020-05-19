package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WanderingTraderRenderer extends MobRenderer<WanderingTraderEntity, VillagerModel<WanderingTraderEntity>> {
   private static final ResourceLocation field_217780_a = new ResourceLocation("textures/entity/wandering_trader.png");

   public WanderingTraderRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new VillagerModel<>(0.0F), 0.5F);
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new CrossedArmsItemLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(WanderingTraderEntity entity) {
      return field_217780_a;
   }

   protected void preRenderCallback(WanderingTraderEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      float f = 0.9375F;
      matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
   }
}