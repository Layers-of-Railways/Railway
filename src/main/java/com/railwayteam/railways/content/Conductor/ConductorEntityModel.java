package com.railwayteam.railways.content.Conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.Railways;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ConductorEntityModel<T extends LivingEntity> extends HumanoidModel<T> implements ArmedModel, HeadedModel {
  // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Railways.MODID, "conductor"), "main");

  public ConductorEntityModel (ModelPart root) {
    super(root);
  }

  public static LayerDefinition createBodyLayer () {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition Head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 2.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

    PartDefinition Body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, 10.0F, -3.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
      .texOffs(0, 29).addBox(-3.0F, 15.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

    PartDefinition RightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, 8.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
    PartDefinition LeftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(34, 0).addBox(0.0F, 9.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, 0.0F));
    PartDefinition RightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 15).addBox(-1.1F, 7.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
    PartDefinition LeftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(34, 15).addBox(-1.9F, 7.0F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

    PartDefinition Hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0F, 0.0F));
    return LayerDefinition.create(meshdefinition, 64, 64);
  }

  @Override
  public void setupAnim (@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

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