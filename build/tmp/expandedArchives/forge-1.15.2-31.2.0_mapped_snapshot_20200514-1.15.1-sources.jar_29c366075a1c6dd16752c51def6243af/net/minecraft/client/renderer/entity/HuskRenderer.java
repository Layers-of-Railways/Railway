package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HuskRenderer extends ZombieRenderer {
   private static final ResourceLocation HUSK_ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/husk.png");

   public HuskRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void preRenderCallback(ZombieEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      float f = 1.0625F;
      matrixStackIn.scale(1.0625F, 1.0625F, 1.0625F);
      super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ZombieEntity entity) {
      return HUSK_ZOMBIE_TEXTURES;
   }
}