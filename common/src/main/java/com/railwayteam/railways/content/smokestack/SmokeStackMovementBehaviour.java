package com.railwayteam.railways.content.smokestack;


import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock;
import com.railwayteam.railways.content.smokestack.particles.chimneypush.ChimneyPushParticle;
import com.railwayteam.railways.content.smokestack.particles.chimneypush.ChimneyPushParticleData;
import com.railwayteam.railways.mixin.client.AccessorLevelRenderer;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SmokeStackMovementBehaviour implements MovementBehaviour {

    private static class TemporaryData {
        @NotNull
        final LerpedFloat chanceChaser;
        @NotNull
        final LerpedFloat speedMultiplierChaser;
        long movementStartTick;
        boolean wasStopped = true;
        int nextSmallPuffOffset = 0;

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

        @Environment(EnvType.CLIENT)
        private List<ChimneyPushParticle> pushParticles;

        @Environment(EnvType.CLIENT)
        void moveParticles(MovementContext context) {
            if (pushParticles == null) return;

            SmokeStackBlock.SmokeStackType type = ((SmokeStackBlock) context.state.getBlock()).type;
            Vec3 pos = context.position.subtract(0.5, 0, 0.5).add(type.getParticleSpawnOffset());

            Iterator<ChimneyPushParticle> iterator = pushParticles.iterator();
            while (iterator.hasNext()) {
                ChimneyPushParticle particle = iterator.next();
                particle.setPos(pos.x, pos.y, pos.z);
                particle.setOldPos();
                if (!particle.isAlive())
                    iterator.remove(); //tp -6.417 2.7 33.5
            }
        }

        @Environment(EnvType.CLIENT)
        List<ChimneyPushParticle> getPushParticles() {
            return pushParticles == null ? Collections.emptyList() : pushParticles;
        }

        @Environment(EnvType.CLIENT)
        @SuppressWarnings("SameParameterValue")
        void addAndTrackParticle(ChimneyPushParticleData<?> particleType, boolean force, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            if (pushParticles == null) pushParticles = new ArrayList<>();
            Particle added = ((AccessorLevelRenderer) Minecraft.getInstance().levelRenderer).callAddParticleInternal(particleType, force, true, x, y, z, xSpeed, ySpeed, zSpeed);
            if (added instanceof ChimneyPushParticle chimneyPushParticle)
                pushParticles.add(chimneyPushParticle);
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

    @Environment(EnvType.CLIENT)
    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!(context.temporaryData instanceof TemporaryData temporaryData)) {
            return;
        }
        temporaryData.moveParticles(context);
        if (true) {
            return;
        }
        ShaderInstance oldShader = RenderSystem.getShader();
        float[] oldShaderColor = RenderSystem.getShaderColor();
        {
            ParticleRenderType renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            renderType.begin(bufferBuilder, Minecraft.getInstance().getTextureManager());

            for (ChimneyPushParticle particle : temporaryData.getPushParticles()) {
                particle.render(bufferBuilder, Minecraft.getInstance().gameRenderer.getMainCamera(), AnimationTickHolder.getPartialTicks(renderWorld));
            }

            renderType.end(tesselator);
        }
        RenderSystem.setShader(() -> oldShader);
        RenderSystem.setShaderColor(oldShaderColor[0], oldShaderColor[1], oldShaderColor[2], oldShaderColor[3]);
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

        data.moveParticles(context);

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

        SmokeType smokeType = CRConfigs.client().smokeType.get();
        if (smokeType == SmokeType.CARTOON) {
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
        if (smokeType == SmokeType.CARTOON && color != DyeColor.WHITE) {
            if (movementTicks == 0) {
                ChimneyPushParticleData<?> particleType;
                if (color != null) {
                    particleType = ChimneyPushParticleData.create(random.nextBoolean(), false, color);
                } else {
                    particleType = ChimneyPushParticleData.create(random.nextBoolean(), false);
                }

                Vec3 pos = context.position.subtract(0.5, 0, 0.5).add(type.getParticleSpawnOffset());
                data.addAndTrackParticle(particleType, true, pos.x, pos.y, pos.z, context.motion.x, context.motion.y, context.motion.z);
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
        if (smokeType != SmokeType.CARTOON || color != DyeColor.WHITE) {
            if (random.nextFloat() < type.particleSpawnChance * chanceModifier * CRConfigs.client().smokePercentage.get()) {
                for (int i = 0; i < random.nextInt((type.maxParticles + maxModifier - (type.minParticles + minModifier))) + type.minParticles + minModifier; ++i) {
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

        if (smokeType != SmokeType.CARTOON) return;
        // only real                                              and fake trains should emit steam & extra smoke puffs
        if (!(context.contraption instanceof CarriageContraption) && !(context.contraption instanceof TranslatingContraption)) return;

        // little smoke go vroom
        if (chanceModifier >= 0.25 && CRConfigs.client().spawnFasterPuffs.get()) {
            //                                        0.25 * 85
            int littleSmokeInterval = (int) Mth.clamp(21.25/chanceModifier, 15, 85);
            long time = context.world.getGameTime()-data.nextSmallPuffOffset;
            if (time % littleSmokeInterval >= 0 && time % littleSmokeInterval <= 2) {
                //if (movementTicks < 40)
                //    color = DyeColor.BLUE;
                for (int i = 0; i < random.nextInt((type.maxParticles + maxModifier - (type.minParticles + minModifier))) + type.minParticles + minModifier; ++i) {
                    boolean small = true;
                    SmokeStackBlock.makeParticles(context.world, context.position.subtract(0.5, 0, 0.5).subtract((random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.5), random.nextBoolean(), true,
                        type.getParticleSpawnOffset(), type.getParticleSpawnDelta(), -1, false, color, small, isSoul);
                }
            }
            if (time % littleSmokeInterval == 3)
                data.nextSmallPuffOffset = random.nextIntBetweenInclusive(0, littleSmokeInterval/2);
        }

        // normal steam
        int steamInterval = (int) Mth.clamp(6.25/chanceModifier, 13, 50);
        if (CRConfigs.client().spawnSteam.get() || color == DyeColor.WHITE) {
            if (context.world.getGameTime() % steamInterval >= 0 && context.world.getGameTime() % steamInterval <= 3) {
                color = isSoul ? DyeColor.RED : DyeColor.WHITE;
                if (data.getPushParticles().isEmpty()) {
                    ChimneyPushParticleData<?> particleType = ChimneyPushParticleData.create(false, false, color);

                    Vec3 pos = context.position.subtract(0.5, 0, 0.5).add(type.getParticleSpawnOffset());
                    data.addAndTrackParticle(particleType, true, pos.x, pos.y, pos.z, context.motion.x, context.motion.y, context.motion.z);
                }
                for (int i = 0; i < random.nextInt((type.maxParticles + maxModifier - (type.minParticles + minModifier))) + type.minParticles + minModifier; ++i) {
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
                        type.getParticleSpawnOffset(), type.getParticleSpawnDelta(), speedMultiplierChaser.getValue(), false, color, small, false);
                }
            }
        }
    }
}
