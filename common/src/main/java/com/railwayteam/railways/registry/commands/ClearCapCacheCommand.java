package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.content.conductor.ConductorCapModel;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.SharedSuggestionProvider;

import static com.railwayteam.railways.multiloader.ClientCommands.literal;
import static com.railwayteam.railways.multiloader.ClientCommands.sendSuccess;

public class ClearCapCacheCommand {
  public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
    return literal("clear_cap_cache")
        .requires(cs -> cs.hasPermission(0))
        .executes(ctx -> {
          Env.CLIENT.runIfCurrent(() -> ConductorCapModel::clearModelCache);

          sendSuccess(ctx.getSource(), Components.literal("cleared cap cache"));
          return 1;
        });
  }
}
