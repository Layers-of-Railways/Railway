package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ClearCasingCacheCommand {
  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("clear_casing_cache")
        .requires(cs -> cs.hasPermission(0))
        .executes(ctx -> {
          DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CasingRenderUtils::clearModelCache);

          ctx.getSource()
              .sendSuccess(Components.literal("cleared casing cache"), true);
          return 1;
        });
  }
}
