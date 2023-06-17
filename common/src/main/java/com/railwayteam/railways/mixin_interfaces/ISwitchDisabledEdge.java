package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.graph.TrackEdge;

public interface ISwitchDisabledEdge {
    void setEnabled(boolean enabled);
    boolean isEnabled();

    static boolean isEnabled(TrackEdge edge) {
        return ((ISwitchDisabledEdge) edge.getEdgeData()).isEnabled();
    }

    static boolean isDisabled(TrackEdge edge) {
        return !isEnabled(edge);
    }
}
