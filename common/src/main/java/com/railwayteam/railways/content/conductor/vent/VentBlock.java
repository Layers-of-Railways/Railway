package com.railwayteam.railways.content.conductor.vent;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class VentBlock extends Block implements IWrenchable {
    public VentBlock(Properties properties) {
        super(properties);
    }

    public Optional<BlockPos> getTeleportTarget(Level level, BlockPos start, ConductorEntity conductor) {
        Set<BlockPos> visited = new HashSet<>();
        BlockPos.MutableBlockPos end = start.mutable();
        BlockPos normal = conductor.blockPosition().subtract(start);
        Direction prevDirection = Direction.fromNormal(normal);
        if (prevDirection == null)
            prevDirection = Direction.NORTH;
        else
            prevDirection = prevDirection.getOpposite();

        Outer: while (true) {
            end.move(prevDirection);
            if (visited.contains(end)) {
                return Optional.empty();
            }

            if (level.getBlockState(end).getBlock() == this) {
                visited.add(end.immutable());
                continue;
            } else {
                end.move(prevDirection.getOpposite());
            }

            for (Direction direction : Direction.values()) {
                if (direction == prevDirection || direction == prevDirection.getOpposite()) continue;

                end.move(direction);
                if (visited.contains(end))
                    return Optional.empty();

                if (level.getBlockState(end).getBlock() == this) {
                    visited.add(end.immutable());
                    prevDirection = direction;
                    continue Outer;
                } else {
                    end.move(direction.getOpposite());
                }
            }

            // flow only ends up here if no more vents are found
            break;
        }

        // search for air blocks to teleport to
        end.move(prevDirection);

        if (level.getBlockState(end).isAir()) {
            return Optional.of(end.immutable());
        } else {
            end.move(prevDirection.getOpposite());
        }

        for (Direction direction : Direction.values()) {
            if (direction == prevDirection || direction == prevDirection.getOpposite()) continue;

            end.move(direction);
            if (visited.contains(end))
                return Optional.empty();

            if (level.getBlockState(end).isAir()) {
                return Optional.of(end.immutable());
            } else {
                end.move(direction.getOpposite());
            }
        }

        return Optional.empty();
    }

    public boolean teleportConductor(Level level, BlockPos start, ConductorEntity conductor) {
        Optional<BlockPos> target = getTeleportTarget(level, start, conductor);
        if (target.isPresent()) {
            BlockPos end = target.get();
            conductor.teleportToForce(end.getX() + 0.5, end.getY() + 0.0, end.getZ() + 0.5);
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide)
            return;
        if (entity instanceof ConductorEntity conductor) {// && conductor.isPossessed()) {
            if (conductor.ventCooldown <= 0)
                teleportConductor(level, pos, conductor);
            conductor.ventCooldown = 20;
        }
    }

    public static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 1.0, 1.0, 15.0, 15.0, 15.0);
    public static final VoxelShape OUTLINE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return OUTLINE_SHAPE;
    }
}
