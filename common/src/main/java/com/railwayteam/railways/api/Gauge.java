package com.railwayteam.railways.api;

import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;

/**
 * Specifies what gauge a Bogey is for, Narrow and Wide are only used when Steam 'n' Rails is installed.
 */
public enum Gauge {
    NARROW(Railways.MODID), STANDARD, WIDE(Railways.MODID);

    private final String fromMod;

    Gauge() {
        this.fromMod = Create.ID;
    }

    Gauge(String fromMod) {
        this.fromMod = fromMod;
    }

    public String getFromMod() {
        return fromMod;
    }
}
