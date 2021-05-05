package com.railwayteam.railways.entities;

import com.railwayteam.railways.entities.conductor.ConductorEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HandcarModel extends AnimatedGeoModel<HandcarEntity> {
    @Override
    public ResourceLocation getModelLocation(HandcarEntity object)
    {
        return new ResourceLocation("railways", "geo/handcar.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(HandcarEntity object)
    {
        return new ResourceLocation("railways", "textures/entity/handcar.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(HandcarEntity object)
    {
        return new ResourceLocation("railways", "animations/handcar.animation.json");
    }
}
