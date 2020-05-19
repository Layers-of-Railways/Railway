package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.EndermanEyesLayer;
import net.minecraft.client.renderer.entity.layers.HeldBlockLayer;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanRenderer extends MobRenderer<EndermanEntity, EndermanModel<EndermanEntity>> {
   private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random rnd = new Random();

   public EndermanRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new EndermanModel<>(0.0F), 0.5F);
      this.addLayer(new EndermanEyesLayer<>(this));
      this.addLayer(new HeldBlockLayer(this));
   }

   public void render(EndermanEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      BlockState blockstate = entityIn.getHeldBlockState();
      EndermanModel<EndermanEntity> endermanmodel = this.getEntityModel();
      endermanmodel.isCarrying = blockstate != null;
      endermanmodel.isAttacking = entityIn.isScreaming();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   public Vec3d getRenderOffset(EndermanEntity entityIn, float partialTicks) {
      if (entityIn.isScreaming()) {
         double d0 = 0.02D;
         return new Vec3d(this.rnd.nextGaussian() * 0.02D, 0.0D, this.rnd.nextGaussian() * 0.02D);
      } else {
         return super.getRenderOffset(entityIn, partialTicks);
      }
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(EndermanEntity entity) {
      return ENDERMAN_TEXTURES;
   }
}