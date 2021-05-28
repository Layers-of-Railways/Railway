package com.railwayteam.railways.entities.conductor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.items.engineers_cap.EngineersCapItem;
import com.railwayteam.railways.items.engineers_cap.EngineersCapModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SheepWoolModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

public class EngineersCapLayer extends LayerRenderer<ConductorEntity, ConductorEntityModel> {
    private final EngineersCapModel1 capModel = new EngineersCapModel1(true);

    public EngineersCapLayer(IEntityRenderer<ConductorEntity, ConductorEntityModel> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, ConductorEntity entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
//        renderModel(matrixStack, renderTypeBuffer, packedLight, getEntityModel(), entity);
        capModel.setLivingAnimations(entity, p_225628_5_, p_225628_6_, p_225628_7_);
        capModel.setAngles(entity, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);

        IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.getEntityCutoutNoCull(getCapTexture(entity)));
        capModel.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
//        render(this.getEntityModel(), getEntityModel(), getCapTexture(entity), matrixStack, renderTypeBuffer, packedLight, entity, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
    }

    public static ResourceLocation getCapTexture(String color) {
        return new ResourceLocation("railways", "textures/models/armor/" + color + "_golem_hat.png");
    }

    public static ResourceLocation getCapTexture(DyeColor color) {
        return getCapTexture(color.getTranslationKey());
    }

    public static ResourceLocation getCapTexture(ConductorEntity entity) {
        return getCapTexture(entity.getColor());
    }

    public void renderModel(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, EntityModel<ConductorEntity> entityModel, float red, float green, float blue, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorGlintConsumer(renderTypeBuffer, RenderType.getArmorCutoutNoCull(armorResource), false, false);
        entityModel.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }

    public void renderModel(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, EntityModel<ConductorEntity> entityModel, ConductorEntity entity) {
        renderModel(matrixStack, renderTypeBuffer, packedLight, entityModel, 1, 1, 1, getCapTexture(entity));
    }
}

class EngineersCapModel1 extends BipedModel<LivingEntity> { // TODO: remove 1 from the name when the armor model stops using geckolib
    private final ModelRenderer Hat;
    private final ModelRenderer Hat_r1;
    public boolean isOnConductor;

    @Override
    public void setAngles(LivingEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float headYaw, float headPitch) {
        if(isOnConductor) {
//            Hat.rotationPointY = bipedHead.rotationPointY;
            Hat.rotationPointY = 16;
//            System.out.println(Hat.rotationPointY);

            Hat.rotateAngleX = headPitch * ((float)Math.PI / 180F);
            Hat.rotateAngleY = headYaw * ((float)Math.PI / 180F);
        }

        super.setAngles(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, headYaw, headPitch);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        if(isOnConductor) {
            Hat.render(matrixStack, buffer, packedLight, packedOverlay);
        }

        super.render(matrixStack, buffer, packedLight, packedOverlay, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }

    public EngineersCapModel1(boolean isOnConductor) {
        super(0,0,0,0);
        this.isOnConductor = isOnConductor;
        textureWidth = 64;
        textureHeight = 64;

        if(isOnConductor) {
            Hat = new ModelRenderer(this);
            Hat.setRotationPoint(0.0F, 3.0F, 0.0F);
            Hat.setTextureOffset(39, 33).addCuboid(-4.0F, -6.0F, 4.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
            Hat.setTextureOffset(34, 48).addCuboid(-5.0F, -6.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
            Hat.setTextureOffset(34, 36).addCuboid(4.0F, -6.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
            Hat.setTextureOffset(39, 30).addCuboid(-4.0F, -6.0F, -5.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
            Hat.setTextureOffset(32, 22).addCuboid(-4.0F, -6.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);

            Hat_r1 = new ModelRenderer(this);
            Hat_r1.setRotationPoint(8.0F, -3.4362F, -5.904F);
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

    public EngineersCapModel1() {
        this(false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
