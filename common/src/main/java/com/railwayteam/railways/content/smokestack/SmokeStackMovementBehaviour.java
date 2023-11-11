package com.railwayteam.railways.content.smokestack;


import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.smokestack.particles.chimneypush.ChimneyPushParticleData;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SmokeStackMovementBehaviour implements MovementBehaviour {

    private static class TemporaryData {
        @NotNull
        final LerpedFloat chanceChaser;
        @NotNull
        final LerpedFloat speedMultiplierChaser;
        long movementStartTick;
        boolean wasStopped = true;

        public TemporaryData(MovementContext context) {
            chanceChaser = LerpedFloat.linear();
            speedMultiplierChaser = LerpedFloat.linear();
            this.movementStartTick = 0;
            startMoving(context);
        }

        public void startMoving(MovementContext context) {
            movementStartTick = context.world.getGameTime();
        }

        public long getMovementTicks(MovementContext context) {
            return context.world.getGameTime() - movementStartTick;
        }
    }

    private final boolean renderAsNormalBlockEntity;
    private final boolean createsSmoke;
    private final boolean spawnExtraSmoke;

    public SmokeStackMovementBehaviour() {
        this(true);
    }

    public SmokeStackMovementBehaviour(boolean spawnExtraSmoke) {
        this(false, true, spawnExtraSmoke);
    }

    public SmokeStackMovementBehaviour(boolean renderAsNormalBlockEntity, boolean createsSmoke, boolean spawnExtraSmoke) {
        this.renderAsNormalBlockEntity = renderAsNormalBlockEntity;
        this.createsSmoke = createsSmoke;
        this.spawnExtraSmoke = spawnExtraSmoke;
    }

    @Override
    public boolean renderAsNormalBlockEntity() {
        return renderAsNormalBlockEntity;
    }

    @Override
    public void startMoving(MovementContext context) {
        MovementBehaviour.super.startMoving(context);
        context.temporaryData = new TemporaryData(context);
    }

    @Override
    public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
        MovementBehaviour.super.onSpeedChanged(context, oldMotion, motion);
        boolean isStopped = Mth.equal(motion.lengthSqr(), 0);
        if (context.temporaryData instanceof TemporaryData temporaryData && isStopped != temporaryData.wasStopped) {
            if (!isStopped)
                temporaryData.startMoving(context);
            temporaryData.wasStopped = isStopped;
        }
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null
            || !context.state.getValue(SmokeStackBlock.ENABLED))
            return;

        TemporaryData data;
        if (context.temporaryData instanceof TemporaryData tempDat) {
            data = tempDat;
        } else {
            data = new TemporaryData(context);
            context.temporaryData = data;
        }

        LerpedFloat chanceChaser = data.chanceChaser;
        LerpedFloat speedMultiplierChaser = data.speedMultiplierChaser;

        long movementTicks = data.getMovementTicks(context);

        float chanceModifierTarget = (Math.abs(context.getAnimationSpeed()) + 100) / 800;
        chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;

        if (context.contraption.presentBlockEntities.get(context.localPos) instanceof ISpeedNotifiable notifiable) {
            notifiable.notifySpeed(chanceModifierTarget);
        }

