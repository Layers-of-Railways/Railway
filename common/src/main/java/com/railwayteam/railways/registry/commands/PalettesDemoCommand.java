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
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.railwayteam.railways.content.palettes.boiler.BoilerBlock.HORIZONTAL_AXIS;
import static net.minecraft.world.level.block.RotatedPillarBlock.AXIS;

public class PalettesDemoCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("palettes_demo")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(ctx -> {
                    BlockPos origin = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                    PatternBuilder pattern = create();

                    pattern.place(ctx.getSource().getLevel(), origin, null);
                    for (DyeColor dyeColor : DyeColor.values()) {
                        origin = origin.offset(pattern.maxWidth + 2, 0, 0);
                        pattern.place(ctx.getSource().getLevel(), origin, dyeColor);
                    }

                    ctx.getSource().sendSuccess(() -> Component.literal("Placed palettes blocks"), true);
                    return 1;
                }));
    }

    private static PatternBuilder create() {
        return new PatternBuilder()
            .next(Styles.SLASHED).next(Styles.SLASHED).next(Styles.FLAT_SLASHED).next(Styles.SMOKEBOX).transform(AXIS, Axis.Y)
            .nextRow()
            .next(Styles.RIVETED).next(Styles.RIVETED).next(Styles.FLAT_RIVETED).next(Styles.SMOKEBOX).transform(AXIS, Axis.Z)
            .nextRow()
            .next(Styles.PILLAR).transform(AXIS, Axis.Y).next(Styles.PILLAR).transform(AXIS, Axis.Z).next(Styles.PLATED).next(Styles.BRASS_WRAPPED_SLASHED)
            .nextRow()
            .next(Styles.IRON_WRAPPED_SLASHED).next(Styles.COPPER_WRAPPED_SLASHED).next(Styles.COPPER_WRAPPED_SLASHED).next(Styles.BRASS_WRAPPED_SLASHED)
            .nextRow()
            .nextRow()
            .next(Styles.BOILER).transform(HORIZONTAL_AXIS, Axis.X)
            .next(Styles.BRASS_WRAPPED_BOILER).transform(HORIZONTAL_AXIS, Axis.X)
            .next(Styles.COPPER_WRAPPED_BOILER).transform(HORIZONTAL_AXIS, Axis.X)
            .next(Styles.IRON_WRAPPED_BOILER).transform(HORIZONTAL_AXIS, Axis.X)
            .nextRow()
            .nextRow()
            .next(Styles.BOILER).transform(HORIZONTAL_AXIS, Axis.Z).transform(BoilerBlock.STYLE, BoilerBlock.Style.GULLET)
            .skip()
            .skip()
            .next(Styles.BOILER).transform(HORIZONTAL_AXIS, Axis.Z).transform(BoilerBlock.STYLE, BoilerBlock.Style.SMOKEBOX);
    }

    private record Transform<T extends Comparable<T>>(Property<T> property, T value) {
        public static <T extends Comparable<T>> Transform<T> of(Property<T> property, T value) {
            return new Transform<>(property, value);
        }

        public BlockState apply(BlockState state) {
            return state.setValue(property, value);
        }
    }

    private static class PatternBuilder {
        private @Nullable Entry latest = null;

        final List<Entry> entries = new ArrayList<>();
        int xOffset = 0;
        int yOffset = 0;
        int maxWidth = 0;

        PatternBuilder next(Styles style) {
            latest = new Entry(style, xOffset, yOffset);
            xOffset += 1;
            maxWidth = Math.max(maxWidth, xOffset);
            entries.add(latest);
            return this;
        }

        PatternBuilder skip() {
            xOffset += 1;
            maxWidth = Math.max(maxWidth, xOffset);
            return this;
        }

        PatternBuilder nextRow() {
            xOffset = 0;
            yOffset += 1;
            return this;
        }

        <T extends Comparable<T>> PatternBuilder transform(Property<T> property, T value) {
            if (latest != null) {
                latest.transforms.add(Transform.of(property, value));
            }
            return this;
        }

        void place(Level level, BlockPos pos, DyeColor color) {
            for (Entry entry : entries) {
                BlockState state = entry.style.get(color).getDefaultState();
                for (Transform<?> transform : entry.transforms) {
                    state = transform.apply(state);
                }
                level.setBlockAndUpdate(pos.offset(entry.xOffset, entry.yOffset, 0), state);
            }
        }
    }

    private static class Entry {
        final Styles style;
        final int xOffset;
        final int yOffset;
        final List<Transform<?>> transforms = new ArrayList<>();

        private Entry(Styles style, int xOffset, int yOffset) {
            this.style = style;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
