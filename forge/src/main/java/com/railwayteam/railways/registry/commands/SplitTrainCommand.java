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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class SplitTrainCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("split_train")
          .requires(cs -> cs.hasPermission(2))
          .then(Commands.argument("train_id", UuidArgument.uuid())
              .executes(ctx -> {
                  UUID trainId = UuidArgument.getUuid(ctx, "train_id");
//          Env.CLIENT.runIfCurrent(() -> CasingRenderUtils::clearModelCache);
                  return execute(ctx, trainId, 1);
              })
              .then(Commands.argument("number", IntegerArgumentType.integer(1))
                .executes(ctx -> {
                    UUID trainId = UuidArgument.getUuid(ctx, "train_id");
                    int number = IntegerArgumentType.getInteger(ctx, "number");
                    return execute(ctx, trainId, number);
                })
              )
          )
          .then(Commands.argument("train_name", StringArgumentType.string())
              .executes(ctx -> {
                  String name = StringArgumentType.getString(ctx, "train_name");
                  UUID trainId = Create.RAILWAYS.trains.values().stream().filter(t -> t.name.getString().equals(name)).findFirst().map(t -> t.id).orElse(null);
//          Env.CLIENT.runIfCurrent(() -> CasingRenderUtils::clearModelCache);
                  return execute(ctx, trainId, 1);
              })
              .then(Commands.argument("number", IntegerArgumentType.integer(1))
                .executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "train_name");
                    UUID trainId = Create.RAILWAYS.trains.values().stream().filter(t -> t.name.getString().equals(name)).findFirst().map(t -> t.id).orElse(null);
                    int number = IntegerArgumentType.getInteger(ctx, "number");
                    return execute(ctx, trainId, number);
                })
              )
          );
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, UUID trainId, int numberOffEnd) {
        if (trainId == null) {
            ctx.getSource().sendFailure(Component.literal("No trains were found."));
            return 0;
        }
        Train train = Create.RAILWAYS.trains.get(trainId);
        if (train == null) {
            ctx.getSource().sendFailure(Component.literal("No Train with id " + trainId.toString()
              .substring(0, 5) + "[...] was found"));
            return 0;
        }
        try {
            TrainUtils.splitTrain(train, numberOffEnd);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Failed to split train: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Train '").append(train.name)
          .append("' split successfully"), true);
        return 1;
    }
}
