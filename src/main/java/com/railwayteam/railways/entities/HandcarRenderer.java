package com.railwayteam.railways.entities;

import com.railwayteam.railways.entities.conductor.ConductorEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class HandcarRenderer extends GeoEntityRenderer<HandcarEntity> {
    public HandcarRenderer(EntityRendererManager renderManager, AnimatedGeoModel<HandcarEntity> modelProvider) {
        super(renderManager, modelProvider);
    }

    public HandcarRenderer(EntityRendererManager renderManager) {
        this(renderManager, new HandcarModel());
    }

    @Override
    public ResourceLocation getEntityTexture(HandcarEntity entity) {
        return getGeoModelProvider().getTextureLocation(entity);
    }
}
