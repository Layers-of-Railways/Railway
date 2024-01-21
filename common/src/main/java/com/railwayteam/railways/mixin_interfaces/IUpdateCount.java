package com.railwayteam.railways.mixin_interfaces;

public interface IUpdateCount {
    int railways$getUpdateCount();
    void railways$fromParent(IUpdateCount parent);
    void railways$markUpdate();

    static boolean outOfSync(IUpdateCount a, IUpdateCount b) {
        return a.railways$getUpdateCount() != b.railways$getUpdateCount();
    }
}
