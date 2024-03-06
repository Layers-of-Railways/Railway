package com.railwayteam.railways.content.smokestack.block;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.smokestack.SmokeType;
import com.railwayteam.railways.content.smokestack.block.be.SmokeStackBlockEntity;
import com.railwayteam.railways.content.smokestack.particles.legacy.SmokeParticleData;
import com.railwayteam.railways.content.smokestack.particles.puffs.PuffSmokeParticle;
import com.railwayteam.railways.content.smokestack.particles.puffs.PuffSmokeParticleData;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.ShapeWrapper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SmokeStackBlock extends AbstractSmokeStackBlock<SmokeStackBlockEntity> {
    public final SmokeStackType type;
    public boolean createsStationarySmoke;

    public SmokeStackBlock(Properties properties, SmokeStackType type, ShapeWrapper shape, boolean createsStationarySmoke, String variant) {
        super(properties, shape, variant);
        this.registerDefaultState(this.makeDefaultState());
        this.type = type;
        this.createsStationarySmoke = createsStationarySmoke;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).getItem() instanceof DyeItem dyeItem) {
            DyeColor color = dyeItem.getDyeColor();
            withBlockEntityDo(pLevel, pPos, te -> te.setColor(color));
            if (!pPlayer.isCreative()) {
                pPlayer.getItemInHand(pHand).shrink(1);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        if (pPlayer.getItemInHand(pHand).is(ItemTags.SOUL_FIRE_BASE_BLOCKS)) {
            withBlockEntityDo(pLevel, pPos, te -> te.setSoul(true));
            if (!pPlayer.isCreative()) {
                pPlayer.getItemInHand(pHand).shrink(1);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        if (pPlayer.isShiftKeyDown()) {
            withBlockEntityDo(pLevel, pPos, te -> {
                te.setSoul(false);
                te.setColor(null);
            });
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
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
        DyeColor color = null;
        boolean isSoul = false;
        if (level.getBlockEntity(BlockPos.containing(pos)) instanceof SmokeStackBlockEntity be) {
            isSoul = be.isSoul();
            color = be.getColor();
        }
        makeParticles(level, pos, isSignalFire, spawnExtraSmoke, spawnOffset, spawnDelta, speedMultiplier, stationary, color, null, isSoul);
    }

    public static void makeParticles(Level level, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta, double speedMultiplier, boolean stationary, @Nullable DyeColor color) {
        makeParticles(level, pos, isSignalFire, spawnExtraSmoke, spawnOffset, spawnDelta, speedMultiplier, stationary, color, null);
    }

    public static void makeParticles(Level level, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta, double speedMultiplier, boolean stationary, @Nullable DyeColor color, @Nullable Boolean small) {
        makeParticles(level, pos, isSignalFire, spawnExtraSmoke, spawnOffset, spawnDelta, speedMultiplier, stationary, color, small, false);
    }

    public static void makeParticles(Level level, Vec3 pos, boolean isSignalFire, boolean spawnExtraSmoke, Vec3 spawnOffset, Vec3 spawnDelta, double speedMultiplier, boolean stationary, @Nullable DyeColor color, @Nullable Boolean small, boolean isSoul) {
        RandomSource random = level.getRandom();
        SmokeType smokeType = CRConfigs.client().smokeType.get();
        if (small == null)
            small = random.nextDouble() < 0.33;
        switch (smokeType) {
            case VANILLA -> {
                SimpleParticleType particleType = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                level.addAlwaysVisibleParticle(particleType, true,
                    pos.x() + spawnOffset.x + random.nextDouble() * spawnDelta.x * (double)(random.nextBoolean() ? 1 : -1),
                    pos.y() + random.nextDouble() * spawnDelta.y + spawnOffset.y,
                    pos.z() + spawnOffset.z + random.nextDouble() * spawnDelta.z * (double)(random.nextBoolean() ? 1 : -1),
                    0.0D, 0.07D*speedMultiplier / (stationary ? 1. : 25.), 0.0D);
            }
            case OLD -> {
                ParticleOptions particleType;
                if (color != null) {
                    float[] c = color.getTextureDiffuseColors();
                    particleType = new SmokeParticleData(stationary, c[0], c[1], c[2]);
                } else {
                    particleType = new SmokeParticleData(stationary);
                }
                level.addAlwaysVisibleParticle(particleType, true,
                    pos.x() + spawnOffset.x + random.nextDouble() * spawnDelta.x * (random.nextDouble() * 2 - 1),
                    pos.y() + random.nextDouble() * spawnDelta.y + spawnOffset.y + 0.5,
                    pos.z() + spawnOffset.z + random.nextDouble() * spawnDelta.z * (random.nextDouble() * 2 - 1),
                    0.0D, 0.07D * speedMultiplier * (stationary ? 25 : 1), 0.0D);
            }
            case CARTOON -> {
                ParticleOptions particleType;
                if (isSoul) {
                    particleType = PuffSmokeParticleData.create(small, stationary, -2, -2, -2);
                } else if (color != null) {
                    particleType = PuffSmokeParticleData.create(small, stationary, color);
                } else {
                    particleType = PuffSmokeParticleData.create(small, stationary);
                }
                level.addAlwaysVisibleParticle(particleType, true,
                    pos.x() + spawnOffset.x + random.nextDouble() * spawnDelta.x * (random.nextDouble() * 2 - 1),
                    pos.y() + random.nextDouble() * spawnDelta.y + spawnOffset.y + 0.5,
                    pos.z() + spawnOffset.z + random.nextDouble() * spawnDelta.z * (random.nextDouble() * 2 - 1),
                    0.0D, Mth.equal(speedMultiplier, -1) ? PuffSmokeParticle.DOUBLE_SPEED_SENTINEL : 2.1, 0.0D);
            }
        }
        if (spawnExtraSmoke && smokeType != SmokeType.CARTOON) {
            level.addParticle(ParticleTypes.SMOKE,
                pos.x() + spawnOffset.x + random.nextDouble() * spawnDelta.x * 0.75d * (double)(random.nextBoolean() ? 1 : -1),
                pos.y() + spawnOffset.y - 0.1d,
                pos.z() + spawnOffset.z + random.nextDouble() * spawnDelta.z * 0.75d * (double)(random.nextBoolean() ? 1 : -1),
                0.0D, 0.005D*speedMultiplier, 0.0D);
        }

    }

    public void blockEntityAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ENABLED)) {
            if (random.nextFloat() < type.particleSpawnChance * 1.5 && createsStationarySmoke) {
                for(int i = 0; i < random.nextInt((type.maxParticles - type.minParticles)) + type.minParticles; ++i) {
                    makeParticlesStationary(level, pos, random.nextBoolean(), true, type.getParticleSpawnOffset(), type.getParticleSpawnDelta());
                }
            }

        }
    }

    @Override
    public Class<SmokeStackBlockEntity> getBlockEntityClass() {
        return SmokeStackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmokeStackBlockEntity> getBlockEntityType() {
        return CRBlockEntities.SMOKE_STACK.get();
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

    public enum RotationType {
        NONE, AXIS, FACING
    }
}
