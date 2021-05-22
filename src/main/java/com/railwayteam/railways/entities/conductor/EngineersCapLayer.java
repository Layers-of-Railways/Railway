package com.railwayteam.railways.entities.conductor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

public class EngineersCapLayer extends LayerRenderer<ConductorEntity, ConductorEntityModel> {
    public EngineersCapLayer(IEntityRenderer<ConductorEntity, ConductorEntityModel> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, ConductorEntity entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        renderModel(matrixStack, renderTypeBuffer, packedLight, getEntityModel(), entity);
    }

    public static ResourceLocation getCapColor(String color) {
        return new ResourceLocation("railways", "textures/models/armor/" + color + "_golem_hat.png");
    }

    public static ResourceLocation getCapColor(DyeColor color) {
        return getCapColor(color.getTranslationKey());
    }

    public static ResourceLocation getCapColor(ConductorEntity entity) {
        return getCapColor(entity.getColor());
    }

    public void renderModel(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, EntityModel<ConductorEntity> entityModel, float red, float green, float blue, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorGlintConsumer(renderTypeBuffer, RenderType.getArmorCutoutNoCull(armorResource), false, false);
        entityModel.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }

    public void renderModel(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, EntityModel<ConductorEntity> entityModel, ConductorEntity entity) {
        renderModel(matrixStack, renderTypeBuffer, packedLight, entityModel, 1, 1, 1, getCapColor(entity));
    }
}
