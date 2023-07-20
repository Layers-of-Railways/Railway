package com.railwayteam.railways.mixin_interfaces;

public interface IPotentiallyInvisibleSpriteContents {
    void snr$uploadFrame(boolean visible);

    boolean snr$shouldDoInvisibility();
    boolean snr$isVisible();
}
