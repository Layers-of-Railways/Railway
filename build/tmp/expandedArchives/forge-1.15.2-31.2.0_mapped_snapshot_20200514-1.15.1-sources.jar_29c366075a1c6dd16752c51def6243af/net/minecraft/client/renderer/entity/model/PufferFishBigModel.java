package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishBigModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer rightFin;
   private final ModelRenderer leftFin;
   private final ModelRenderer frontTopSpines;
   private final ModelRenderer topMidSpines;
   private final ModelRenderer backTopSpines;
   private final ModelRenderer frontRightSpines;
   private final ModelRenderer frontLeftSpines;
   private final ModelRenderer frontBottomSpines;
   private final ModelRenderer bottomBackSpines;
   private final ModelRenderer bottomMidSpines;
   private final ModelRenderer backRightSpines;
   private final ModelRenderer backLeftSpines;

   public PufferFishBigModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.body.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.rightFin = new ModelRenderer(this, 24, 0);
      this.rightFin.addBox(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F);
      this.rightFin.setRotationPoint(-4.0F, 15.0F, -2.0F);
      this.leftFin = new ModelRenderer(this, 24, 3);
      this.leftFin.addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F);
      this.leftFin.setRotationPoint(4.0F, 15.0F, -2.0F);
      this.frontTopSpines = new ModelRenderer(this, 15, 17);
      this.frontTopSpines.addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.frontTopSpines.setRotationPoint(0.0F, 14.0F, -4.0F);
      this.frontTopSpines.rotateAngleX = ((float)Math.PI / 4F);
      this.topMidSpines = new ModelRenderer(this, 14, 16);
      this.topMidSpines.addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F);
      this.topMidSpines.setRotationPoint(0.0F, 14.0F, 0.0F);
      this.backTopSpines = new ModelRenderer(this, 23, 18);
      this.backTopSpines.addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.backTopSpines.setRotationPoint(0.0F, 14.0F, 4.0F);
      this.backTopSpines.rotateAngleX = (-(float)Math.PI / 4F);
      this.frontRightSpines = new ModelRenderer(this, 5, 17);
      this.frontRightSpines.addBox(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.frontRightSpines.setRotationPoint(-4.0F, 22.0F, -4.0F);
      this.frontRightSpines.rotateAngleY = (-(float)Math.PI / 4F);
      this.frontLeftSpines = new ModelRenderer(this, 1, 17);
      this.frontLeftSpines.addBox(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.frontLeftSpines.setRotationPoint(4.0F, 22.0F, -4.0F);
      this.frontLeftSpines.rotateAngleY = ((float)Math.PI / 4F);
      this.frontBottomSpines = new ModelRenderer(this, 15, 20);
      this.frontBottomSpines.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.frontBottomSpines.setRotationPoint(0.0F, 22.0F, -4.0F);
      this.frontBottomSpines.rotateAngleX = (-(float)Math.PI / 4F);
      this.bottomMidSpines = new ModelRenderer(this, 15, 20);
      this.bottomMidSpines.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.bottomMidSpines.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.bottomBackSpines = new ModelRenderer(this, 15, 20);
      this.bottomBackSpines.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.bottomBackSpines.setRotationPoint(0.0F, 22.0F, 4.0F);
      this.bottomBackSpines.rotateAngleX = ((float)Math.PI / 4F);
      this.backRightSpines = new ModelRenderer(this, 9, 17);
      this.backRightSpines.addBox(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.backRightSpines.setRotationPoint(-4.0F, 22.0F, 4.0F);
      this.backRightSpines.rotateAngleY = ((float)Math.PI / 4F);
      this.backLeftSpines = new ModelRenderer(this, 9, 17);
      this.backLeftSpines.addBox(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.backLeftSpines.setRotationPoint(4.0F, 22.0F, 4.0F);
      this.backLeftSpines.rotateAngleY = (-(float)Math.PI / 4F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body, this.rightFin, this.leftFin, this.frontTopSpines, this.topMidSpines, this.backTopSpines, this.frontRightSpines, this.backLeftSpines, this.frontBottomSpines, this.bottomMidSpines, this.bottomBackSpines, this.backRightSpines, this.backLeftSpines);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.rightFin.rotateAngleZ = -0.2F + 0.4F * MathHelper.sin(ageInTicks * 0.2F);
      this.leftFin.rotateAngleZ = 0.2F - 0.4F * MathHelper.sin(ageInTicks * 0.2F);
   }
}