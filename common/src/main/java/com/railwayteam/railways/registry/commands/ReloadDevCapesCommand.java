package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.multiloader.Env;
import com.railwayteam.railways.util.DevCapeUtils;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.SharedSuggestionProvider;

import static com.railwayteam.railways.multiloader.ClientCommands.literal;
import static com.railwayteam.railways.multiloader.ClientCommands.sendSuccess;

public class ReloadDevCapesCommand {
  public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
    return literal("reload_dev_capes")
        .requires(cs -> cs.hasPermission(0))
        .executes(ctx -> {
          Env.CLIENT.runIfCurrent(() -> DevCapeUtils.INSTANCE::refresh);

          sendSuccess(ctx.getSource(), Components.literal("Refreshed dev capes"));
          return 1;
        });
  }
}
