/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.content.moving_bes;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

public class GuiBlockLevelAccess implements ContainerLevelAccess {
    private final Level level;
    private final AbstractContraptionEntity abstractContraptionEntity;
    private final BlockPos blockPos;

    public GuiBlockLevelAccess(Level level, AbstractContraptionEntity abstractContraptionEntity, BlockPos blockPos) {
        this.level = level;
        this.abstractContraptionEntity = abstractContraptionEntity;
        this.blockPos = blockPos;
    }

    @Override
    public <T> @NotNull Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosConsumer) {
        return Optional.of(
                levelPosConsumer.apply(level,
                        BlockPos.containing(abstractContraptionEntity.toGlobalVector(Vec3.atCenterOf(blockPos), 1))
                )
        );
    }
}