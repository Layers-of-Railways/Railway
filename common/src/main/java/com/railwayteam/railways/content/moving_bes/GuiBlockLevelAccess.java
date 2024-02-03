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