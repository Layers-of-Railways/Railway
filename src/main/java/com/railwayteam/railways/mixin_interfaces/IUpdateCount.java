package com.railwayteam.railways.mixin_interfaces;

public interface IUpdateCount {
    int snr_getUpdateCount();
    void snr_fromParent(IUpdateCount parent);
    void snr_markUpdate();

    static boolean outOfSync(IUpdateCount a, IUpdateCount b) {
        return a.snr_getUpdateCount() != b.snr_getUpdateCount();
    }
}
