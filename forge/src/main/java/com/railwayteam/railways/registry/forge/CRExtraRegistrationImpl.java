package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.Create;
import net.minecraft.core.registries.Registries;

public class CRExtraRegistrationImpl {
    public static void platformSpecificRegistration() {
        Create.REGISTRATE.addRegisterCallback("copycat", Registries.BLOCK_ENTITY_TYPE, CRExtraRegistration::addVentAsCopycat);
    }
}
