package com.railwayteam.railways.mixin_interfaces;

public interface IPotentiallyInvisibleSpriteContents {
    void railways$uploadFrame(boolean visible);

    boolean railways$shouldDoInvisibility();
    boolean railways$isVisible();
}
