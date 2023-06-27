package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.Railways;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class ConductorEntityModel<T extends ConductorEntity> extends HumanoidModel<T> implements ArmedModel, HeadedModel {
  // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Railways.MODID, "conductor"), "main");

  public ConductorEntityModel (ModelPart root) {
    super(root);
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

    PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

    PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(34, 0).addBox(0.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 12.0F, 0.0F));

    PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(50, 0).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 12.0F, 0.0F));

    PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -9.0F, -3.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(0, 29).addBox(-3.0F, -4.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));

    PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(34, 15).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 19.0F, 0.0F));

    PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 15).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 19.0F, 0.0F));

    return LayerDefinition.create(meshdefinition, 64, 64);
  }

  @Override
  public void setupAnim (@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    if (entity.visualBaseModel != null && false) {
      setupAnimBasedOn(entity.visualBaseModel, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
    // mostly based on HumanoidModel::setupAnim
    // TODO can't call super directly due to rotation anchor offsets, find a way to fix them?
    this.head.xRot = headPitch * ((float)Math.PI / 180F);
    this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

    this.hat.xRot = (float) (this.head.xRot + (-10 * (Math.PI / 180)));
    this.hat.yRot = this.head.yRot;
    float amt = -0.1f*16;
    this.hat.x = (float) (Math.cos(this.head.xRot) * amt * Math.sin(this.head.yRot));
    this.hat.z = (float) (Math.cos(this.head.xRot) * amt * Math.cos(this.head.yRot));
    this.hat.y = 10.0f - (float) (Math.sin(this.head.xRot) * amt);

    this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F * 2 + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
    this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F * 2) * 2.0F * limbSwingAmount * 0.5F;

    this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F * 3) * 1.4F * limbSwingAmount;
    this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F * 3 + (float)Math.PI) * 1.4F * limbSwingAmount;
    this.rightLeg.yRot = 0.0F;
    this.leftLeg.yRot = 0.0F;
    this.rightLeg.zRot = 0.0F;
    this.leftLeg.zRot = 0.0F;
    if (this.riding) {
      this.rightArm.xRot += (-(float)Math.PI / 2f);
      this.leftArm.xRot += (-(float)Math.PI / 2f);
      this.rightLeg.xRot = -1.4137167F;
      this.rightLeg.yRot = (-(float)Math.PI / 20f);
      this.rightLeg.zRot = 0.07853982F;
      this.leftLeg.xRot = -1.4137167F;
      this.leftLeg.yRot = ((float)Math.PI / 20f);
      this.leftLeg.zRot = -0.07853982F;
    }
  }

  private void setupAnimBasedOn(PlayerModel<?> visualBaseModel, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    this.head.xRot = visualBaseModel.head.xRot;
    this.head.yRot = visualBaseModel.head.yRot;

    this.hat.xRot = (float) (this.head.xRot + (-10 * (Math.PI / 180)));
    this.hat.yRot = this.head.yRot;
    float amt = -0.1f*16;
    this.hat.x = (float) (Math.cos(this.head.xRot) * amt * Math.sin(this.head.yRot));
    this.hat.z = (float) (Math.cos(this.head.xRot) * amt * Math.cos(this.head.yRot));
    this.hat.y = 10.0f - (float) (Math.sin(this.head.xRot) * amt);

    this.rightArm.xRot = visualBaseModel.rightArm.xRot;
    this.leftArm.xRot = visualBaseModel.leftArm.xRot;

    this.rightLeg.xRot = visualBaseModel.rightLeg.xRot;
    this.leftLeg.xRot = visualBaseModel.leftLeg.xRot;
    this.rightLeg.yRot = 0.0F;
    this.leftLeg.yRot = 0.0F;
    this.rightLeg.zRot = 0.0F;
    this.leftLeg.zRot = 0.0F;
    if (visualBaseModel.riding) {
      this.rightArm.xRot += (-(float)Math.PI / 2f);
      this.leftArm.xRot += (-(float)Math.PI / 2f);
      this.rightLeg.xRot = -1.4137167F;
      this.rightLeg.yRot = (-(float)Math.PI / 20f);
      this.rightLeg.zRot = 0.07853982F;
      this.leftLeg.xRot = -1.4137167F;
      this.leftLeg.yRot = ((float)Math.PI / 20f);
      this.leftLeg.zRot = -0.07853982F;
    }
  }

  @Override
  public void translateToHand (@NotNull HumanoidArm arm, @NotNull PoseStack stack) {
    (arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm).translateAndRotate(stack);
  }

  @Override
  public @NotNull ModelPart getHead () {
    return this.head;
  }

  @Override
  public void renderToBuffer (@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    //hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}