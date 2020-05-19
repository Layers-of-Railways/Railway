package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrownedRenderer extends AbstractZombieRenderer<DrownedEntity, DrownedModel<DrownedEntity>> {
   private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

   public DrownedRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new DrownedModel<>(0.0F, 0.0F, 64, 64), new DrownedModel<>(0.5F, true), new DrownedModel<>(1.0F, true));
      this.addLayer(new DrownedOuterLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ZombieEntity entity) {
      return DROWNED_LOCATION;
   }

   protected void applyRotations(DrownedEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      float f = entityLiving.getSwimAnimation(partialTicks);
      if (f > 0.0F) {
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(f, entityLiving.rotationPitch, -10.0F - entityLiving.rotationPitch)));
      }

   }
}