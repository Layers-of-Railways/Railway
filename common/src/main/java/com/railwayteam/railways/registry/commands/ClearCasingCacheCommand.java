package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.SharedSuggestionProvider;

import static com.railwayteam.railways.multiloader.ClientCommands.literal;
import static com.railwayteam.railways.multiloader.ClientCommands.sendSuccess;

public class ClearCasingCacheCommand {
  public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
    return literal("clear_casing_cache")
        .requires(cs -> cs.hasPermission(0))
        .executes(ctx -> {
          Env.CLIENT.runIfCurrent(() -> CasingRenderUtils::clearModelCache);

          sendSuccess(ctx.getSource(), Components.literal("cleared casing cache"));
          return 1;
        });
  }
}
