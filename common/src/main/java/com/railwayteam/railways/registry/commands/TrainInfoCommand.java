package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;

public class TrainInfoCommand {
  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("train_uuid")
      .requires(cs -> cs.hasPermission(2))
      .then(Commands.argument("name", StringArgumentType.string())
        .executes(ctx -> {
          String trainName = StringArgumentType.getString(ctx, "name");
//          Env.CLIENT.runIfCurrent(() -> CasingRenderUtils::clearModelCache);
          long count = Create.RAILWAYS.trains.values().stream().filter(t -> t.name.getString().equals(trainName)).count();
          Train train = Create.RAILWAYS.trains.values().stream().filter(t -> t.name.getString().equals(trainName)).findFirst().orElse(null);
          if (train == null) {
            ctx.getSource().sendFailure(Components.literal("No Train with name " + trainName + " was found"));
            return 0;
          }

          ctx.getSource().sendSuccess(() -> Components.literal("Train '").append(train.name)
            .append("' has UUID: "+train.id+", "+count+", total trains found.").append(Components.literal(" [Copy]").withStyle(
                Style.EMPTY
                    .withColor(ChatFormatting.GOLD)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, train.id.toString()))
              )), true);
          return 1;
        }));
  }
}
