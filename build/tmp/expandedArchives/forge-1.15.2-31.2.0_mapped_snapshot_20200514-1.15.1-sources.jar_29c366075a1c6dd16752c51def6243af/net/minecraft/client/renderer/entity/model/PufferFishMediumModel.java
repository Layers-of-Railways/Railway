package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishMediumModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer rightFin;
   private final ModelRenderer leftFin;
   private final ModelRenderer frontTopSpines;
   private final ModelRenderer backTopSpines;
   private final ModelRenderer frontRightSpines;
   private final ModelRenderer backRightSpines;
   private final ModelRenderer backLeftSpines;
   private final ModelRenderer frontLeftSpines;
   /** This is only one spine and not a row like the others */
   private final ModelRenderer backBottomSpine;
   private final ModelRenderer frontBottomSpines;

   public PufferFishMediumModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.body = new ModelRenderer(this, 12, 22);
      this.body.addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F);
      this.body.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.rightFin = new ModelRenderer(this, 24, 0);
      this.rightFin.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.rightFin.setRotationPoint(-2.5F, 17.0F, -1.5F);
      this.leftFin = new ModelRenderer(this, 24, 3);
      this.leftFin.addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.leftFin.setRotationPoint(2.5F, 17.0F, -1.5F);
      this.frontTopSpines = new ModelRenderer(this, 15, 16);
      this.frontTopSpines.addBox(-2.5F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F);
      this.frontTopSpines.setRotationPoint(0.0F, 17.0F, -2.5F);
      this.frontTopSpines.rotateAngleX = ((float)Math.PI / 4F);
      this.backTopSpines = new ModelRenderer(this, 10, 16);
      this.backTopSpines.addBox(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F);
      this.backTopSpines.setRotationPoint(0.0F, 17.0F, 2.5F);
      this.backTopSpines.rotateAngleX = (-(float)Math.PI / 4F);
      this.frontRightSpines = new ModelRenderer(this, 8, 16);
      this.frontRightSpines.addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.frontRightSpines.setRotationPoint(-2.5F, 22.0F, -2.5F);
      this.frontRightSpines.rotateAngleY = (-(float)Math.PI / 4F);
      this.backRightSpines = new ModelRenderer(this, 8, 16);
      this.backRightSpines.addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.backRightSpines.setRotationPoint(-2.5F, 22.0F, 2.5F);
      this.backRightSpines.rotateAngleY = ((float)Math.PI / 4F);
      this.backLeftSpines = new ModelRenderer(this, 4, 16);
      this.backLeftSpines.addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.backLeftSpines.setRotationPoint(2.5F, 22.0F, 2.5F);
      this.backLeftSpines.rotateAngleY = (-(float)Math.PI / 4F);
      this.frontLeftSpines = new ModelRenderer(this, 0, 16);
      this.frontLeftSpines.addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.frontLeftSpines.setRotationPoint(2.5F, 22.0F, -2.5F);
      this.frontLeftSpines.rotateAngleY = ((float)Math.PI / 4F);
      this.backBottomSpine = new ModelRenderer(this, 8, 22);
      this.backBottomSpine.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.backBottomSpine.setRotationPoint(0.5F, 22.0F, 2.5F);
      this.backBottomSpine.rotateAngleX = ((float)Math.PI / 4F);
      this.frontBottomSpines = new ModelRenderer(this, 17, 21);
      this.frontBottomSpines.addBox(-2.5F, 0.0F, 0.0F, 5.0F, 1.0F, 1.0F);
      this.frontBottomSpines.setRotationPoint(0.0F, 22.0F, -2.5F);
      this.frontBottomSpines.rotateAngleX = (-(float)Math.PI / 4F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body, this.rightFin, this.leftFin, this.frontTopSpines, this.backTopSpines, this.frontRightSpines, this.backRightSpines, this.backLeftSpines, this.frontLeftSpines, this.backBottomSpine, this.frontBottomSpines);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.rightFin.rotateAngleZ = -0.2F + 0.4F * MathHelper.sin(ageInTicks * 0.2F);
      this.leftFin.rotateAngleZ = 0.2F - 0.4F * MathHelper.sin(ageInTicks * 0.2F);
   }
}