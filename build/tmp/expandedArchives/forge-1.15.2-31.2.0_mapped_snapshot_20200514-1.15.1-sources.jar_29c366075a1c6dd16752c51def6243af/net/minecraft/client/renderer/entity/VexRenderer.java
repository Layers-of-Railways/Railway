package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.VexModel;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexRenderer extends BipedRenderer<VexEntity, VexModel> {
   private static final ResourceLocation VEX_TEXTURE = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_TEXTURE = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new VexModel(), 0.3F);
   }

   protected int getBlockLight(VexEntity entityIn, float partialTicks) {
      return 15;
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(VexEntity entity) {
      return entity.isCharging() ? VEX_CHARGING_TEXTURE : VEX_TEXTURE;
   }

   protected void preRenderCallback(VexEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      matrixStackIn.scale(0.4F, 0.4F, 0.4F);
   }
}