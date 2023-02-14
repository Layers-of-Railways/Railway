package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;

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
            ctx.getSource().sendFailure(Components.literal("No trains were found."));
            return 0;
        }
        Train train = Create.RAILWAYS.trains.get(trainId);
        if (train == null) {
            ctx.getSource().sendFailure(Components.literal("No Train with id " + trainId.toString()
              .substring(0, 5) + "[...] was found"));
            return 0;
        }
        try {
            TrainUtils.splitTrain(train, numberOffEnd);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Components.literal("Failed to split train: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
        ctx.getSource().sendSuccess(Components.literal("Train '").append(train.name)
          .append("' split successfully"), true);
        return 1;
    }
}
