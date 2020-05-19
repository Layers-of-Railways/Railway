package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaveSpiderRenderer extends SpiderRenderer<CaveSpiderEntity> {
   private static final ResourceLocation CAVE_SPIDER_TEXTURES = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public CaveSpiderRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize *= 0.7F;
   }

   protected void preRenderCallback(CaveSpiderEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(0.7F, 0.7F, 0.7F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(CaveSpiderEntity entity) {
      return CAVE_SPIDER_TEXTURES;
   }
}