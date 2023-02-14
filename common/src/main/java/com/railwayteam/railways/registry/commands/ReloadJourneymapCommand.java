package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.SharedSuggestionProvider;

import static com.railwayteam.railways.multiloader.ClientCommands.*;

public class ReloadJourneymapCommand {
    public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
        return literal("reload_jmap")
            .requires(cs -> cs.hasPermission(0))
            .executes(ctx -> {
                SharedSuggestionProvider source = ctx.getSource();
                if (Mods.JOURNEYMAP.isLoaded()) {
                    Env.CLIENT.runIfCurrent(() -> () -> DummyRailwayMarkerHandler.getInstance().reloadMarkers());

                    sendSuccess(source, Components.literal("Reloaded journeymap"));
                    return 1;
                } else {
                    sendFailure(source, Components.literal("Journeymap not loaded"));
                    return 0;
                }
            });
    }
}
