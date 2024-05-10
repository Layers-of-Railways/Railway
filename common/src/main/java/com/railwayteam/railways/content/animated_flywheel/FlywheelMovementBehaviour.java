package com.railwayteam.railways.content.animated_flywheel;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.mixin_interfaces.ICarriageFlywheel;
import com.railwayteam.railways.mixin_interfaces.IDistanceTravelled;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FlywheelMovementBehaviour implements MovementBehaviour {
    @Override public boolean renderAsNormalBlockEntity() { return true; }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        // Early exit checks, don't mind them :)
        if (!CRConfigs.client().animatedFlywheels.get()) return;
        if (!context.world.isClientSide || Minecraft.getInstance().isPaused()) return;
        if (!(context.contraption instanceof CarriageContraption carriageContraption)) return;
        if (!(carriageContraption.entity instanceof CarriageContraptionEntity carriageContraptionEntity)) return;
        if (!(context.contraption.presentBlockEntities.get(context.localPos) instanceof FlywheelBlockEntity flywheelBlockEntity)) return;
        if (flywheelBlockEntity.getBlockState().getValue(BlockStateProperties.AXIS).isVertical()) return;
        // It wasn't that bad was it? :^)

        Direction dir = carriageContraption.getAssemblyDirection();
        Direction.Axis flwAxis = flywheelBlockEntity.getBlockState().getValue(BlockStateProperties.AXIS);

        switch (dir) {
            case NORTH, SOUTH -> { if (flwAxis == Direction.Axis.Z) return; }
            case EAST, WEST -> { if (flwAxis == Direction.Axis.X) return; }
        }

        ICarriageFlywheel flywheel = ((ICarriageFlywheel) flywheelBlockEntity);
        double distanceTravelled = ((IDistanceTravelled) carriageContraptionEntity).railways$getDistanceTravelled();

        double angleDiff = 360 * (distanceTravelled / 3f) / (Math.PI *  2.8125);

        if (dir == Direction.SOUTH || dir == Direction.WEST)
            angleDiff = -angleDiff;

        float newWheelAngle = (float) (flywheel.railways$getAngle() + angleDiff % 360);

        flywheel.railways$setAngle(newWheelAngle);
    }
}
