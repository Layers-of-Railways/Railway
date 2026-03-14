/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.RepaintingTarget;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static com.railwayteam.railways.multiloader.PlatformAbstractionHelper.enumArgument;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CountPaintCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(
        (max, specified) -> Component.translatable("commands.fill.toobig", max, specified)
    );

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return literal("count_paint")
            .requires(cs -> cs.hasPermission(2))
            .then(argument("from", BlockPosArgument.blockPos())
                .then(argument("to", BlockPosArgument.blockPos())
                    .then(argument("color", enumArgument(PalettesColor.class))
                        .executes(ctx -> countColor(
                            ctx.getSource(),
                            BoundingBox.fromCorners(
                                BlockPosArgument.getLoadedBlockPos(ctx, "from"),
                                BlockPosArgument.getLoadedBlockPos(ctx, "to")
                            ),
                            ctx.getArgument("color", PalettesColor.class),
                            null
                        ))
                        .then(literal("where")
                            .then(argument("block_filter", BlockPredicateArgument.blockPredicate(context))
                                .executes(ctx -> countColor(
                                    ctx.getSource(),
                                    BoundingBox.fromCorners(
                                        BlockPosArgument.getLoadedBlockPos(ctx, "from"),
                                        BlockPosArgument.getLoadedBlockPos(ctx, "to")
                                    ),
                                    ctx.getArgument("color", PalettesColor.class),
                                    BlockPredicateArgument.getBlockPredicate(ctx, "block_filter")
                                ))
                            )
                        )
                    )));
    }

    private static int countColor(
        CommandSourceStack source,
        BoundingBox area,
        PalettesColor color,
        @Nullable Predicate<BlockInWorld> targetPredicate
    ) throws CommandSyntaxException {
        int targetedVolume = area.getXSpan() * area.getYSpan() * area.getZSpan();
        int max = source.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        if (targetedVolume > max) {
            throw ERROR_AREA_TOO_LARGE.create(max, targetedVolume);
        }

        ServerLevel level = source.getLevel();
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ())) {
            BlockInWorld blockInWorld = new BlockInWorld(level, pos, true);
            if (targetPredicate != null && !targetPredicate.test(blockInWorld))
                continue;

            RepaintingTarget target = RepaintingTarget.get(level, pos, blockInWorld.getState());
            if (target == null)
                continue;

            if (target.getColor() != color)
                continue;

            count++;
        }

        final int finalCount = count;
        source.sendSuccess(() -> Component.translatable("railways.commands.count_paint.success", finalCount), true);
        return count;
    }
}
