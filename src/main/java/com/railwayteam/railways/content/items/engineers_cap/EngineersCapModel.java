package com.railwayteam.railways.content.items.engineers_cap;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.content.contraptions.components.actors.SeatEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class EngineersCapModel extends BipedModel<LivingEntity> {
    private final ModelRenderer Hat;
    private final ModelRenderer Hat_r1;
    public boolean isOnConductor;

    @Override
    public void setAngles(LivingEntity entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float headYaw, float headPitch) {
        if (isOnConductor) {
//            Hat.rotationPointY = bipedHead.rotationPointY;
//            System.out.println(Hat.rotationPointY);

            Hat.rotateAngleX = headPitch * ((float) Math.PI / 180F);
            Hat.rotateAngleY = headYaw * ((float) Math.PI / 180F);

            Entity riding = entity.getRidingEntity();

            Hat.rotationPointY = 6.5F;
            Hat.rotationPointZ = 0;

            if(riding instanceof SeatEntity || riding instanceof AbstractContraptionEntity) {
                Hat.rotationPointY += 1;
                Hat.rotationPointZ += 1;
            }
        }

        super.setAngles(entity, p_225597_2_, p_225597_3_, p_225597_4_, headYaw, headPitch);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (isOnConductor) {
            Hat.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        super.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public EngineersCapModel(boolean isOnConductor) {
        super(0, 0, 0, 0);
        this.isOnConductor = isOnConductor;
        textureWidth = 64;
        textureHeight = 64;

        if (isOnConductor) {
            Hat = new ModelRenderer(this);
            Hat.setRotationPoint(0.0F, 6.5F, 0.0F);
            Hat.setTextureOffset(39, 33).addCuboid(-4.0F, -4.5F, 4.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
            Hat.setTextureOffset(34, 48).addCuboid(-5.0F, -4.5F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
            Hat.setTextureOffset(34, 36).addCuboid(4.0F, -4.5F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
            Hat.setTextureOffset(39, 30).addCuboid(-4.0F, -4.5F, -5.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
            Hat.setTextureOffset(32, 22).addCuboid(-4.0F, -4.5F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);

            Hat_r1 = new ModelRenderer(this);
            Hat_r1.setRotationPoint(8.0F, -1.9362F, -5.904F);
            Hat.addChild(Hat_r1);
            setRotationAngle(Hat_r1, 0.3927F, 0.0F, 0.0F);
            Hat_r1.setTextureOffset(12, 46).addCuboid(-13.0F, -0.0978F, -1.5612F, 10.0F, 1.0F, 3.0F, 0.0F, false);
        } else {
            Hat = new ModelRenderer(this);
            Hat.setRotationPoint(0.0F, 6.0F, 0.0F);
            Hat.setTextureOffset(39, 33).addCuboid(-4.0F, -15.0F, 4.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
            Hat.setTextureOffset(34, 48).addCuboid(-5.0F, -15.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
            Hat.setTextureOffset(34, 36).addCuboid(4.0F, -15.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
            Hat.setTextureOffset(39, 30).addCuboid(-4.0F, -15.0F, -5.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
            Hat.setTextureOffset(32, 22).addCuboid(-4.0F, -15.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);

            Hat_r1 = new ModelRenderer(this);
            Hat_r1.setRotationPoint(8.0F, -12.4362F, -5.904F);
            Hat.addChild(Hat_r1);
            setRotationAngle(Hat_r1, 0.3927F, 0.0F, 0.0F);
            Hat_r1.setTextureOffset(12, 46).addCuboid(-13.0F, -0.0978F, -1.5612F, 10.0F, 1.0F, 3.0F, 0.0F, false);
            bipedHead.addChild(Hat);
        }
    }

    public EngineersCapModel() {
        this(false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
