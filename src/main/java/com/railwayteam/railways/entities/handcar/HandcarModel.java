package com.railwayteam.railways.entities.handcar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.Arrays;

public class HandcarModel extends EntityModel<HandcarEntity> {
    @Override
    public void setAngles(HandcarEntity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {
        if(Minecraft.getInstance().isGamePaused()) return;
        if(entity.shouldMoveWheels()) {
            whole_axis_3.rotateAngleX += entity.getRotateWheelsBy(); // rotateAngleX and Z do the same thing, wtf?
        }
        if(entity.shouldPushWalkingBeam()) {
            if(bone3.rotateAngleX >= 0.4) {
                entity.pushDirection = false;
            } else if(bone3.rotateAngleX <= -0.4) {
                entity.pushDirection = true;
            }
            bone3.rotateAngleX += entity.getPushWalkingBeamBy() * (entity.pushDirection ? 1F : -1F);
//            bone3.rotateAngleX+=entity.getPushWalkingBeamBy();
        }
    }

//    @Override
//    public void setLivingAnimations(HandcarEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
//        super.setLivingAnimations(entity, uniqueID, customPredicate);
//
//        if (entity.isMoving && !Minecraft.getInstance().isGamePaused()) {
//            String l = "leftwheel";
//            String r = "rightwheel";
//            AnimationProcessor p = this.getAnimationProcessor();
//            IBone[] wheels = new IBone[]{p.getBone(r+"1"),p.getBone(r+"2"),p.getBone(l+"1"),p.getBone(l+"2")};
//            entity.wheelZ = entity.wheelZ % 360;
//            entity.wheelZ += entity.movementDirection ? toRotateWheels : -toRotateWheels;
//            for(IBone wheel : wheels) {
//                wheel.setRotationZ((float) entity.wheelZ);
//            }
//        }
//    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        Arrays.asList(whole_axis_2, whole_axis_3, bone3, bogies)
                .forEach(part -> part.render(matrixStack, buffer, packedLight, packedOverlay));
    }

    private final ModelRenderer whole_axis_2;
    private final ModelRenderer axis2;
    private final ModelRenderer whole_axis_3;
    private final ModelRenderer axis3;
    private final ModelRenderer rightwheel2;
    private final ModelRenderer rightwheel2_r1;
    private final ModelRenderer rightwheel2_r2;
    private final ModelRenderer rightwheel2_r3;
    private final ModelRenderer rightwheel2_r4;
    private final ModelRenderer rightwheel2_r5;
    private final ModelRenderer rightwheel2_r6;
    private final ModelRenderer rightwheel2_r7;
    private final ModelRenderer rightwheel1;
    private final ModelRenderer rightwheel1_r1;
    private final ModelRenderer rightwheel1_r2;
    private final ModelRenderer rightwheel1_r3;
    private final ModelRenderer rightwheel1_r4;
    private final ModelRenderer rightwheel1_r5;
    private final ModelRenderer rightwheel1_r6;
    private final ModelRenderer rightwheel1_r7;
    private final ModelRenderer leftwheel1;
    private final ModelRenderer leftwheel1_r1;
    private final ModelRenderer leftwheel1_r2;
    private final ModelRenderer leftwheel1_r3;
    private final ModelRenderer leftwheel1_r4;
    private final ModelRenderer leftwheel1_r5;
    private final ModelRenderer leftwheel1_r6;
    private final ModelRenderer leftwheel1_r7;
    private final ModelRenderer leftwheel2;
    private final ModelRenderer leftwheel2_r1;
    private final ModelRenderer leftwheel2_r2;
    private final ModelRenderer leftwheel2_r3;
    private final ModelRenderer leftwheel2_r4;
    private final ModelRenderer leftwheel2_r5;
    private final ModelRenderer leftwheel2_r6;
    private final ModelRenderer leftwheel2_r7;
    private final ModelRenderer bone3;
    private final ModelRenderer bone3_r1;
    private final ModelRenderer bone3_r2;
    private final ModelRenderer bone3_r3;
    private final ModelRenderer bone3_r4;
    private final ModelRenderer bogies;
    private final ModelRenderer FrontBogies;
    private final ModelRenderer wheel_beam;
    private final ModelRenderer bone;
    private final ModelRenderer springrecoil01;
    private final ModelRenderer bone2;
    private final ModelRenderer springrecoil2;
    private final ModelRenderer wheel_beam2;
    private final ModelRenderer wheel_beam2_r1;
    private final ModelRenderer wheel_beam2_r2;
    private final ModelRenderer wheel_beam2_r3;
    private final ModelRenderer wheel_beam2_r4;
    private final ModelRenderer wheel_beam2_r5;
    private final ModelRenderer wheel_beam2_r6;
    private final ModelRenderer wheel_beam2_r7;
    private final ModelRenderer center_plate;

