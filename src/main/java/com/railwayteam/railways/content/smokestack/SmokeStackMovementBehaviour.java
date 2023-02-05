package com.railwayteam.railways.content.smokestack;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.logistics.trains.entity.Carriage;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.util.RandomSource;

public class SmokeStackMovementBehaviour implements MovementBehaviour {

    private LerpedFloat chanceChaser;
    private LerpedFloat speedMultiplierChaser;

    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null
            || !context.state.getValue(SmokeStackBlock.ENABLED))
            return;

        if (chanceChaser == null)
            chanceChaser = LerpedFloat.linear();
        if (speedMultiplierChaser == null)
            speedMultiplierChaser = LerpedFloat.linear();

        float chanceModifierTarget = (Math.abs(context.getAnimationSpeed()) + 100) / 800;
        chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;

/*        Carriage carriage;
        if (context.contraption.entity instanceof CarriageContraptionEntity cce && (carriage = cce.getCarriage()) != null) {
            Train train = carriage.train;
            double actualSpeed = train.speed;
            chanceModifierTarget = (float) ((Math.abs(actualSpeed * 1500) + 100) / 800);
            chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;
        }*/

        chanceChaser.chase(chanceModifierTarget, chanceModifierTarget>chanceChaser.getValue() ? 0.1 : 0.01, LerpedFloat.Chaser.EXP);
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
        speedMultiplierChaser.chase(speedModifierTarget, 0.2, LerpedFloat.Chaser.EXP);
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
