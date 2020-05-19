package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianModel extends SegmentedModel<GuardianEntity> {
   private static final float[] field_217136_a = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
   private static final float[] field_217137_b = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
   private static final float[] field_217138_f = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
   private static final float[] field_217139_g = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
   private static final float[] field_217140_h = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
   private static final float[] field_217141_i = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
   private final ModelRenderer guardianBody;
   private final ModelRenderer guardianEye;
   private final ModelRenderer[] guardianSpines;
   private final ModelRenderer[] guardianTail;

   public GuardianModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.guardianSpines = new ModelRenderer[12];
      this.guardianBody = new ModelRenderer(this);
      this.guardianBody.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F);
      this.guardianBody.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F);
      this.guardianBody.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F);

      for(int i = 0; i < this.guardianSpines.length; ++i) {
         this.guardianSpines[i] = new ModelRenderer(this, 0, 0);
         this.guardianSpines[i].addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);
         this.guardianBody.addChild(this.guardianSpines[i]);
      }

      this.guardianEye = new ModelRenderer(this, 8, 0);
      this.guardianEye.addBox(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F);
      this.guardianBody.addChild(this.guardianEye);
      this.guardianTail = new ModelRenderer[3];
      this.guardianTail[0] = new ModelRenderer(this, 40, 0);
      this.guardianTail[0].addBox(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F);
      this.guardianTail[1] = new ModelRenderer(this, 0, 54);
      this.guardianTail[1].addBox(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F);
      this.guardianTail[2] = new ModelRenderer(this);
      this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F);
      this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F);
      this.guardianBody.addChild(this.guardianTail[0]);
      this.guardianTail[0].addChild(this.guardianTail[1]);
      this.guardianTail[1].addChild(this.guardianTail[2]);
      this.func_228261_a_(0.0F, 0.0F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.guardianBody);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(GuardianEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      float f = ageInTicks - (float)entityIn.ticksExisted;
      this.guardianBody.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.guardianBody.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      float f1 = (1.0F - entityIn.getSpikesAnimation(f)) * 0.55F;
      this.func_228261_a_(ageInTicks, f1);
      this.guardianEye.rotationPointZ = -8.25F;
      Entity entity = Minecraft.getInstance().getRenderViewEntity();
      if (entityIn.hasTargetedEntity()) {
         entity = entityIn.getTargetedEntity();
      }

      if (entity != null) {
         Vec3d vec3d = entity.getEyePosition(0.0F);
         Vec3d vec3d1 = entityIn.getEyePosition(0.0F);
         double d0 = vec3d.y - vec3d1.y;
         if (d0 > 0.0D) {
            this.guardianEye.rotationPointY = 0.0F;
         } else {
            this.guardianEye.rotationPointY = 1.0F;
         }

         Vec3d vec3d2 = entityIn.getLook(0.0F);
         vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
         Vec3d vec3d3 = (new Vec3d(vec3d1.x - vec3d.x, 0.0D, vec3d1.z - vec3d.z)).normalize().rotateYaw(((float)Math.PI / 2F));
         double d1 = vec3d2.dotProduct(vec3d3);
         this.guardianEye.rotationPointX = MathHelper.sqrt((float)Math.abs(d1)) * 2.0F * (float)Math.signum(d1);
      }

      this.guardianEye.showModel = true;
      float f2 = entityIn.getTailAnimation(f);
      this.guardianTail[0].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.05F;
      this.guardianTail[1].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.1F;
      this.guardianTail[1].rotationPointX = -1.5F;
      this.guardianTail[1].rotationPointY = 0.5F;
      this.guardianTail[1].rotationPointZ = 14.0F;
      this.guardianTail[2].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.15F;
      this.guardianTail[2].rotationPointX = 0.5F;
      this.guardianTail[2].rotationPointY = 0.5F;
      this.guardianTail[2].rotationPointZ = 6.0F;
   }

   private void func_228261_a_(float p_228261_1_, float p_228261_2_) {
      for(int i = 0; i < 12; ++i) {
         this.guardianSpines[i].rotateAngleX = (float)Math.PI * field_217136_a[i];
         this.guardianSpines[i].rotateAngleY = (float)Math.PI * field_217137_b[i];
         this.guardianSpines[i].rotateAngleZ = (float)Math.PI * field_217138_f[i];
         this.guardianSpines[i].rotationPointX = field_217139_g[i] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)i) * 0.01F - p_228261_2_);
         this.guardianSpines[i].rotationPointY = 16.0F + field_217140_h[i] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)i) * 0.01F - p_228261_2_);
         this.guardianSpines[i].rotationPointZ = field_217141_i[i] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)i) * 0.01F - p_228261_2_);
      }

   }
}