package com.railwayteam.railways.items.handcar;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class HandcarItemRenderer extends GeoItemRenderer<HandcarItem>  {
    public HandcarItemRenderer() {
        super(new HandcarItemModel());
    }
}
