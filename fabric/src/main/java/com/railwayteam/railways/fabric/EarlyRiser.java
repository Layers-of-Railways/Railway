package com.railwayteam.railways.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import com.railwayteam.railways.registry.CRIcons;
import com.simibubi.create.foundation.gui.AllIcons;

import java.util.function.Supplier;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        ClassTinkerers.enumBuilder("com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity$RollingMode", AllIcons.class)
                .addEnum("TRACK_REPLACE", () -> { // wrap up safely to prevent premature classloading
                    Supplier<Supplier<Object[]>> supplier = (() -> () -> new Object[] {CRIcons.I_SWAP_TRACKS});
                    return supplier.get().get();
                }).build();
    }
}
