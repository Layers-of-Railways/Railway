package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.Create;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class CRExtraRegistrationImpl {
    private static final ResourceLocation COPYCAT_ID = Create.asResource("copycat");

    public static void platformSpecificRegistration() {
        RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK_ENTITY_TYPE).register((rawId, id, blockEntityType) ->  {
            if (id == COPYCAT_ID)
                CRExtraRegistration.addVentAsCopycat(blockEntityType);
        });
    }
}
