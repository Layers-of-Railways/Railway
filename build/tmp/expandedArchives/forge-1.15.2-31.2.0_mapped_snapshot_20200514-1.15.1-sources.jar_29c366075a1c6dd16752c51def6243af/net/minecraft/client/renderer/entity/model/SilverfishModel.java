package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilverfishModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] silverfishBodyParts;
   private final ModelRenderer[] silverfishWings;
   private final ImmutableList<ModelRenderer> field_228295_f_;
   private final float[] zPlacement = new float[7];
   private static final int[][] SILVERFISH_BOX_LENGTH = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] SILVERFISH_TEXTURE_POSITIONS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel() {
      this.silverfishBodyParts = new ModelRenderer[7];
      float f = -3.5F;

      for(int i = 0; i < this.silverfishBodyParts.length; ++i) {
         this.silverfishBodyParts[i] = new ModelRenderer(this, SILVERFISH_TEXTURE_POSITIONS[i][0], SILVERFISH_TEXTURE_POSITIONS[i][1]);
         this.silverfishBodyParts[i].addBox((float)SILVERFISH_BOX_LENGTH[i][0] * -0.5F, 0.0F, (float)SILVERFISH_BOX_LENGTH[i][2] * -0.5F, (float)SILVERFISH_BOX_LENGTH[i][0], (float)SILVERFISH_BOX_LENGTH[i][1], (float)SILVERFISH_BOX_LENGTH[i][2]);
         this.silverfishBodyParts[i].setRotationPoint(0.0F, (float)(24 - SILVERFISH_BOX_LENGTH[i][1]), f);
         this.zPlacement[i] = f;
         if (i < this.silverfishBodyParts.length - 1) {
            f += (float)(SILVERFISH_BOX_LENGTH[i][2] + SILVERFISH_BOX_LENGTH[i + 1][2]) * 0.5F;
         }
      }

      this.silverfishWings = new ModelRenderer[3];
      this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
      this.silverfishWings[0].addBox(-5.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[2][2] * -0.5F, 10.0F, 8.0F, (float)SILVERFISH_BOX_LENGTH[2][2]);
      this.silverfishWings[0].setRotationPoint(0.0F, 16.0F, this.zPlacement[2]);
      this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
      this.silverfishWings[1].addBox(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6.0F, 4.0F, (float)SILVERFISH_BOX_LENGTH[4][2]);
      this.silverfishWings[1].setRotationPoint(0.0F, 20.0F, this.zPlacement[4]);
      this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
      this.silverfishWings[2].addBox(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6.0F, 5.0F, (float)SILVERFISH_BOX_LENGTH[1][2]);
      this.silverfishWings[2].setRotationPoint(0.0F, 19.0F, this.zPlacement[1]);
      Builder<ModelRenderer> builder = ImmutableList.builder();
      builder.addAll(Arrays.asList(this.silverfishBodyParts));
      builder.addAll(Arrays.asList(this.silverfishWings));
      this.field_228295_f_ = builder.build();
   }

   public ImmutableList<ModelRenderer> getParts() {
      return this.field_228295_f_;
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      for(int i = 0; i < this.silverfishBodyParts.length; ++i) {
         this.silverfishBodyParts[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
         this.silverfishBodyParts[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i - 2);
      }

      this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
      this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
      this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
      this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
      this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
   }
}