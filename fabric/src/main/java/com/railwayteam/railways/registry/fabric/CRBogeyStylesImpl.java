package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.api.bogeymenu.v0.BogeyMenuEvents;

public class CRBogeyStylesImpl {
    public static void fireReadyForRegistrationEvent() {
        BogeyMenuEvents.ENTRY_REGISTRATION.invoker().onReadyForRegistration();
    }
}
