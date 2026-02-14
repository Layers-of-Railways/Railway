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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.railwayteam.railways.content.palettes.boiler.BoilerBlock.HORIZONTAL_AXIS;
import static com.railwayteam.railways.content.palettes.smokebox.PalettesSmokeboxBlock.FACING;
import static com.railwayteam.railways.util.BlockStateUtils.blockWithProperties;
import static net.minecraft.world.level.block.RotatedPillarBlock.AXIS;

public class PalettesDemoCommand {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("palettes_demo")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(ctx -> {
                    final BlockPos origin0 = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                    BlockPos origin = origin0;
                    ServerLevel level = ctx.getSource().getLevel();
                    StructureTemplate template = level.getStructureManager().get(Railways.asResource("palettes_showcase")).get();
                    final BoundingBox bounds = template.getBoundingBox(BlockPos.ZERO, Rotation.NONE, BlockPos.ZERO, Mirror.NONE);

                    try {
                        for (PalettesColor palettesColor : PalettesColor.values()) {
                            if (palettesColor.ordinal() > 0)
                                origin = origin.offset(bounds.getXSpan() + 2, 0, 0);
                            template.placeInWorld(
                                level,
                                origin,
                                origin,
                                new StructurePlaceSettings().addProcessor(new LocometalSubstituteProcessor(palettesColor)),
                                level.random,
                                Block.UPDATE_CLIENTS
                            );

                            Block associatedBlock = palettesColor.getAssociatedBlock();
                            if (associatedBlock != null) {
                                BlockState state = associatedBlock.defaultBlockState();
                                if (state.hasProperty(HORIZONTAL_AXIS)) {
                                    state = state.setValue(HORIZONTAL_AXIS, Axis.Z);
                                } else if (state.hasProperty(AXIS)) {
                                    state = state.setValue(AXIS, Axis.Y);
                                } else if (state.hasProperty(FACING)) {
                                    state = state.setValue(FACING, Direction.UP);
                                }
                                for (int i = 0; i <= 3; i++) {
                                    for (int j = 0; j <= 4; j++) {
                                        level.setBlockAndUpdate(origin.offset(i, 0, j), state);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Railways.LOGGER.error("Failed to place palettes blocks", e);
                        throw e;
                    }

                    final BlockPos finalCorner = origin.offset(bounds.getXSpan() - 1, bounds.getYSpan() - 1, bounds.getZSpan() - 1);

                    ctx.getSource().sendSuccess(() -> Components.literal("Placed palettes blocks. Click ")
                        .append(Components.literal("[here]").withStyle(Style.EMPTY
                            .withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/fill " +
                                    origin0.getX() + " " + origin0.getY() + " " + origin0.getZ() + " " +
                                    finalCorner.getX() + " " + finalCorner.getY() + " " + finalCorner.getZ() + " air")
                            )
                            .withBold(true)
                        ))
                        .append(Components.literal(" to clear.")), true);
                    return 1;
                }));
    }

    private static class LocometalSubstituteProcessor extends StructureProcessor {
        private final PalettesColor color;

        public LocometalSubstituteProcessor(PalettesColor color) {
            this.color = color;
        }

        @Override
        public @Nullable StructureBlockInfo processBlock(
            @NotNull LevelReader level,
            @NotNull BlockPos blockPos,
            @NotNull BlockPos pos,
            @NotNull StructureBlockInfo blockInfo,
            @NotNull StructureBlockInfo relativeBlockInfo,
            @NotNull StructurePlaceSettings settings
        ) {
            StructureBlockInfo superInfo = super.processBlock(level, blockPos, pos, blockInfo, relativeBlockInfo, settings);
            if (superInfo == null) return null;

            Pair<Styles, PalettesColor> styleInfo = CRPalettes.getStyleForBlock(superInfo.state.getBlock());
            if (styleInfo != null) {
                return new StructureBlockInfo(
                    superInfo.pos(),
                    blockWithProperties(styleInfo.getFirst().get(color).get(), superInfo.state),
                    superInfo.nbt()
                );
            }
            return superInfo;
        }

        @Override
        protected @NotNull StructureProcessorType<?> getType() {
            return StructureProcessorType.NOP; // not actually a NOP, but this won't ever be serialized
        }
    }
}
