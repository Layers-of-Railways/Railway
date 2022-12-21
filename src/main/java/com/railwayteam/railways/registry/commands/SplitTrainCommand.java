package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
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
            .then(Commands.argument("train", UuidArgument.uuid())
                .executes(ctx -> {
                    UUID trainId = UuidArgument.getUuid(ctx, "train");
//          DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CasingRenderUtils::clearModelCache);
                    Train train = Create.RAILWAYS.trains.get(trainId);
                    if (train == null) {
                        ctx.getSource().sendFailure(Components.literal("No Train with id " + trainId.toString()
                            .substring(0, 5) + "[...] was found"));
                        return 0;
                    }
                    try {
                        TrainUtils.splitTrain(train);
                    } catch (Exception e) {
                        ctx.getSource().sendFailure(Components.literal("Failed to split train: " + e.getMessage()));
                        e.printStackTrace();
                        return 0;
                    }
                    ctx.getSource().sendSuccess(Components.literal("Train '").append(train.name)
                        .append("' split successfully"), true);
                    return 1;
                }));
    }
}
