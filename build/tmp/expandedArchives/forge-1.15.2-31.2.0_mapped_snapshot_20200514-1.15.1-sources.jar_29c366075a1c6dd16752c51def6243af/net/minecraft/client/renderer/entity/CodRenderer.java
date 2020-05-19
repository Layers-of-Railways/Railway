package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.CodModel;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CodRenderer extends MobRenderer<CodEntity, CodModel<CodEntity>> {
   private static final ResourceLocation COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");

   public CodRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new CodModel<>(), 0.3F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(CodEntity entity) {
      return COD_LOCATION;
   }

   protected void applyRotations(CodEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      float f = 4.3F * MathHelper.sin(0.6F * ageInTicks);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
      if (!entityLiving.isInWater()) {
         matrixStackIn.translate((double)0.1F, (double)0.1F, (double)-0.1F);
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
      }

   }
}