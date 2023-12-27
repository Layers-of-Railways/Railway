package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.Create;
import net.minecraft.core.Registry;

public class CRExtraRegistrationImpl {
    public static void platformSpecificRegistration() {
        Create.REGISTRATE.addRegisterCallback("copycat", Registry.BLOCK_ENTITY_TYPE_REGISTRY, CRExtraRegistration::addVentAsCopycat);
    }
}
