package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.util.BlockStateUtils;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SmokeStackBlock extends Block implements ProperWaterloggedBlock, IWrenchable {

    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public final SmokeStackType type;
    protected final ShapeWrapper shape;
    public boolean createsStationarySmoke;

    public SmokeStackBlock(Properties properties, SmokeStackType type, ShapeWrapper shape, boolean createsStationarySmoke) {
        super(properties);
        this.registerDefaultState(this.makeDefaultState());
        this.type = type;
        this.shape = shape;
        this.createsStationarySmoke = createsStationarySmoke;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape.get();
    }

    protected BlockState makeDefaultState() {
        return this.defaultBlockState()
            .setValue(ENABLED, true)
            .setValue(POWERED, false)
            .setValue(WATERLOGGED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ENABLED).add(POWERED).add(WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
            blockstate = blockstate.setValue(ENABLED, false).setValue(POWERED, true);
        }

        return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        updateWater(level, state, currentPos);
        return state;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        if (AllTags.AllItemTags.WRENCH.matches(pPlayer.getItemInHand(pHand))) {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        pState = pState.cycle(ENABLED);
        pLevel.setBlock(pPos, pState, 2);
        if (pState.getValue(WATERLOGGED))
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            boolean powered = level.hasNeighborSignal(pos);
            boolean shouldBeEnabled = !powered;
            if (powered != state.getValue(POWERED)) {
                if (state.getValue(ENABLED) != shouldBeEnabled) {
                    state = state.setValue(ENABLED, shouldBeEnabled);
                }

                level.setBlock(pos, state.setValue(POWERED, powered), 2);
                if (state.getValue(WATERLOGGED)) {
                    level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                }
            }
        }
    }

    public static void makeParticlesStationary(Level level, BlockPos pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta) {
        makeParticles(level, new Vec3(pos.getX(), pos.getY(), pos.getZ()), isSignalFire, spawnExtraSmoke, spawnOffset, spawnDelta, 1.0, true);
    }

    public static void makeParticles(Level level, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta) {
        makeParticles(level, pos, isSignalFire, spawnExtraSmoke, spawnOffset, spawnDelta, 1.0d);
    }


    public static void makeParticles(Level level, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta, double speedMultiplier) {
        makeParticles(level, pos, isSignalFire, spawnExtraSmoke, spawnOffset, spawnDelta, speedMultiplier, false);
    }

    public static void makeParticles(Level level, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta, double speedMultiplier, boolean stationary) {
        BlockPos underPos = new BlockPos(pos.x, pos.y-1, pos.z);
        BlockState underState = level.getBlockState(underPos);
        makeParticles(level, pos, isSignalFire, spawnExtraSmoke, spawnOffset, spawnDelta, speedMultiplier, stationary, underState);
    }

    public static void makeParticles(Level level, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta, double speedMultiplier, boolean stationary, BlockState underState) {
        RandomSource random = level.getRandom();
        if (CRConfigs.client().oldSmoke.get()) {
            SimpleParticleType particleType = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
            level.addAlwaysVisibleParticle(particleType, true,
                (double)pos.x() + spawnOffset.x + random.nextDouble() * spawnDelta.x * (double)(random.nextBoolean() ? 1 : -1),
                (double)pos.y() + random.nextDouble() * spawnDelta.y + spawnOffset.y,
                (double)pos.z() + spawnOffset.z + random.nextDouble() * spawnDelta.z * (double)(random.nextBoolean() ? 1 : -1),
                0.0D, 0.07D*speedMultiplier / (stationary ? 1. : 25.), 0.0D);
        } else {
            ParticleOptions particleType;
            if (underState.is(BlockTags.WOOL)) {
                DyeColor color = BlockStateUtils.getWoolColor(underState.getBlock());//state.getMapColor(level, underPos).col;
                float[] c = color.getTextureDiffuseColors();
                particleType = new SmokeParticleData(stationary, c[0], c[1], c[2]);
            } else {
                particleType = new SmokeParticleData(stationary);
            }
            level.addAlwaysVisibleParticle(particleType, true,
                (double) pos.x() + spawnOffset.x + random.nextDouble() * spawnDelta.x * (random.nextDouble() * 2 - 1),
                (double) pos.y() + random.nextDouble() * spawnDelta.y + spawnOffset.y + 0.5,
                (double) pos.z() + spawnOffset.z + random.nextDouble() * spawnDelta.z * (random.nextDouble() * 2 - 1),
                0.0D, 0.07D * speedMultiplier * (stationary ? 25 : 1), 0.0D);
        }
        if (spawnExtraSmoke) {
            level.addParticle(ParticleTypes.SMOKE,
                (double)pos.x() + spawnOffset.x + random.nextDouble() * spawnDelta.x * 0.75d * (double)(random.nextBoolean() ? 1 : -1),
                (double)pos.y() + spawnOffset.y - 0.1d,
                (double)pos.z() + spawnOffset.z + random.nextDouble() * spawnDelta.z * 0.75d * (double)(random.nextBoolean() ? 1 : -1),
                0.0D, 0.005D*speedMultiplier, 0.0D);
        }

    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles).
     */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ENABLED)) {
            if (random.nextFloat() < type.particleSpawnChance * 1.5 && createsStationarySmoke) {
                for(int i = 0; i < random.nextInt((type.maxParticles - type.minParticles)) + type.minParticles; ++i) {
                    makeParticlesStationary(level, pos, random.nextBoolean(), true, type.getParticleSpawnOffset(), type.getParticleSpawnDelta());
                }
            }

        }
    }

    public static class SmokeStackType {
        public Vec3 particleSpawnOffset;
        public Vec3 particleSpawnDelta;
        public double particleSpawnChance;
        public int minParticles;
        public int maxParticles;

        public SmokeStackType(double xOffset, double yOffset, double zOffset) {
            this(new Vec3(xOffset, yOffset, zOffset));
        }

        public SmokeStackType(Vec3 particleSpawnOffset) {
            this(particleSpawnOffset, new Vec3(0.3, 2.0, 0.3));
        }

        public SmokeStackType(Vec3 particleSpawnOffset, Vec3 particleSpawnDelta) {
            this(particleSpawnOffset, particleSpawnDelta, 2, 4);
        }

        public SmokeStackType(Vec3 particleSpawnOffset, Vec3 particleSpawnDelta, int minParticles, int maxParticles) {
            this(particleSpawnOffset, particleSpawnDelta, minParticles, maxParticles, 1.0F);
        }

        public SmokeStackType(Vec3 particleSpawnOffset, Vec3 particleSpawnDelta, int minParticles, int maxParticles, double particleSpawnChance) {
            this.particleSpawnOffset = particleSpawnOffset;
            this.particleSpawnDelta = particleSpawnDelta;
            this.minParticles = minParticles;
            this.maxParticles = maxParticles;
            this.particleSpawnChance = particleSpawnChance;
        }

        public Vec3 getParticleSpawnOffset() {
            return particleSpawnOffset;
        }

        public Vec3 getParticleSpawnDelta() {
            return particleSpawnDelta;
        }
    }
}
