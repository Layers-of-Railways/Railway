package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.fml.DistExecutor;

public class ReloadJourneymapCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reload_jmap")
            .requires(cs -> cs.hasPermission(0))
            .executes(ctx -> {
                if (Mods.JOURNEYMAP.isLoaded()) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DummyRailwayMarkerHandler.getInstance().reloadMarkers());

                    ctx.getSource()
                        .sendSuccess(Components.literal("Reloaded journeymap"), true);
                    return 1;
                } else {
                    ctx.getSource()
                        .sendFailure(Components.literal("Journeymap not loaded"));
                    return 0;
                }
            });
    }
}
