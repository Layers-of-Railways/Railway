package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChickenModel<T extends Entity> extends AgeableModel<T> {
   private final ModelRenderer head;
   private final ModelRenderer body;
   private final ModelRenderer rightLeg;
   private final ModelRenderer leftLeg;
   private final ModelRenderer rightWing;
   private final ModelRenderer leftWing;
   private final ModelRenderer bill;
   private final ModelRenderer chin;

   public ChickenModel() {
      int i = 16;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F, 0.0F);
      this.head.setRotationPoint(0.0F, 15.0F, -4.0F);
      this.bill = new ModelRenderer(this, 14, 0);
      this.bill.addBox(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.0F);
      this.bill.setRotationPoint(0.0F, 15.0F, -4.0F);
      this.chin = new ModelRenderer(this, 14, 4);
      this.chin.addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F);
      this.chin.setRotationPoint(0.0F, 15.0F, -4.0F);
      this.body = new ModelRenderer(this, 0, 9);
      this.body.addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F);
      this.body.setRotationPoint(0.0F, 16.0F, 0.0F);
      this.rightLeg = new ModelRenderer(this, 26, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
      this.rightLeg.setRotationPoint(-2.0F, 19.0F, 1.0F);
      this.leftLeg = new ModelRenderer(this, 26, 0);
      this.leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
      this.leftLeg.setRotationPoint(1.0F, 19.0F, 1.0F);
      this.rightWing = new ModelRenderer(this, 24, 13);
      this.rightWing.addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
      this.rightWing.setRotationPoint(-4.0F, 13.0F, 0.0F);
      this.leftWing = new ModelRenderer(this, 24, 13);
      this.leftWing.addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
      this.leftWing.setRotationPoint(4.0F, 13.0F, 0.0F);
   }

   protected Iterable<ModelRenderer> getHeadParts() {
      return ImmutableList.of(this.head, this.bill, this.chin);
   }

   protected Iterable<ModelRenderer> getBodyParts() {
      return ImmutableList.of(this.body, this.rightLeg, this.leftLeg, this.rightWing, this.leftWing);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.bill.rotateAngleX = this.head.rotateAngleX;
      this.bill.rotateAngleY = this.head.rotateAngleY;
      this.chin.rotateAngleX = this.head.rotateAngleX;
      this.chin.rotateAngleY = this.head.rotateAngleY;
      this.body.rotateAngleX = ((float)Math.PI / 2F);
      this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.rightWing.rotateAngleZ = ageInTicks;
      this.leftWing.rotateAngleZ = -ageInTicks;
   }
}