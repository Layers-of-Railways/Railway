package com.railwayteam.railways.items.handcar;

import com.railwayteam.railways.entities.handcar.HandcarModel;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

// TODO: make this into a vanilla iter
public class HandcarItemModel extends AnimatedGeoModel<HandcarItem> {
    HandcarModel model = new HandcarModel();

    @Override
    public ResourceLocation getModelLocation(HandcarItem handcarItem) {
        return new ResourceLocation("railways", "geo/handcar_item.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(HandcarItem handcarItem) {
        return new ResourceLocation("railways", "textures/entity/handcar.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(HandcarItem handcarItem) {
        return new ResourceLocation("railways", "animations/handcar.animation.json");
    }
}