/*        Carriage carriage;
        if (context.contraption.entity instanceof CarriageContraptionEntity cce && (carriage = cce.getCarriage()) != null) {
            Train train = carriage.train;
            double actualSpeed = train.speed;
            chanceModifierTarget = (float) ((Math.abs(actualSpeed * 1500) + 100) / 800);
            chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;
        }*/

        if (!createsSmoke)
            return;

        chanceChaser.chase(chanceModifierTarget, chanceModifierTarget>chanceChaser.getChaseTarget() ? 0.1 : 0.01, LerpedFloat.Chaser.LINEAR);
        chanceChaser.tickChaser();
        float chanceModifier = chanceChaser.getValue() * (spawnExtraSmoke ? 1.0f : 0.5f);

        int maxModifier = 0;
        int minModifier = 0;
        if (chanceModifier > 2) {
            maxModifier += (int) (chanceModifier+0.5) - 1;
            if (chanceModifier > 3) {
                minModifier = (int) (chanceModifier+0.5) - 2;
            } else {
                minModifier = 1;
            }
        } else if (chanceModifier > 1) {
            maxModifier++;
        }

        if (CRConfigs.client().smokeType.get() == SmokeType.CARTOON) {
            maxModifier += 2;
        } else {
            minModifier += 5;
            maxModifier += 15;
        }

        // Mostly copied from CampfireBlock and CampfireBlockEntity
        RandomSource random = context.world.random;
        SmokeStackBlock.SmokeStackType type = ((SmokeStackBlock) context.state.getBlock()).type;
        double speedModifierTarget = 5 * (0.5+maxModifier);
        speedMultiplierChaser.chase(speedModifierTarget, 0.4, LerpedFloat.Chaser.LINEAR);
        speedMultiplierChaser.tickChaser();

        DyeColor color = null;
        boolean isSoul = false;
        if (context.blockEntityData != null) {
            if (context.blockEntityData.contains("color", Tag.TAG_INT)) {
                int colorOrdinal = context.blockEntityData.getInt("color");
                color = DyeColor.byId(colorOrdinal);
            }
            isSoul = context.blockEntityData.getBoolean("isSoul");
        }

        // chimney push
        if (CRConfigs.client().smokeType.get() == SmokeType.CARTOON) {
            if (movementTicks == 0) {
                ParticleOptions particleType;
                if (color != null) {
                    float[] c = color.getTextureDiffuseColors();
                    particleType = ChimneyPushParticleData.create(random.nextBoolean(), false, c[0], c[1], c[2]);
                } else {
                    particleType = ChimneyPushParticleData.create(random.nextBoolean(), false);
                }

                Vec3 pos = context.position.subtract(0.5, 0, 0.5).add(type.getParticleSpawnOffset());
                context.world.addAlwaysVisibleParticle(particleType, true, pos.x, pos.y, pos.z, context.motion.x, context.motion.y, context.motion.z);
            } else if (movementTicks == 8) {
                for (int i = 0; i < 3; i++) {
                    SmokeStackBlock.makeParticles(context.world, context.position.subtract(0.5, 0, 0.5).subtract((random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5), random.nextBoolean(), true,
                        type.getParticleSpawnOffset(), type.getParticleSpawnDelta(), speedMultiplierChaser.getValue(), false, color, true, isSoul);
                }
            } else if (movementTicks < 15) {
                return;
            } else {
                movementTicks -= 15;
            }
        }

        // normal smoke
        if (random.nextFloat() < type.particleSpawnChance * chanceModifier * CRConfigs.client().smokePercentage.get()) {
            //if (movementTicks < 40)
            //    color = DyeColor.BLUE;
            for(int i = 0; i < random.nextInt((type.maxParticles + maxModifier - (type.minParticles + minModifier))) + type.minParticles + minModifier; ++i) {
                boolean small = movementTicks < 50;
                if (!small) {
                    double smallChance = 0.33;
                    if (movementTicks < 100) {
                        smallChance = Mth.lerp((movementTicks - 50) / 50.0f, 1.0, 0.33);
                    }
                    double speedFactor = 0.3 + (0.7 * Math.max(0, Math.min(chanceModifier / 2, 1)));
                    small = random.nextDouble() * speedFactor < smallChance;
                }
                SmokeStackBlock.makeParticles(context.world, context.position.subtract(0.5, 0, 0.5).subtract((random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5), random.nextBoolean(), true,
                    type.getParticleSpawnOffset(), type.getParticleSpawnDelta(), speedMultiplierChaser.getValue(), false, color, small, isSoul);
            }
        }
    }
}
