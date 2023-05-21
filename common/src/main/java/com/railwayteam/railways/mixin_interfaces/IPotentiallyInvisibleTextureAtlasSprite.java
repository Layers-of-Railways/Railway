package com.railwayteam.railways.mixin_interfaces;

public interface IPotentiallyInvisibleTextureAtlasSprite {
    void uploadFrame(boolean visible);

    boolean shouldDoInvisibility();
    boolean isVisible();
}
