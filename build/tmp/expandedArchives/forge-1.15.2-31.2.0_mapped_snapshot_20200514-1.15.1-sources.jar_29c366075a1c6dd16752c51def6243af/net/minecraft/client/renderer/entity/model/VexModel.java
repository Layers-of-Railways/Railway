package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexModel extends BipedModel<VexEntity> {
   private final ModelRenderer leftWing;
   private final ModelRenderer rightWing;

   public VexModel() {
      super(0.0F, 0.0F, 64, 64);
      this.bipedLeftLeg.showModel = false;
      this.bipedHeadwear.showModel = false;
      this.bipedRightLeg = new ModelRenderer(this, 32, 0);
      this.bipedRightLeg.addBox(-1.0F, -1.0F, -2.0F, 6.0F, 10.0F, 4.0F, 0.0F);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.rightWing = new ModelRenderer(this, 0, 32);
      this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
      this.leftWing = new ModelRenderer(this, 0, 32);
      this.leftWing.mirror = true;
      this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
   }

   protected Iterable<ModelRenderer> getBodyParts() {
      return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(VexEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      if (entityIn.isCharging()) {
         if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
            this.bipedRightArm.rotateAngleX = 3.7699115F;
         } else {
            this.bipedLeftArm.rotateAngleX = 3.7699115F;
         }
      }

      this.bipedRightLeg.rotateAngleX += ((float)Math.PI / 5F);
      this.rightWing.rotationPointZ = 2.0F;
      this.leftWing.rotationPointZ = 2.0F;
      this.rightWing.rotationPointY = 1.0F;
      this.leftWing.rotationPointY = 1.0F;
      this.rightWing.rotateAngleY = 0.47123894F + MathHelper.cos(ageInTicks * 0.8F) * (float)Math.PI * 0.05F;
      this.leftWing.rotateAngleY = -this.rightWing.rotateAngleY;
      this.leftWing.rotateAngleZ = -0.47123894F;
      this.leftWing.rotateAngleX = 0.47123894F;
      this.rightWing.rotateAngleX = 0.47123894F;
      this.rightWing.rotateAngleZ = 0.47123894F;
   }
}