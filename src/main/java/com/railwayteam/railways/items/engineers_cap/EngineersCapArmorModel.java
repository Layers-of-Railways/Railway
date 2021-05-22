package com.railwayteam.railways.items.engineers_cap;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

class EngineersCapArmorModel extends BipedModel<LivingEntity> {
    private final ModelRenderer Hat;
    private final ModelRenderer Hat_r1;

    public EngineersCapArmorModel() {
        super(0,0,0,0);
        textureWidth = 64;
        textureHeight = 64;

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

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}