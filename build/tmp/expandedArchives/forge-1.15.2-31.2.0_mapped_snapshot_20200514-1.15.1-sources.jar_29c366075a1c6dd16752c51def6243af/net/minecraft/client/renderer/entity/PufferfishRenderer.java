package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PufferFishBigModel;
import net.minecraft.client.renderer.entity.model.PufferFishMediumModel;
import net.minecraft.client.renderer.entity.model.PufferFishSmallModel;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferfishRenderer extends MobRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
   private static final ResourceLocation PUFFERFISH_TEXTURES = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int lastPuffState;
   private final PufferFishSmallModel<PufferfishEntity> modelSmall = new PufferFishSmallModel<>();
   private final PufferFishMediumModel<PufferfishEntity> modelMedium = new PufferFishMediumModel<>();
   private final PufferFishBigModel<PufferfishEntity> modelLarge = new PufferFishBigModel<>();

   public PufferfishRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new PufferFishBigModel<>(), 0.2F);
      this.lastPuffState = 3;
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(PufferfishEntity entity) {
      return PUFFERFISH_TEXTURES;
   }

   public void render(PufferfishEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      int i = entityIn.getPuffState();
      if (i != this.lastPuffState) {
         if (i == 0) {
            this.entityModel = this.modelSmall;
         } else if (i == 1) {
            this.entityModel = this.modelMedium;
         } else {
            this.entityModel = this.modelLarge;
         }
      }

      this.lastPuffState = i;
      this.shadowSize = 0.1F + 0.1F * (float)i;
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   protected void applyRotations(PufferfishEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      matrixStackIn.translate(0.0D, (double)(MathHelper.cos(ageInTicks * 0.05F) * 0.08F), 0.0D);
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
   }
}