package com.railwayteam.railways.entities.conductor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.railwayteam.railways.items.engineers_cap.EngineersCapModel;
import com.railwayteam.railways.items.engineers_cap.EngineersCapItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Iterator;

public class EngineersCapLayer extends GeoLayerRenderer<ConductorEntity> {
    private final EngineersCapModel geoModel = new EngineersCapModel();
    private final IGeoRenderer<ConductorEntity> entityRenderer;

    public EngineersCapLayer(IGeoRenderer<ConductorEntity> entityRendererIn) {
        super(entityRendererIn);
        this.entityRenderer = entityRendererIn;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int i, ConductorEntity entity, float v, float v1, float v2, float v3, float v4, float v5) {
        EngineersCapItem item = (EngineersCapItem) entity.getHatByColor(entity.getColor());
        GeoModel conductorModel = this.entityRenderer.getGeoModelProvider().getModel(this.entityRenderer.getGeoModelProvider().getModelLocation(entity));

//        System.out.println(item.color.getTranslationKey());
        IVertexBuilder ivertexbuilder = ItemRenderer.getItemGlintConsumer(bufferIn, RenderType.getArmorCutoutNoCull(geoModel.getTextureLocation(item)), false, false);

        Iterator group = geoModel.getModel(geoModel.getConductorEntityModel()).topLevelBones.iterator();

        while (group.hasNext()) {
            GeoBone itemBone = (GeoBone) group.next();
            IBone headBone = conductorModel.getBone("Head").get();
            itemBone.setRotationX(headBone.getRotationX());
            itemBone.setRotationY(headBone.getRotationY());
            this.entityRenderer.renderRecursively(itemBone, matrixStack, ivertexbuilder, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