    public HandcarModel() {
        textureWidth = 128;
        textureHeight = 128;

        whole_axis_2 = new ModelRenderer(this);
        whole_axis_2.setRotationPoint(0.0F, 17.0F, 18.0F);
        setRotationAngle(whole_axis_2, 0.0F, -1.5708F, 0.0F);


        axis2 = new ModelRenderer(this);
        axis2.setRotationPoint(-7.0F, 6.0F, 0.0F);
        whole_axis_2.addChild(axis2);
        axis2.setTextureOffset(0, 98).addCuboid(-3.0F, -7.0F, -13.0F, 2.0F, 2.0F, 26.0F, 0.0F, false);
        axis2.setTextureOffset(0, 98).addCuboid(-21.0F, -7.0F, -13.0F, 2.0F, 2.0F, 26.0F, 0.0F, false);

        whole_axis_3 = new ModelRenderer(this);
        whole_axis_3.setRotationPoint(0.0F, 18.0F, -8.0F);
        setRotationAngle(whole_axis_3, 0.0F, -1.5708F, 0.0F);


        axis3 = new ModelRenderer(this);
        axis3.setRotationPoint(1.0F, 5.0F, 0.0F);
        whole_axis_3.addChild(axis3);


        rightwheel2 = new ModelRenderer(this);
        rightwheel2.setRotationPoint(16.0F, -6.0F, 11.0F);
        axis3.addChild(rightwheel2);
        setRotationAngle(rightwheel2, 3.1416F, 0.0F, 0.0F);
        rightwheel2.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, 1.0F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        rightwheel2_r1 = new ModelRenderer(this);
        rightwheel2_r1.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel2.addChild(rightwheel2_r1);
        setRotationAngle(rightwheel2_r1, 0.0F, 0.0F, -2.3562F);
        rightwheel2_r1.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        rightwheel2_r2 = new ModelRenderer(this);
        rightwheel2_r2.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel2.addChild(rightwheel2_r2);
        setRotationAngle(rightwheel2_r2, 0.0F, 0.0F, -0.7854F);
        rightwheel2_r2.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        rightwheel2_r3 = new ModelRenderer(this);
        rightwheel2_r3.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel2.addChild(rightwheel2_r3);
        setRotationAngle(rightwheel2_r3, 0.0F, 0.0F, -1.5708F);
        rightwheel2_r3.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        rightwheel2_r4 = new ModelRenderer(this);
        rightwheel2_r4.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel2.addChild(rightwheel2_r4);
        setRotationAngle(rightwheel2_r4, 0.0F, 0.0F, -3.1416F);
        rightwheel2_r4.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        rightwheel2_r5 = new ModelRenderer(this);
        rightwheel2_r5.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel2.addChild(rightwheel2_r5);
        setRotationAngle(rightwheel2_r5, 0.0F, 0.0F, 2.3562F);
        rightwheel2_r5.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        rightwheel2_r6 = new ModelRenderer(this);
        rightwheel2_r6.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel2.addChild(rightwheel2_r6);
        setRotationAngle(rightwheel2_r6, 0.0F, 0.0F, 0.7854F);
        rightwheel2_r6.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        rightwheel2_r7 = new ModelRenderer(this);
        rightwheel2_r7.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel2.addChild(rightwheel2_r7);
        setRotationAngle(rightwheel2_r7, 0.0F, 0.0F, 1.5708F);
        rightwheel2_r7.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, -0.5F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        rightwheel1 = new ModelRenderer(this);
        rightwheel1.setRotationPoint(-2.0F, -6.0F, 11.0F);
        axis3.addChild(rightwheel1);
        setRotationAngle(rightwheel1, 3.1416F, 0.0F, 0.0F);
        rightwheel1.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, 1.0F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        rightwheel1_r1 = new ModelRenderer(this);
        rightwheel1_r1.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel1.addChild(rightwheel1_r1);
        setRotationAngle(rightwheel1_r1, 0.0F, 0.0F, -2.3562F);
        rightwheel1_r1.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        rightwheel1_r2 = new ModelRenderer(this);
        rightwheel1_r2.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel1.addChild(rightwheel1_r2);
        setRotationAngle(rightwheel1_r2, 0.0F, 0.0F, -0.7854F);
        rightwheel1_r2.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        rightwheel1_r3 = new ModelRenderer(this);
        rightwheel1_r3.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel1.addChild(rightwheel1_r3);
        setRotationAngle(rightwheel1_r3, 0.0F, 0.0F, -1.5708F);
        rightwheel1_r3.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        rightwheel1_r4 = new ModelRenderer(this);
        rightwheel1_r4.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel1.addChild(rightwheel1_r4);
        setRotationAngle(rightwheel1_r4, 0.0F, 0.0F, -3.1416F);
        rightwheel1_r4.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        rightwheel1_r5 = new ModelRenderer(this);
        rightwheel1_r5.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel1.addChild(rightwheel1_r5);
        setRotationAngle(rightwheel1_r5, 0.0F, 0.0F, 2.3562F);
        rightwheel1_r5.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        rightwheel1_r6 = new ModelRenderer(this);
        rightwheel1_r6.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel1.addChild(rightwheel1_r6);
        setRotationAngle(rightwheel1_r6, 0.0F, 0.0F, 0.7854F);
        rightwheel1_r6.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        rightwheel1_r7 = new ModelRenderer(this);
        rightwheel1_r7.setRotationPoint(0.0F, 0.0F, 1.5F);
        rightwheel1.addChild(rightwheel1_r7);
        setRotationAngle(rightwheel1_r7, 0.0F, 0.0F, 1.5708F);
        rightwheel1_r7.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, -0.5F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        leftwheel1 = new ModelRenderer(this);
        leftwheel1.setRotationPoint(-2.0F, -6.0F, -11.0F);
        axis3.addChild(leftwheel1);
        setRotationAngle(leftwheel1, 0.0F, 0.0F, 3.1416F);
        leftwheel1.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, 1.0F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        leftwheel1_r1 = new ModelRenderer(this);
        leftwheel1_r1.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel1.addChild(leftwheel1_r1);
        setRotationAngle(leftwheel1_r1, 0.0F, 0.0F, -2.3562F);
        leftwheel1_r1.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        leftwheel1_r2 = new ModelRenderer(this);
        leftwheel1_r2.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel1.addChild(leftwheel1_r2);
        setRotationAngle(leftwheel1_r2, 0.0F, 0.0F, -0.7854F);
        leftwheel1_r2.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        leftwheel1_r3 = new ModelRenderer(this);
        leftwheel1_r3.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel1.addChild(leftwheel1_r3);
        setRotationAngle(leftwheel1_r3, 0.0F, 0.0F, -1.5708F);
        leftwheel1_r3.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        leftwheel1_r4 = new ModelRenderer(this);
        leftwheel1_r4.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel1.addChild(leftwheel1_r4);
        setRotationAngle(leftwheel1_r4, 0.0F, 0.0F, -3.1416F);
        leftwheel1_r4.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        leftwheel1_r5 = new ModelRenderer(this);
        leftwheel1_r5.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel1.addChild(leftwheel1_r5);
        setRotationAngle(leftwheel1_r5, 0.0F, 0.0F, 2.3562F);
        leftwheel1_r5.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        leftwheel1_r6 = new ModelRenderer(this);
        leftwheel1_r6.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel1.addChild(leftwheel1_r6);
        setRotationAngle(leftwheel1_r6, 0.0F, 0.0F, 0.7854F);
        leftwheel1_r6.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        leftwheel1_r7 = new ModelRenderer(this);
        leftwheel1_r7.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel1.addChild(leftwheel1_r7);
        setRotationAngle(leftwheel1_r7, 0.0F, 0.0F, 1.5708F);
        leftwheel1_r7.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, -0.5F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        leftwheel2 = new ModelRenderer(this);
        leftwheel2.setRotationPoint(16.0F, -6.0F, -11.0F);
        axis3.addChild(leftwheel2);
        setRotationAngle(leftwheel2, 0.0F, 0.0F, 3.1416F);
        leftwheel2.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, 1.0F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        leftwheel2_r1 = new ModelRenderer(this);
        leftwheel2_r1.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel2.addChild(leftwheel2_r1);
        setRotationAngle(leftwheel2_r1, 0.0F, 0.0F, -2.3562F);
        leftwheel2_r1.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        leftwheel2_r2 = new ModelRenderer(this);
        leftwheel2_r2.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel2.addChild(leftwheel2_r2);
        setRotationAngle(leftwheel2_r2, 0.0F, 0.0F, -0.7854F);
        leftwheel2_r2.setTextureOffset(0, 0).addCuboid(-4.2426F, -1.4142F, -2.5F, 8.0F, 2.0F, 2.0F, 0.0F, false);

        leftwheel2_r3 = new ModelRenderer(this);
        leftwheel2_r3.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel2.addChild(leftwheel2_r3);
        setRotationAngle(leftwheel2_r3, 0.0F, 0.0F, -1.5708F);
        leftwheel2_r3.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        leftwheel2_r4 = new ModelRenderer(this);
        leftwheel2_r4.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel2.addChild(leftwheel2_r4);
        setRotationAngle(leftwheel2_r4, 0.0F, 0.0F, -3.1416F);
        leftwheel2_r4.setTextureOffset(0, 4).addCuboid(-4.0F, -2.0F, -2.5F, 8.0F, 4.0F, 2.0F, 0.0F, false);

        leftwheel2_r5 = new ModelRenderer(this);
        leftwheel2_r5.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel2.addChild(leftwheel2_r5);
        setRotationAngle(leftwheel2_r5, 0.0F, 0.0F, 2.3562F);
        leftwheel2_r5.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        leftwheel2_r6 = new ModelRenderer(this);
        leftwheel2_r6.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel2.addChild(leftwheel2_r6);
        setRotationAngle(leftwheel2_r6, 0.0F, 0.0F, 0.7854F);
        leftwheel2_r6.setTextureOffset(0, 10).addCuboid(-5.3033F, -1.7678F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);

        leftwheel2_r7 = new ModelRenderer(this);
        leftwheel2_r7.setRotationPoint(0.0F, 0.0F, 1.5F);
        leftwheel2.addChild(leftwheel2_r7);
        setRotationAngle(leftwheel2_r7, 0.0F, 0.0F, 1.5708F);
        leftwheel2_r7.setTextureOffset(0, 14).addCuboid(-5.0F, -2.5F, -0.5F, 10.0F, 5.0F, 1.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
        bone3.setTextureOffset(0, 54).addCuboid(-6.5F, -0.5F, -7.5F, 13.0F, 1.0F, 1.0F, 0.0F, false);
        bone3.setTextureOffset(0, 52).addCuboid(-1.5F, -0.5F, 5.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        bone3.setTextureOffset(0, 44).addCuboid(-4.0F, -0.5F, -0.5F, 8.0F, 1.0F, 1.0F, 0.0F, false);
        bone3.setTextureOffset(0, 37).addCuboid(-3.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        bone3.setTextureOffset(0, 46).addCuboid(-0.5F, -0.5F, -5.5F, 1.0F, 1.0F, 5.0F, 0.0F, false);

        bone3_r1 = new ModelRenderer(this);
        bone3_r1.setRotationPoint(0.0F, 0.0F, -6.0F);
        bone3.addChild(bone3_r1);
        setRotationAngle(bone3_r1, 0.0F, 3.1416F, 0.0F);
        bone3_r1.setTextureOffset(0, 52).addCuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        bone3_r2 = new ModelRenderer(this);
        bone3_r2.setRotationPoint(8.5F, 12.0F, 0.0F);
        bone3.addChild(bone3_r2);
        setRotationAngle(bone3_r2, 0.0F, 3.1416F, 0.0F);
        bone3_r2.setTextureOffset(0, 37).addCuboid(5.5F, -13.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        bone3_r3 = new ModelRenderer(this);
        bone3_r3.setRotationPoint(0.0F, 0.0F, 3.0F);
        bone3.addChild(bone3_r3);
        setRotationAngle(bone3_r3, 0.0F, 3.1416F, 0.0F);
        bone3_r3.setTextureOffset(0, 46).addCuboid(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 5.0F, 0.0F, false);

        bone3_r4 = new ModelRenderer(this);
        bone3_r4.setRotationPoint(0.0F, 0.0F, 7.0F);
        bone3.addChild(bone3_r4);
        setRotationAngle(bone3_r4, 0.0F, 3.1416F, 0.0F);
        bone3_r4.setTextureOffset(0, 54).addCuboid(-6.5F, -0.5F, -0.5F, 13.0F, 1.0F, 1.0F, 0.0F, false);

        bogies = new ModelRenderer(this);
        bogies.setRotationPoint(0.0F, 24.0F, -39.0F);


        FrontBogies = new ModelRenderer(this);
        FrontBogies.setRotationPoint(0.0F, -8.45F, 39.0F);
        bogies.addChild(FrontBogies);


        wheel_beam = new ModelRenderer(this);
        wheel_beam.setRotationPoint(-12.0F, -1.55F, 5.0F);
        FrontBogies.addChild(wheel_beam);


        bone = new ModelRenderer(this);
        bone.setRotationPoint(15.0F, 1.0F, 0.0F);
        wheel_beam.addChild(bone);


        springrecoil01 = new ModelRenderer(this);
        springrecoil01.setRotationPoint(-17.0F, 1.0F, 12.0F);
        bone.addChild(springrecoil01);
        setRotationAngle(springrecoil01, -0.9425F, 0.0F, 0.0F);


        bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(-1.0F, 1.0F, 0.0F);
        wheel_beam.addChild(bone2);
        setRotationAngle(bone2, 0.0F, 3.1416F, 0.0F);


        springrecoil2 = new ModelRenderer(this);
        springrecoil2.setRotationPoint(-1.0F, 1.0F, 12.0F);
        bone2.addChild(springrecoil2);
        setRotationAngle(springrecoil2, -0.9425F, 0.0F, 0.0F);


        wheel_beam2 = new ModelRenderer(this);
        wheel_beam2.setRotationPoint(12.0F, -1.55F, 5.0F);
        FrontBogies.addChild(wheel_beam2);
        setRotationAngle(wheel_beam2, 0.0F, 3.1416F, 0.0F);
        wheel_beam2.setTextureOffset(42, 84).addCuboid(4.0F, 0.0F, -16.0F, 1.0F, 2.0F, 42.0F, 0.0F, false);

        wheel_beam2_r1 = new ModelRenderer(this);
        wheel_beam2_r1.setRotationPoint(20.5F, 1.0F, 5.0F);
        wheel_beam2.addChild(wheel_beam2_r1);
        setRotationAngle(wheel_beam2_r1, 0.0F, 3.1416F, 0.0F);
        wheel_beam2_r1.setTextureOffset(62, 16).addCuboid(-0.5F, -2.0F, -20.0F, 18.0F, 1.0F, 15.0F, 0.0F, false);
        wheel_beam2_r1.setTextureOffset(62, 0).addCuboid(-0.5F, -2.0F, 5.0F, 18.0F, 1.0F, 15.0F, 0.0F, false);
        wheel_beam2_r1.setTextureOffset(114, 56).addCuboid(10.5F, -14.0F, -3.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        wheel_beam2_r1.setTextureOffset(94, 56).addCuboid(11.5F, -4.0F, -8.0F, 1.0F, 2.0F, 16.0F, 0.0F, false);
        wheel_beam2_r1.setTextureOffset(94, 56).addCuboid(4.5F, -4.0F, -8.0F, 1.0F, 2.0F, 16.0F, 0.0F, false);
        wheel_beam2_r1.setTextureOffset(114, 56).addCuboid(5.5F, -14.0F, -3.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        wheel_beam2_r1.setTextureOffset(67, 43).addCuboid(-0.5F, -2.0F, -5.0F, 7.0F, 1.0F, 10.0F, 0.0F, false);
        wheel_beam2_r1.setTextureOffset(42, 84).addCuboid(0.5F, -1.0F, -21.0F, 1.0F, 2.0F, 42.0F, 0.0F, false);

        wheel_beam2_r2 = new ModelRenderer(this);
        wheel_beam2_r2.setRotationPoint(14.5F, -13.0F, 2.0F);
        wheel_beam2.addChild(wheel_beam2_r2);
        setRotationAngle(wheel_beam2_r2, -1.1781F, 0.0F, 0.0F);
        wheel_beam2_r2.setTextureOffset(118, 34).addCuboid(-4.5F, -5.5433F, 6.2961F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        wheel_beam2_r2.setTextureOffset(100, 42).addCuboid(-5.5F, -5.5433F, 2.2961F, 1.0F, 1.0F, 13.0F, 0.0F, false);
        wheel_beam2_r2.setTextureOffset(100, 42).addCuboid(-0.5F, -5.5433F, 2.2961F, 1.0F, 1.0F, 13.0F, 0.0F, false);

        wheel_beam2_r3 = new ModelRenderer(this);
        wheel_beam2_r3.setRotationPoint(12.0F, -2.184F, 11.9389F);
        wheel_beam2.addChild(wheel_beam2_r3);
        setRotationAngle(wheel_beam2_r3, -1.9635F, 0.0F, -3.1416F);
        wheel_beam2_r3.setTextureOffset(114, 36).addCuboid(-2.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F, 0.0F, false);

        wheel_beam2_r4 = new ModelRenderer(this);
        wheel_beam2_r4.setRotationPoint(14.5F, -13.0F, 3.0F);
        wheel_beam2.addChild(wheel_beam2_r4);
        setRotationAngle(wheel_beam2_r4, -1.1781F, 3.1416F, 0.0F);
        wheel_beam2_r4.setTextureOffset(100, 42).addCuboid(-0.5F, -0.9239F, 0.3827F, 1.0F, 1.0F, 13.0F, 0.0F, false);

        wheel_beam2_r5 = new ModelRenderer(this);
        wheel_beam2_r5.setRotationPoint(9.5F, -13.0F, 3.0F);
        wheel_beam2.addChild(wheel_beam2_r5);
        setRotationAngle(wheel_beam2_r5, -1.1781F, 3.1416F, 0.0F);
        wheel_beam2_r5.setTextureOffset(118, 34).addCuboid(-4.5F, -0.9239F, 4.3827F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        wheel_beam2_r5.setTextureOffset(100, 42).addCuboid(-0.5F, -0.9239F, 0.3827F, 1.0F, 1.0F, 13.0F, 0.0F, false);

        wheel_beam2_r6 = new ModelRenderer(this);
        wheel_beam2_r6.setRotationPoint(12.0F, -2.184F, -1.9389F);
        wheel_beam2.addChild(wheel_beam2_r6);
        setRotationAngle(wheel_beam2_r6, 1.1781F, 0.0F, 0.0F);
        wheel_beam2_r6.setTextureOffset(114, 36).addCuboid(-2.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F, 0.0F, false);

        wheel_beam2_r7 = new ModelRenderer(this);
        wheel_beam2_r7.setRotationPoint(6.5F, -0.5F, 5.0F);
        wheel_beam2.addChild(wheel_beam2_r7);
        setRotationAngle(wheel_beam2_r7, 0.0F, 3.1416F, 0.0F);
        wheel_beam2_r7.setTextureOffset(67, 32).addCuboid(-3.5F, -0.5F, -5.0F, 7.0F, 1.0F, 10.0F, 0.0F, false);

        center_plate = new ModelRenderer(this);
        center_plate.setRotationPoint(0.0F, 0.0F, 0.0F);
        FrontBogies.addChild(center_plate);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
