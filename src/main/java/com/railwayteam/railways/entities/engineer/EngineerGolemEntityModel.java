package com.railwayteam.railways.entities.engineer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class EngineerGolemEntityModel extends EntityModel<EngineerGolemEntity> {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer armRight;
	private final ModelRenderer armLeft;
	private final ModelRenderer legRight;
	private final ModelRenderer legLeft;
	private final ModelRenderer hat;
	private final ModelRenderer hatBrim;

	public EngineerGolemEntityModel() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this); //(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.setTextureOffset(0, 1).addCuboid(-12.0F, 3.0F, 4.0F, 8.0F, 7.0F, 8.0F, 0.0F, true);

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

		hat = new ModelRenderer(this);
		hat.setRotationPoint(-8.0F, 3.5777F, 7.0058F);
		setRotationAngle(hat, -0.1309F, 0.0F, 0.0F);
		hat.setTextureOffset(39, 33).addCuboid(-4.0F, -1.5777F, 4.9942F, 8.0F, 3.0F, 1.0F, 0.0F, false);
		hat.setTextureOffset(34, 48).addCuboid(-5.0F, -1.5777F, -4.0058F, 1.0F, 3.0F, 10.0F, 0.0F, false);
		hat.setTextureOffset(34, 36).addCuboid(4.0F, -1.5777F, -4.0058F, 1.0F, 3.0F, 10.0F, 0.0F, false);
		hat.setTextureOffset(39, 30).addCuboid(-4.0F, -1.5777F, -4.0058F, 8.0F, 3.0F, 1.0F, 0.0F, false);
		hat.setTextureOffset(32, 22).addCuboid(-4.0F, -1.5777F, -3.0058F, 8.0F, 1.0F, 8.0F, 0.0F, false);

		hatBrim = new ModelRenderer(this);
		hatBrim.setRotationPoint(8.0F, 0.9861F, -4.9098F);
		setRotationAngle(hatBrim, 0.3927F, 0.0F, 0.0F);
		hatBrim.setTextureOffset(12, 46).addCuboid(-13.0F, -0.0978F, -1.5612F, 10.0F, 1.0F, 3.0F, 0.0F, false);

		hat.addChild(hatBrim);
		head.addChild(hat);
		body.addChild(legLeft);
		body.addChild(legRight);
		body.addChild(armLeft);
		body.addChild(armRight);
		head.addChild(body);
		head.setRotationPoint(8.0F, 0.0F, -8.0F);
	}

	@Override
	public void setAngles(EngineerGolemEntity entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay);
	//	body.render(matrixStack, buffer, packedLight, packedOverlay);
	//	armRight.render(matrixStack, buffer, packedLight, packedOverlay);
	//	armLeft.render(matrixStack, buffer, packedLight, packedOverlay);
	//	legRight.render(matrixStack, buffer, packedLight, packedOverlay);
	//	legLeft.render(matrixStack, buffer, packedLight, packedOverlay);
	//	hat.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}