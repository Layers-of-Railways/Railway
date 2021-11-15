package com.railwayteam.railways.content.entities.conductor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.railwayteam.railways.content.items.engineers_cap.EngineersCapModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

public class EngineersCapLayer extends LayerRenderer<ConductorEntity, ConductorEntityModel> {
    private final EngineersCapModel capModel = new EngineersCapModel(true);

    public EngineersCapLayer(IEntityRenderer<ConductorEntity, ConductorEntityModel> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, ConductorEntity entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
//        renderModel(matrixStack, renderTypeBuffer, packedLight, getEntityModel(), entity);
        capModel.setLivingAnimations(entity, p_225628_5_, p_225628_6_, p_225628_7_);
        capModel.setAngles(entity, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);

        IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.getEntityCutoutNoCull
                (getCapTextureRainbowSupport(entity)));
        float r = 1;
        float g = 1;
        float b = 1;
        if (entity.shouldBeRainbow()) {
            int i = entity.ticksExisted / 25 + entity.getEntityId();
            int j = DyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f3 = ((float)(entity.ticksExisted % 25) + p_225628_7_) / 25.0F;
            float[] afloat1 = SheepEntity.getDyeRgb(DyeColor.byId(k));
            float[] afloat2 = SheepEntity.getDyeRgb(DyeColor.byId(l));
            r = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
            g = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
            b = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
        }
        //I'm trying, ok?
        //capModel.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.DEFAULT_UV, r, g, b, 1.0F);
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

    public static ResourceLocation getCapTextureRainbowSupport(ConductorEntity entity) {
        return entity.shouldBeRainbow() ? getCapTexture(DyeColor.WHITE) : getCapTexture(entity);
    }
}

