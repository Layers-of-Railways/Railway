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
            double rotateBy = entity.getRotateWheelsBy();
            Arrays.asList(RightWheel1, RightWheel2, LeftWheel1, LeftWheel2, Axle1, Axle2).forEach(part -> {
                part.rotateAngleZ %= Math.PI;
                part.rotateAngleZ += rotateBy;
            });
        }
        if(entity.shouldPushWalkingBeam()) {
            if(WalkingBeam.rotateAngleX >= 0.4) {
                entity.pushDirection = false;
            } else if(WalkingBeam.rotateAngleX <= -0.4) {
                entity.pushDirection = true;
            }
            WalkingBeam.rotateAngleX += entity.getPushWalkingBeamBy() * (entity.pushDirection ? 1D : -1D);
//            bone3.rotateAngleX+=entity.getPushWalkingBeamBy();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        Arrays.asList(Axles, WalkingBeam, Wheels, Body)
                .forEach(part -> part.render(matrixStack, buffer, packedLight, packedOverlay));
    }

    public HandcarModel() {
        textureWidth = 128;
        textureHeight = 128;

        Axles = new ModelRenderer(this);
        Axles.setRotationPoint(0.0F, 17.0F, 18.0F);
        setRotationAngle(Axles, 0.0F, -1.5708F, 0.0F);


        Axle2 = new ModelRenderer(this);
        Axle2.setRotationPoint(-9.0F, 0.0F, 0.0F);
        Axles.addChild(Axle2);
        Axle2.setTextureOffset(0, 98).addCuboid(-1.0F, -1.0F, -12.8F, 2.0F, 2.0F, 26.0F, 0.0F, false);

        Axle1 = new ModelRenderer(this);
        Axle1.setRotationPoint(-27.0F, 0.0F, 0.0F);
        Axles.addChild(Axle1);
        Axle1.setTextureOffset(0, 98).addCuboid(-1.0F, -1.0F, -12.8F, 2.0F, 2.0F, 26.0F, 0.0F, false);

        WalkingBeam = new ModelRenderer(this);
        WalkingBeam.setRotationPoint(1.3333F, 0.0F, 0.0F);
        WalkingBeam.setTextureOffset(0, 54).addCuboid(-7.8333F, -0.5F, -7.5F, 13.0F, 1.0F, 1.0F, 0.0F, false);
        WalkingBeam.setTextureOffset(0, 52).addCuboid(-2.8333F, -0.5F, 5.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        WalkingBeam.setTextureOffset(0, 44).addCuboid(-5.3333F, -0.5F, -0.5F, 8.0F, 1.0F, 1.0F, 0.0F, false);
        WalkingBeam.setTextureOffset(0, 37).addCuboid(-4.3333F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        WalkingBeam.setTextureOffset(0, 46).addCuboid(-1.8333F, -0.5F, -5.5F, 1.0F, 1.0F, 5.0F, 0.0F, false);

        bone3_r1 = new ModelRenderer(this);
        bone3_r1.setRotationPoint(-1.3333F, 0.0F, -6.0F);
        WalkingBeam.addChild(bone3_r1);
        setRotationAngle(bone3_r1, 0.0F, 3.1416F, 0.0F);
        bone3_r1.setTextureOffset(0, 52).addCuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        bone3_r2 = new ModelRenderer(this);
        bone3_r2.setRotationPoint(7.1667F, 12.0F, 0.0F);
        WalkingBeam.addChild(bone3_r2);
        setRotationAngle(bone3_r2, 0.0F, 3.1416F, 0.0F);
        bone3_r2.setTextureOffset(0, 37).addCuboid(5.5F, -13.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        bone3_r3 = new ModelRenderer(this);
        bone3_r3.setRotationPoint(-1.3333F, 0.0F, 3.0F);
        WalkingBeam.addChild(bone3_r3);
        setRotationAngle(bone3_r3, 0.0F, 3.1416F, 0.0F);
        bone3_r3.setTextureOffset(0, 46).addCuboid(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 5.0F, 0.0F, false);

        bone3_r4 = new ModelRenderer(this);
        bone3_r4.setRotationPoint(-1.3333F, 0.0F, 7.0F);
        WalkingBeam.addChild(bone3_r4);
        setRotationAngle(bone3_r4, 0.0F, 3.1416F, 0.0F);
        bone3_r4.setTextureOffset(0, 54).addCuboid(-6.5F, -0.5F, -0.5F, 13.0F, 1.0F, 1.0F, 0.0F, false);

        Wheels = new ModelRenderer(this);
        Wheels.setRotationPoint(0.0F, 18.0F, -8.0F);
        setRotationAngle(Wheels, 0.0F, -1.5708F, 0.0F);


        RightWheel2 = new ModelRenderer(this);
        RightWheel2.setRotationPoint(17.0F, -1.0F, 11.75F);
        Wheels.addChild(RightWheel2);
        setRotationAngle(RightWheel2, 3.1416F, 0.0F, 0.0F);


        octagon_r1 = new ModelRenderer(this);
        octagon_r1.setRotationPoint(0.0F, 0.0F, 1.25F);
        RightWheel2.addChild(octagon_r1);
        setRotationAngle(octagon_r1, 0.0F, -1.4835F, 0.0F);
        octagon_r1.setTextureOffset(118, 110).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r1.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r2 = new ModelRenderer(this);
        octagon_r2.setRotationPoint(0.0F, 0.0F, 1.25F);
        RightWheel2.addChild(octagon_r2);
        setRotationAngle(octagon_r2, -0.7854F, -1.4835F, 0.0F);
        octagon_r2.setTextureOffset(118, 111).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r2.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r3 = new ModelRenderer(this);
        octagon_r3.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel2.addChild(octagon_r3);
        setRotationAngle(octagon_r3, 0.0F, -1.5708F, 0.0F);
        octagon_r3.setTextureOffset(0, 111).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r3.setTextureOffset(33, 110).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        octagon_r4 = new ModelRenderer(this);
        octagon_r4.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel2.addChild(octagon_r4);
        setRotationAngle(octagon_r4, 0.0F, -1.5708F, -0.7854F);
        octagon_r4.setTextureOffset(72, 114).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r4.setTextureOffset(0, 97).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        rightwheel2_r1 = new ModelRenderer(this);
        rightwheel2_r1.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel2.addChild(rightwheel2_r1);
        setRotationAngle(rightwheel2_r1, 0.0F, 0.0F, -2.3562F);


        rightwheel2_r2 = new ModelRenderer(this);
        rightwheel2_r2.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel2.addChild(rightwheel2_r2);
        setRotationAngle(rightwheel2_r2, 0.0F, 0.0F, -0.7854F);


        rightwheel2_r3 = new ModelRenderer(this);
        rightwheel2_r3.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel2.addChild(rightwheel2_r3);
        setRotationAngle(rightwheel2_r3, 0.0F, 0.0F, -1.5708F);


        rightwheel2_r4 = new ModelRenderer(this);
        rightwheel2_r4.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel2.addChild(rightwheel2_r4);
        setRotationAngle(rightwheel2_r4, 0.0F, 0.0F, -3.1416F);


        rightwheel2_r5 = new ModelRenderer(this);
        rightwheel2_r5.setRotationPoint(0.0F, 0.0F, 1.75F);
        RightWheel2.addChild(rightwheel2_r5);
        setRotationAngle(rightwheel2_r5, 0.0F, 0.0F, 2.3562F);


        rightwheel2_r6 = new ModelRenderer(this);
        rightwheel2_r6.setRotationPoint(0.0F, 0.0F, 1.75F);
        RightWheel2.addChild(rightwheel2_r6);
        setRotationAngle(rightwheel2_r6, 0.0F, 0.0F, 0.7854F);


        rightwheel2_r7 = new ModelRenderer(this);
        rightwheel2_r7.setRotationPoint(0.0F, 0.0F, 1.75F);
        RightWheel2.addChild(rightwheel2_r7);
        setRotationAngle(rightwheel2_r7, 0.0F, 0.0F, 1.5708F);


        RightWheel1 = new ModelRenderer(this);
        RightWheel1.setRotationPoint(-1.0F, -1.0F, 11.75F);
        Wheels.addChild(RightWheel1);
        setRotationAngle(RightWheel1, 3.1416F, 0.0F, 0.0F);


        octagon_r5 = new ModelRenderer(this);
        octagon_r5.setRotationPoint(0.0F, 0.0F, 1.25F);
        RightWheel1.addChild(octagon_r5);
        setRotationAngle(octagon_r5, 0.0F, -1.5708F, 0.0F);
        octagon_r5.setTextureOffset(118, 110).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r5.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r6 = new ModelRenderer(this);
        octagon_r6.setRotationPoint(0.0F, 0.0F, 1.25F);
        RightWheel1.addChild(octagon_r6);
        setRotationAngle(octagon_r6, 0.0F, -1.5708F, -0.7854F);
        octagon_r6.setTextureOffset(118, 111).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r6.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r7 = new ModelRenderer(this);
        octagon_r7.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel1.addChild(octagon_r7);
        setRotationAngle(octagon_r7, 0.0F, -1.5708F, 0.0F);
        octagon_r7.setTextureOffset(0, 111).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r7.setTextureOffset(33, 110).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        octagon_r8 = new ModelRenderer(this);
        octagon_r8.setRotationPoint(0.0F, 0.0F, 0.25F);
        RightWheel1.addChild(octagon_r8);
        setRotationAngle(octagon_r8, 0.0F, -1.5708F, -0.7854F);
        octagon_r8.setTextureOffset(72, 114).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r8.setTextureOffset(0, 97).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        rightwheel1_r5 = new ModelRenderer(this);
        rightwheel1_r5.setRotationPoint(0.0F, 0.0F, 1.75F);
        RightWheel1.addChild(rightwheel1_r5);
        setRotationAngle(rightwheel1_r5, 0.0F, 0.0F, 2.3562F);


        rightwheel1_r6 = new ModelRenderer(this);
        rightwheel1_r6.setRotationPoint(0.0F, 0.0F, 1.75F);
        RightWheel1.addChild(rightwheel1_r6);
        setRotationAngle(rightwheel1_r6, 0.0F, 0.0F, 0.7854F);


        rightwheel1_r7 = new ModelRenderer(this);
        rightwheel1_r7.setRotationPoint(0.0F, 0.0F, 1.75F);
        RightWheel1.addChild(rightwheel1_r7);
        setRotationAngle(rightwheel1_r7, 0.0F, 0.0F, 1.5708F);


        LeftWheel1 = new ModelRenderer(this);
        LeftWheel1.setRotationPoint(-1.0F, -1.0F, -10.25F);
        Wheels.addChild(LeftWheel1);
        setRotationAngle(LeftWheel1, 0.0F, 0.0F, 3.1416F);


        octagon_r9 = new ModelRenderer(this);
        octagon_r9.setRotationPoint(0.0F, 0.0F, 0.25F);
        LeftWheel1.addChild(octagon_r9);
        setRotationAngle(octagon_r9, -3.1416F, 1.5708F, 3.1416F);
        octagon_r9.setTextureOffset(118, 110).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r9.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r10 = new ModelRenderer(this);
        octagon_r10.setRotationPoint(0.0F, 0.0F, 0.25F);
        LeftWheel1.addChild(octagon_r10);
        setRotationAngle(octagon_r10, 2.3562F, 1.5708F, 3.1416F);
        octagon_r10.setTextureOffset(118, 111).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r10.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r11 = new ModelRenderer(this);
        octagon_r11.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel1.addChild(octagon_r11);
        setRotationAngle(octagon_r11, 0.0F, -1.5708F, 0.0F);
        octagon_r11.setTextureOffset(0, 111).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r11.setTextureOffset(33, 110).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        octagon_r12 = new ModelRenderer(this);
        octagon_r12.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel1.addChild(octagon_r12);
        setRotationAngle(octagon_r12, 0.0F, -1.5708F, -0.7854F);
        octagon_r12.setTextureOffset(72, 114).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r12.setTextureOffset(0, 97).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        leftwheel1_r1 = new ModelRenderer(this);
        leftwheel1_r1.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel1.addChild(leftwheel1_r1);
        setRotationAngle(leftwheel1_r1, 0.0F, 0.0F, -2.3562F);


        leftwheel1_r2 = new ModelRenderer(this);
        leftwheel1_r2.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel1.addChild(leftwheel1_r2);
        setRotationAngle(leftwheel1_r2, 0.0F, 0.0F, -0.7854F);


        leftwheel1_r3 = new ModelRenderer(this);
        leftwheel1_r3.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel1.addChild(leftwheel1_r3);
        setRotationAngle(leftwheel1_r3, 0.0F, 0.0F, -1.5708F);


        leftwheel1_r4 = new ModelRenderer(this);
        leftwheel1_r4.setRotationPoint(0.0F, 0.0F, 0.75F);
        LeftWheel1.addChild(leftwheel1_r4);
        setRotationAngle(leftwheel1_r4, 0.0F, 0.0F, -3.1416F);


        leftwheel1_r5 = new ModelRenderer(this);
        leftwheel1_r5.setRotationPoint(0.0F, 0.0F, 0.75F);
        LeftWheel1.addChild(leftwheel1_r5);
        setRotationAngle(leftwheel1_r5, 0.0F, 0.0F, 2.3562F);


        leftwheel1_r6 = new ModelRenderer(this);
        leftwheel1_r6.setRotationPoint(0.0F, 0.0F, 0.75F);
        LeftWheel1.addChild(leftwheel1_r6);
        setRotationAngle(leftwheel1_r6, 0.0F, 0.0F, 0.7854F);


        leftwheel1_r7 = new ModelRenderer(this);
        leftwheel1_r7.setRotationPoint(0.0F, 0.0F, 0.75F);
        LeftWheel1.addChild(leftwheel1_r7);
        setRotationAngle(leftwheel1_r7, 0.0F, 0.0F, 1.5708F);


        LeftWheel2 = new ModelRenderer(this);
        LeftWheel2.setRotationPoint(17.0F, -1.0F, -10.25F);
        Wheels.addChild(LeftWheel2);
        setRotationAngle(LeftWheel2, 0.0F, 0.0F, 3.1416F);


        octagon_r13 = new ModelRenderer(this);
        octagon_r13.setRotationPoint(0.0F, 0.0F, 0.25F);
        LeftWheel2.addChild(octagon_r13);
        setRotationAngle(octagon_r13, -3.1416F, -1.5708F, 3.1416F);
        octagon_r13.setTextureOffset(118, 110).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r13.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r14 = new ModelRenderer(this);
        octagon_r14.setRotationPoint(0.0F, 0.0F, 0.25F);
        LeftWheel2.addChild(octagon_r14);
        setRotationAngle(octagon_r14, 2.3562F, -1.5708F, -3.1416F);
        octagon_r14.setTextureOffset(118, 111).addCuboid(-0.5F, -5.0F, -2.0711F, 1.0F, 10.0F, 4.0F, 0.0F, false);
        octagon_r14.setTextureOffset(106, 111).addCuboid(-0.5F, -2.0711F, -5.0F, 1.0F, 4.0F, 10.0F, 0.0F, false);

        octagon_r15 = new ModelRenderer(this);
        octagon_r15.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel2.addChild(octagon_r15);
        setRotationAngle(octagon_r15, 0.0F, -1.5708F, 0.0F);
        octagon_r15.setTextureOffset(0, 111).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r15.setTextureOffset(33, 110).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        octagon_r16 = new ModelRenderer(this);
        octagon_r16.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel2.addChild(octagon_r16);
        setRotationAngle(octagon_r16, 0.0F, -1.5708F, -0.7854F);
        octagon_r16.setTextureOffset(72, 114).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        octagon_r16.setTextureOffset(0, 97).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        leftwheel2_r1 = new ModelRenderer(this);
        leftwheel2_r1.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel2.addChild(leftwheel2_r1);
        setRotationAngle(leftwheel2_r1, 0.0F, 0.0F, -2.3562F);


        leftwheel2_r2 = new ModelRenderer(this);
        leftwheel2_r2.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel2.addChild(leftwheel2_r2);
        setRotationAngle(leftwheel2_r2, 0.0F, 0.0F, -0.7854F);


        leftwheel2_r3 = new ModelRenderer(this);
        leftwheel2_r3.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel2.addChild(leftwheel2_r3);
        setRotationAngle(leftwheel2_r3, 0.0F, 0.0F, -1.5708F);


        leftwheel2_r4 = new ModelRenderer(this);
        leftwheel2_r4.setRotationPoint(0.0F, 0.0F, -0.75F);
        LeftWheel2.addChild(leftwheel2_r4);
        setRotationAngle(leftwheel2_r4, 0.0F, 0.0F, -3.1416F);


        leftwheel2_r5 = new ModelRenderer(this);
        leftwheel2_r5.setRotationPoint(0.0F, 0.0F, 0.75F);
        LeftWheel2.addChild(leftwheel2_r5);
        setRotationAngle(leftwheel2_r5, 0.0F, 0.0F, 2.3562F);


        leftwheel2_r6 = new ModelRenderer(this);
        leftwheel2_r6.setRotationPoint(0.0F, 0.0F, 0.75F);
        LeftWheel2.addChild(leftwheel2_r6);
        setRotationAngle(leftwheel2_r6, 0.0F, 0.0F, 0.7854F);


        leftwheel2_r7 = new ModelRenderer(this);
        leftwheel2_r7.setRotationPoint(0.0F, 0.0F, 0.75F);
        LeftWheel2.addChild(leftwheel2_r7);
        setRotationAngle(leftwheel2_r7, 0.0F, 0.0F, 1.5708F);


        Body = new ModelRenderer(this);
        Body.setRotationPoint(-6.1667F, 6.9017F, -1.4424F);
        setRotationAngle(Body, 0.0F, 3.1416F, 0.0F);
        Body.setTextureOffset(42, 84).addCuboid(-13.8333F, 7.0983F, -22.5576F, 1.0F, 2.0F, 42.0F, 0.0F, false);

        wheel_beam2_r1 = new ModelRenderer(this);
        wheel_beam2_r1.setRotationPoint(2.6667F, 8.0983F, -1.5576F);
        Body.addChild(wheel_beam2_r1);
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
        wheel_beam2_r2.setRotationPoint(-3.3333F, -5.9017F, -4.5576F);
        Body.addChild(wheel_beam2_r2);
        setRotationAngle(wheel_beam2_r2, -1.1781F, 0.0F, 0.0F);
        wheel_beam2_r2.setTextureOffset(118, 34).addCuboid(-4.5F, -5.5433F, 6.2961F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        wheel_beam2_r2.setTextureOffset(100, 42).addCuboid(-5.5F, -5.5433F, 2.2961F, 1.0F, 1.0F, 13.0F, 0.0F, false);
        wheel_beam2_r2.setTextureOffset(100, 42).addCuboid(-0.5F, -5.5433F, 2.2961F, 1.0F, 1.0F, 13.0F, 0.0F, false);

        wheel_beam2_r3 = new ModelRenderer(this);
        wheel_beam2_r3.setRotationPoint(-5.8333F, 4.9143F, 5.3813F);
        Body.addChild(wheel_beam2_r3);
        setRotationAngle(wheel_beam2_r3, -1.9635F, 0.0F, -3.1416F);
        wheel_beam2_r3.setTextureOffset(114, 36).addCuboid(-2.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F, 0.0F, false);

        wheel_beam2_r4 = new ModelRenderer(this);
        wheel_beam2_r4.setRotationPoint(-3.3333F, -5.9017F, -3.5576F);
        Body.addChild(wheel_beam2_r4);
        setRotationAngle(wheel_beam2_r4, -1.1781F, 3.1416F, 0.0F);
        wheel_beam2_r4.setTextureOffset(100, 42).addCuboid(-0.5F, -0.9239F, 0.3827F, 1.0F, 1.0F, 13.0F, 0.0F, false);

        wheel_beam2_r5 = new ModelRenderer(this);
        wheel_beam2_r5.setRotationPoint(-8.3333F, -5.9017F, -3.5576F);
        Body.addChild(wheel_beam2_r5);
        setRotationAngle(wheel_beam2_r5, -1.1781F, 3.1416F, 0.0F);
        wheel_beam2_r5.setTextureOffset(118, 34).addCuboid(-4.5F, -0.9239F, 4.3827F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        wheel_beam2_r5.setTextureOffset(100, 42).addCuboid(-0.5F, -0.9239F, 0.3827F, 1.0F, 1.0F, 13.0F, 0.0F, false);

        wheel_beam2_r6 = new ModelRenderer(this);
        wheel_beam2_r6.setRotationPoint(-5.8333F, 4.9143F, -8.4965F);
        Body.addChild(wheel_beam2_r6);
        setRotationAngle(wheel_beam2_r6, 1.1781F, 0.0F, 0.0F);
        wheel_beam2_r6.setTextureOffset(114, 36).addCuboid(-2.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F, 0.0F, false);

        wheel_beam2_r7 = new ModelRenderer(this);
        wheel_beam2_r7.setRotationPoint(-11.3333F, 6.5983F, -1.5576F);
        Body.addChild(wheel_beam2_r7);
        setRotationAngle(wheel_beam2_r7, 0.0F, 3.1416F, 0.0F);
        wheel_beam2_r7.setTextureOffset(67, 32).addCuboid(-3.5F, -0.5F, -5.0F, 7.0F, 1.0F, 10.0F, 0.0F, false);

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);


        rightwheel1_r11_r1 = new ModelRenderer(this);
        rightwheel1_r11_r1.setRotationPoint(-1.0F, -7.0F, 4.0F);
        bb_main.addChild(rightwheel1_r11_r1);
        setRotationAngle(rightwheel1_r11_r1, 0.0F, -1.5708F, 0.0F);
        rightwheel1_r11_r1.setTextureOffset(0, 111).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        rightwheel1_r11_r1.setTextureOffset(33, 110).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);

        rightwheel1_r10_r1 = new ModelRenderer(this);
        rightwheel1_r10_r1.setRotationPoint(-1.0F, -7.0F, 4.0F);
        bb_main.addChild(rightwheel1_r10_r1);
        setRotationAngle(rightwheel1_r10_r1, 0.0F, -1.5708F, -0.7854F);
        rightwheel1_r10_r1.setTextureOffset(72, 114).addCuboid(-1.0F, -4.0F, -1.6569F, 2.0F, 8.0F, 3.0F, 0.0F, false);
        rightwheel1_r10_r1.setTextureOffset(0, 97).addCuboid(-1.0F, -1.6569F, -4.0F, 2.0F, 3.0F, 8.0F, 0.0F, false);
    }

    private final ModelRenderer Axles;
    private final ModelRenderer Axle2;
    private final ModelRenderer Axle1;
    private final ModelRenderer WalkingBeam;
    private final ModelRenderer bone3_r1;
    private final ModelRenderer bone3_r2;
    private final ModelRenderer bone3_r3;
    private final ModelRenderer bone3_r4;
    private final ModelRenderer Wheels;
    private final ModelRenderer RightWheel2;
    private final ModelRenderer octagon_r1;
    private final ModelRenderer octagon_r2;
    private final ModelRenderer octagon_r3;
    private final ModelRenderer octagon_r4;
    private final ModelRenderer rightwheel2_r1;
    private final ModelRenderer rightwheel2_r2;
    private final ModelRenderer rightwheel2_r3;
    private final ModelRenderer rightwheel2_r4;
    private final ModelRenderer rightwheel2_r5;
    private final ModelRenderer rightwheel2_r6;
    private final ModelRenderer rightwheel2_r7;
    private final ModelRenderer RightWheel1;
    private final ModelRenderer octagon_r5;
    private final ModelRenderer octagon_r6;
    private final ModelRenderer octagon_r7;
    private final ModelRenderer octagon_r8;
    private final ModelRenderer rightwheel1_r5;
    private final ModelRenderer rightwheel1_r6;
    private final ModelRenderer rightwheel1_r7;
    private final ModelRenderer LeftWheel1;
    private final ModelRenderer octagon_r9;
    private final ModelRenderer octagon_r10;
    private final ModelRenderer octagon_r11;
    private final ModelRenderer octagon_r12;
    private final ModelRenderer leftwheel1_r1;
    private final ModelRenderer leftwheel1_r2;
    private final ModelRenderer leftwheel1_r3;
    private final ModelRenderer leftwheel1_r4;
    private final ModelRenderer leftwheel1_r5;
    private final ModelRenderer leftwheel1_r6;
    private final ModelRenderer leftwheel1_r7;
    private final ModelRenderer LeftWheel2;
    private final ModelRenderer octagon_r13;
    private final ModelRenderer octagon_r14;
    private final ModelRenderer octagon_r15;
    private final ModelRenderer octagon_r16;
    private final ModelRenderer leftwheel2_r1;
    private final ModelRenderer leftwheel2_r2;
    private final ModelRenderer leftwheel2_r3;
    private final ModelRenderer leftwheel2_r4;
    private final ModelRenderer leftwheel2_r5;
    private final ModelRenderer leftwheel2_r6;
    private final ModelRenderer leftwheel2_r7;
    private final ModelRenderer Body;
    private final ModelRenderer wheel_beam2_r1;
    private final ModelRenderer wheel_beam2_r2;
    private final ModelRenderer wheel_beam2_r3;
    private final ModelRenderer wheel_beam2_r4;
    private final ModelRenderer wheel_beam2_r5;
    private final ModelRenderer wheel_beam2_r6;
    private final ModelRenderer wheel_beam2_r7;
    private final ModelRenderer bb_main;
    private final ModelRenderer rightwheel1_r11_r1;
    private final ModelRenderer rightwheel1_r10_r1;

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
