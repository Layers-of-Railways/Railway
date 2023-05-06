package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.coupling.coupler.TrackCoupler;
import com.railwayteam.railways.content.switches.TrackSwitch;
import com.simibubi.create.content.trains.graph.EdgePointType;

public class CREdgePointTypes {
    public static final EdgePointType<TrackCoupler> COUPLER = EdgePointType.register(Railways.asResource("coupler"), TrackCoupler::new);
    public static final EdgePointType<TrackSwitch> SWITCH = EdgePointType.register(Railways.asResource("switch"), TrackSwitch::new);

    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
