package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ReloadCasingCollisionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reload_casing_collision")
            .requires(cs -> cs.hasPermission(2))
            .executes(ctx -> {
                CasingCollisionUtils.register();
                ctx.getSource().sendSuccess(() -> Components.literal("Reloaded Casing Collisions"), true);
                return 1;
            });
    }
}
