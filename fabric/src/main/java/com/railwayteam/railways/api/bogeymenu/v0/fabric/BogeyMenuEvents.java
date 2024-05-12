package com.railwayteam.railways.api.bogeymenu.v0.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class BogeyMenuEvents {
    public static final Event<EntryRegistrationEvent> ENTRY_REGISTRATION = EventFactory.createArrayBacked(EntryRegistrationEvent.class, listeners -> () -> {});

    @FunctionalInterface
    public interface EntryRegistrationEvent {
        void onReadyForRegistration();
    }
}
