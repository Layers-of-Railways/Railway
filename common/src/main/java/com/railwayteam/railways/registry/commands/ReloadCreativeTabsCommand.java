
package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.mixin.AccessorCreativeModeTabs;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ReloadCreativeTabsCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reload_creative_tabs")
            .requires(cs -> cs.hasPermission(2))
            .executes(ctx -> {
                AccessorCreativeModeTabs.setCACHED_PARAMETERS(null);
                ctx.getSource().sendSuccess(() -> Components.literal("Reloaded Creative Tabs"), true);
                return 1;
            });
    }
}
