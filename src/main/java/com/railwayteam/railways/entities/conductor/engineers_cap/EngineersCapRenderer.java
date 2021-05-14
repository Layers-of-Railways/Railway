package com.railwayteam.railways.entities.conductor.engineers_cap;

import com.railwayteam.railways.items.EngineersCapItem;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class EngineersCapRenderer extends GeoArmorRenderer<EngineersCapItem> {
    public EngineersCapRenderer(AnimatedGeoModel modelProvider) {
        super(modelProvider);

//        this.headBone = "bipedHead";
//        this.bodyBone = "bipedBody";
//        this.rightArmBone = "bipedRightArm";
//        this.leftArmBone = "bipedLeftArm";
//        this.rightLegBone = "bipedRightLeg";
//        this.leftLegBone = "bipedLeftLeg";
//        this.rightBootBone = "armorRightBoot";
//        this.leftBootBone = "armorLeftBoot";
    }

    public EngineersCapRenderer() {
        this(new EngineersCapModel());
    }

    //    private final ModelRenderer Hat;
//    private final ModelRenderer Hat_r1;
//
//    public static void addTo(ModelRenderer hat) {
//        hat.setRotationPoint(0.0F, 6.0F, 0.0F);
//        hat.setTextureOffset(39, 33).addCuboid(-4.0F, -15.0F, 4.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
//        hat.setTextureOffset(34, 48).addCuboid(-5.0F, -15.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
//        hat.setTextureOffset(34, 36).addCuboid(4.0F, -15.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
//        hat.setTextureOffset(39, 30).addCuboid(-4.0F, -15.0F, -5.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
//        hat.setTextureOffset(32, 22).addCuboid(-4.0F, -15.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);
//    }
//
//    public static ModelRenderer addTo(Model m) {
//        ModelRenderer r = new ModelRenderer(m);
//        addTo(r);
//        return r;
//    }
//
//    public EngineersCapModel() {
//        super(0, 0, 0, 0);
//        textureWidth = 64;
//        textureHeight = 64;
//
//        Hat = addTo(this);
//
//        Hat_r1 = new ModelRenderer(this);
//        Hat_r1.setRotationPoint(8.0F, -12.4362F, -5.904F);
//        Hat.addChild(Hat_r1);
//        setRotationAngle(Hat_r1, 0.3927F, 0.0F, 0.0F);
//        Hat_r1.setTextureOffset(12, 46).addCuboid(-13.0F, -0.0978F, -1.5612F, 10.0F, 1.0F, 3.0F, 0.0F, false);
//        bipedHead.addChild(Hat);
//    }
//
//    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//        modelRenderer.rotateAngleX = x;
//        modelRenderer.rotateAngleY = y;
//        modelRenderer.rotateAngleZ = z;
//    }
}
