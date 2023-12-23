package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.AllBlockEntityTypes;

public class CRExtraRegistrationImpl {
    public static void platformSpecificRegistration() {
        CRExtraRegistration.addVentAsCopycat(AllBlockEntityTypes.COPYCAT.get());
    }
}
