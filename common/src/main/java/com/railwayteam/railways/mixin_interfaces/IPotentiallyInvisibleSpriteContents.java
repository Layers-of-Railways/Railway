package com.railwayteam.railways.mixin_interfaces;

public interface IPotentiallyInvisibleSpriteContents {
    void uploadFrame(boolean visible);

    boolean shouldDoInvisibility();
    boolean isVisible();
}
