package com.railwayteam.railways.entities.conductor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

//public class ConductorEntityModel extends AnimatedGeoModel<ConductorEntity>
//{
//	public ConductorEntityModel() {
//
//	}
//
//	@Override
//	public ResourceLocation getModelLocation(ConductorEntity object)
//	{
//		return new ResourceLocation("railways", "geo/conductor.geo.json");
//	}
//
//	@Override
//	public ResourceLocation getTextureLocation(ConductorEntity object)
//	{
//		return new ResourceLocation("railways", "textures/entity/conductor.png");
//	}
//
//	@Override
//	public ResourceLocation getAnimationFileLocation(ConductorEntity object)
//	{
//		return new ResourceLocation("railways", "animations/conductor.animation.json");
//	}
//
//	@Override
//	public void setLivingAnimations(ConductorEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
//		super.setLivingAnimations(entity, uniqueID, customPredicate);
//
//		IBone head = this.getAnimationProcessor().getBone("Head");
//
//		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
//		head.setRotationX(extraData.headPitch * ((float) Math.PI / 360F));
//		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 340F));
//	}
//}


public class ConductorEntityModel extends EntityModel<ConductorEntity> {

	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer armRight;
	private final ModelRenderer armLeft;
	private final ModelRenderer legRight;
	private final ModelRenderer legLeft;
//	private final ModelRenderer hat;
//	private final ModelRenderer hatBrim;

	public ConductorEntityModel() {
		super();

		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this); //(this);
		head.setRotationPoint(0.0F, 17.5F, 0.0F);
		head.setTextureOffset(0, 1).addCuboid(-12.0F, 13F, 4.0F, 8.0F, 7.0F, 8.0F, 0.0F, true);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.setTextureOffset(2, 16).addCuboid(-12.0F, 10.0F, 5.0F, 8.0F, 5.0F, 6.0F, 0.0F, false);
		body.setTextureOffset(5, 27).addCuboid(-11.0F, 15.0F, 6.0F, 6.0F, 4.0F, 4.0F, 0.0F, false);

		armRight = new ModelRenderer(this);
		armRight.setRotationPoint(-5.0F, 2.0F, 0.0F);
		armRight.setTextureOffset(50, 0).addCuboid(-10.0F, 8.0F, 6.0F, 3.0F, 9.0F, 4.0F, 0.0F, false);

		armLeft = new ModelRenderer(this);
		armLeft.setRotationPoint(4.0F, 1.0F, 0.0F);
		armLeft.setTextureOffset(36, 0).addCuboid(-8.0F, 9.0F, 6.0F, 3.0F, 9.0F, 4.0F, 0.0F, false);

		legRight = new ModelRenderer(this);
		legRight.setRotationPoint(-1.9F, 12.0F, 0.0F);
		legRight.setTextureOffset(50, 13).addCuboid(-9.1F, 7.0F, 6.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

		legLeft = new ModelRenderer(this);
		legLeft.setRotationPoint(1.9F, 12.0F, 0.0F);
		legLeft.setTextureOffset(36, 13).addCuboid(-9.9F, 7.0F, 6.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

		body.addChild(legLeft);
		body.addChild(legRight);
		body.addChild(armLeft);
		body.addChild(armRight);
		body.addChild(head);
		body.setRotationPoint(8.0F, 0.0F, -8.0F);
	}

	@Override
	public void setAngles(ConductorEntity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {
//		head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
//		head.rotateAngleX = (-(float)Math.PI / 4F);
		// for talrey: i tried doing what minecraft does in BipedModel, line 84.
		// theres also a head pitch thing there but it doesnt use the head pitch argument for some reason
		head.rotateAngleY = headYaw * ((float)Math.PI / 180F);
	}

	@Override
	public void setLivingAnimations(ConductorEntity entity, float limbSwing, float limbSwingAmount, float partialTick) {
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);

//		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
//		head.setRotationPoint(entity.rotationPitch * ((float) Math.PI / 360F), entity.getRotationYawHead() * ((float) Math.PI / 340F), head.rotationPointZ);
//		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 340F));
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedOverlay, int red, float green, float blue, float alpha, float whatisthislol) {
		body.render(matrixStack, buffer, packedOverlay, packedOverlay);
	}
}