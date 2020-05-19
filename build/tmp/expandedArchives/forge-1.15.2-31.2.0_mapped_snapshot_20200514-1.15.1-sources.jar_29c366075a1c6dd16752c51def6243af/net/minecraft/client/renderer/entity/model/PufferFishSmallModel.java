package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishSmallModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer rightEye;
   private final ModelRenderer leftEye;
   private final ModelRenderer rightFin;
   private final ModelRenderer leftFin;
   private final ModelRenderer tail;

   public PufferFishSmallModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 23;
      this.body = new ModelRenderer(this, 0, 27);
      this.body.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F);
      this.body.setRotationPoint(0.0F, 23.0F, 0.0F);
      this.rightEye = new ModelRenderer(this, 24, 6);
      this.rightEye.addBox(-1.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
      this.rightEye.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.leftEye = new ModelRenderer(this, 28, 6);
      this.leftEye.addBox(0.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
      this.leftEye.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.tail = new ModelRenderer(this, -3, 0);
      this.tail.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F);
      this.tail.setRotationPoint(0.0F, 22.0F, 1.5F);
      this.rightFin = new ModelRenderer(this, 25, 0);
      this.rightFin.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
      this.rightFin.setRotationPoint(-1.5F, 22.0F, -1.5F);
      this.leftFin = new ModelRenderer(this, 25, 0);
      this.leftFin.addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
      this.leftFin.setRotationPoint(1.5F, 22.0F, -1.5F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body, this.rightEye, this.leftEye, this.tail, this.rightFin, this.leftFin);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.rightFin.rotateAngleZ = -0.2F + 0.4F * MathHelper.sin(ageInTicks * 0.2F);
      this.leftFin.rotateAngleZ = 0.2F - 0.4F * MathHelper.sin(ageInTicks * 0.2F);
   }
}