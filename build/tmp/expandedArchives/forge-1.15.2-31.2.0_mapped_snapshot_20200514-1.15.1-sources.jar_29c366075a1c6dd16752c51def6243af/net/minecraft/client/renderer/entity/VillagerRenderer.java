package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerRenderer extends MobRenderer<VillagerEntity, VillagerModel<VillagerEntity>> {
   private static final ResourceLocation field_217779_a = new ResourceLocation("textures/entity/villager/villager.png");

   public VillagerRenderer(EntityRendererManager renderManagerIn, IReloadableResourceManager resourceManagerIn) {
      super(renderManagerIn, new VillagerModel<>(0.0F), 0.5F);
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new VillagerLevelPendantLayer<>(this, resourceManagerIn, "villager"));
      this.addLayer(new CrossedArmsItemLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(VillagerEntity entity) {
      return field_217779_a;
   }

   protected void preRenderCallback(VillagerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      float f = 0.9375F;
      if (entitylivingbaseIn.isChild()) {
         f = (float)((double)f * 0.5D);
         this.shadowSize = 0.25F;
      } else {
         this.shadowSize = 0.5F;
      }

      matrixStackIn.scale(f, f, f);
   }
}