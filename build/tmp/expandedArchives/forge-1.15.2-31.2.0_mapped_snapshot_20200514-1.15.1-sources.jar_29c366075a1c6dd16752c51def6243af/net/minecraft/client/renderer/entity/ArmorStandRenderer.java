package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.model.ArmorStandModel;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandRenderer extends LivingRenderer<ArmorStandEntity, ArmorStandArmorModel> {
   /** A constant instance of the armor stand texture, wrapped inside a ResourceLocation wrapper. */
   public static final ResourceLocation TEXTURE_ARMOR_STAND = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(EntityRendererManager manager) {
      super(manager, new ArmorStandModel(), 0.0F);
      this.addLayer(new BipedArmorLayer<>(this, new ArmorStandArmorModel(0.5F), new ArmorStandArmorModel(1.0F)));
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new HeadLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ArmorStandEntity entity) {
      return TEXTURE_ARMOR_STAND;
   }

   protected void applyRotations(ArmorStandEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
      float f = (float)(entityLiving.world.getGameTime() - entityLiving.punchCooldown) + partialTicks;
      if (f < 5.0F) {
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.sin(f / 1.5F * (float)Math.PI) * 3.0F));
      }

   }

   protected boolean canRenderName(ArmorStandEntity entity) {
      double d0 = this.renderManager.squareDistanceTo(entity);
      float f = entity.isCrouching() ? 32.0F : 64.0F;
      return d0 >= (double)(f * f) ? false : entity.isCustomNameVisible();
   }

   @Nullable
   protected RenderType func_230042_a_(ArmorStandEntity p_230042_1_, boolean p_230042_2_, boolean p_230042_3_) {
      if (!p_230042_1_.hasMarker()) {
         return super.func_230042_a_(p_230042_1_, p_230042_2_, p_230042_3_);
      } else {
         ResourceLocation resourcelocation = this.getEntityTexture(p_230042_1_);
         if (p_230042_3_) {
            return RenderType.func_230168_b_(resourcelocation, false);
         } else {
            return p_230042_2_ ? RenderType.func_230167_a_(resourcelocation, false) : null;
         }
      }
   }
}