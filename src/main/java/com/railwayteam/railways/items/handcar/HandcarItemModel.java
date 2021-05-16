package com.railwayteam.railways.items.handcar;

import com.railwayteam.railways.entities.handcar.HandcarModel;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HandcarItemModel extends AnimatedGeoModel<HandcarItem> {
    HandcarModel model = new HandcarModel();

    @Override
    public ResourceLocation getModelLocation(HandcarItem handcarItem) {
        return new ResourceLocation("railways", "geo/handcar_item.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(HandcarItem handcarItem) {
        return model.getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(HandcarItem handcarItem) {
        return model.getAnimationFileLocation();
    }
}
