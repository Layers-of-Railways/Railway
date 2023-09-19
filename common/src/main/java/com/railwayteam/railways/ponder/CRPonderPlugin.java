package com.railwayteam.railways.ponder;

import com.railwayteam.railways.Railways;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CRPonderPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModId() {
        return Railways.MODID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CRPonderIndex.register(helper);
    }
}
