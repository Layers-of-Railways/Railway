/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
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
            ctx.getSource().sendFailure(Component.literal("No Train with name " + trainName + " was found"));
            return 0;
          }

          ctx.getSource().sendSuccess(() -> Component.literal("Train '").append(train.name)
            .append("' has UUID: "+train.id+", "+count+", total trains found.").append(Component.literal(" [Copy]").withStyle(
                Style.EMPTY
                    .withColor(ChatFormatting.GOLD)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, train.id.toString()))
              )), true);
          return 1;
        }));
  }
}
