package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.Create;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Registry;

public class CRExtraRegistrationImpl {
    public static void platformSpecificRegistration() {
        RegistryEntryAddedCallback.event(Registry.BLOCK_ENTITY_TYPE).register((rawId, Id, blockEntityType) ->  {
            if (Id.equals(Create.asResource("copycat")))
                CRExtraRegistration.addVentAsCopycat(blockEntityType);
        });
    }
}
