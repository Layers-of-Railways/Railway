package com.railwayteam.railways.content.conductor.vent;

import com.railwayteam.railways.Config;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class VentBlock extends CopycatBlock implements IWrenchable {
    public static final BooleanProperty CONDUCTOR_VISIBLE = BooleanProperty.create("conductor_visible");
    protected VentBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(CONDUCTOR_VISIBLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CONDUCTOR_VISIBLE));
    }

    @ExpectPlatform
    public static VentBlock create(Properties properties) {
        throw new AssertionError();
    }

    @Nullable
    @Override
    public BlockState getConnectiveMaterial(BlockAndTintGetter reader, BlockState otherState, Direction face, BlockPos fromPos, BlockPos toPos) {
        return getMaterial(reader, toPos);
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return true;
    }

    @Override
    public boolean isUnblockableConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, BlockPos fromPos, BlockPos toPos) {
        return true;
    }

    protected Optional<BlockPos> getTeleportTarget(Level level, BlockPos start, Direction prevDirection) {
        Set<BlockPos> visited = new HashSet<>();
        BlockPos.MutableBlockPos end = start.mutable();

        int panic = Config.MAX_CONDUCTOR_VENT_LENGTH.get();
        Outer: while (true) {
            if (panic-- < 0) {
                return Optional.empty();
            }
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

    protected boolean teleportConductorInternal(Level level, BlockPos start, ConductorEntity conductor, @Nullable Direction prevDirection) {
        if (prevDirection == null) {
            BlockPos normal = conductor.blockPosition().subtract(start);
            prevDirection = Direction.fromNormal(normal);
            if (prevDirection == null)
                prevDirection = Direction.NORTH;
            else
                prevDirection = prevDirection.getOpposite();
        }
        Optional<BlockPos> target = getTeleportTarget(level, start, prevDirection);
        if (target.isPresent()) {
            BlockPos end = target.get();
            if (!level.getBlockState(end.above()).isAir())
                end = end.below();
            conductor.teleportToForce(end.getX() + 0.5, end.getY() + 0.0, end.getZ() + 0.5);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel && ConductorEntity.isPlayerDisguised(player)) {
            Direction direction = hit.getDirection().getOpposite();

            Optional<BlockPos> target = getTeleportTarget(level, pos, direction);
            if (target.isPresent()) {
                BlockPos end = target.get();
                if (!level.getBlockState(end.above()).isAir())
                    end = end.below();
                //serverPlayer.connection.teleport(end.getX(), end.getY(), end.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
                serverPlayer.teleportTo(serverLevel, end.getX() + 0.5, end.getY() + 0.0, end.getZ() + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
                //conductor.teleportToForce(end.getX() + 0.5, end.getY() + 0.0, end.getZ() + 0.5);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        super.entityInside(state, level, pos, entity);
        teleportConductor(level, pos, entity, null);
    }

    public void teleportConductor(@NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity, @Nullable Direction direction) {
        if (level.isClientSide)
            return;
        if (entity instanceof ConductorEntity conductor && conductor.isPossessed()) {
            if (direction != null || conductor.ventCooldown <= 0)
                teleportConductorInternal(level, pos, conductor, direction);
            conductor.ventCooldown = 20;
        }
    }

    public static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 1.0, 1.0, 15.0, 15.0, 15.0);
    public static final VoxelShape OUTLINE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext ec && ec.getEntity() instanceof ConductorEntity)
            return COLLISION_SHAPE;
        return OUTLINE_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    public boolean supportsExternalFaceHiding(BlockState state) {
        throw new AssertionError();
    }

    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState,
                                     Direction dir) {
        throw new AssertionError();
    }
}
