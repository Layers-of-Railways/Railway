package com.railwayteam.railways.content.smokestack;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SmokeStackMovementBehaviour implements MovementBehaviour {

    private final boolean renderAsNormalTileEntity;

    private final Map<Integer, LerpedFloat> chanceChasers = new HashMap<>();
    private final Map<Integer, LerpedFloat> speedMultiplierChasers = new HashMap<>();

    public SmokeStackMovementBehaviour() {
        this(false);
    }

    public SmokeStackMovementBehaviour(boolean renderAsNormalTileEntity) {
        this.renderAsNormalTileEntity = renderAsNormalTileEntity;
    }

    @Override
    public boolean renderAsNormalTileEntity() {
        return renderAsNormalTileEntity;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null
            || !context.state.getValue(SmokeStackBlock.ENABLED))
            return;

        int key = context.hashCode();

        LerpedFloat chanceChaser = chanceChasers.get(key);
        LerpedFloat speedMultiplierChaser = speedMultiplierChasers.get(key);

        if (chanceChaser == null) {
            chanceChaser = LerpedFloat.linear();
            chanceChasers.put(key, chanceChaser);
        }
        if (speedMultiplierChaser == null) {
            speedMultiplierChaser = LerpedFloat.linear();
            speedMultiplierChasers.put(key, speedMultiplierChaser);
        }

        float chanceModifierTarget = (Math.abs(context.getAnimationSpeed()) + 100) / 800;
        chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;

        if (context.contraption.presentTileEntities.get(context.localPos) instanceof ISpeedNotifiable notifiable) {
            notifiable.notifySpeed(chanceModifierTarget);
        }

/*        Carriage carriage;
        if (context.contraption.entity instanceof CarriageContraptionEntity cce && (carriage = cce.getCarriage()) != null) {
            Train train = carriage.train;
            double actualSpeed = train.speed;
            chanceModifierTarget = (float) ((Math.abs(actualSpeed * 1500) + 100) / 800);
            chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;
        }*/

        chanceChaser.chase(chanceModifierTarget, chanceModifierTarget>chanceChaser.getChaseTarget() ? 0.1 : 0.01, LerpedFloat.Chaser.LINEAR);
        chanceChaser.tickChaser();
        float chanceModifier = chanceChaser.getValue();

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

        // Mostly copied from CampfireBlock and CampfireTileEntity
        RandomSource random = context.world.random;
        SmokeStackBlock.SmokeStackType type = ((SmokeStackBlock) context.state.getBlock()).type;
        double speedModifierTarget = 5 * (0.5+maxModifier);
        speedMultiplierChaser.chase(speedModifierTarget, 0.4, LerpedFloat.Chaser.LINEAR);
        speedMultiplierChaser.tickChaser();
        if (random.nextFloat() < type.particleSpawnChance * chanceModifier) {
            for(int i = 0; i < random.nextInt((type.maxParticles + maxModifier - (type.minParticles + minModifier))) + type.minParticles + minModifier; ++i) {
                /*context.world.addAlwaysVisibleParticle(
                    context.state.getValue(CampfireBlock.SIGNAL_FIRE) ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
                        : ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    true, context.position.x() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D),
                    context.position.y() + random.nextDouble() + random.nextDouble(),
                    context.position.z() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D), 0.0D, 0.07D,
                    0.0D);*/
                SmokeStackBlock.makeParticles(context.world, context.position.subtract(0.5, 0, 0.5), random.nextBoolean(), true,
                    type.getParticleSpawnOffset(), type.getParticleSpawnDelta(), speedMultiplierChaser.getValue());
            }
        }
    }
}
