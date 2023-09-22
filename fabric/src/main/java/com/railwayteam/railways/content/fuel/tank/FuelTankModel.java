package com.railwayteam.railways.content.fuel.tank;

import com.railwayteam.railways.registry.CRSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.function.Supplier;

public class FuelTankModel extends CTModel {

    public static FuelTankModel standard(BakedModel originalModel) {
        return new FuelTankModel(originalModel, CRSpriteShifts.FUEL_TANK, CRSpriteShifts.FUEL_TANK_TOP,
                CRSpriteShifts.FUEL_TANK_INNER);
    }

    private FuelTankModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top,
                           CTSpriteShiftEntry inner) {
        super(originalModel, new FluidTankCTBehaviour(side, top, inner));
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        FuelTankModel.CullData cullData = new FuelTankModel.CullData();
        for (Direction d : Iterate.horizontalDirections)
            cullData.setCulled(d, ConnectivityHandler.isConnected(blockView, pos, pos.relative(d)));

        context.pushTransform(quad -> {
            Direction cullFace = quad.cullFace();
            if (cullFace != null && cullData.isCulled(cullFace)) {
                return false;
            }
            quad.cullFace(null);
            return true;
        });
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }

    private static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis()
                    .isVertical())
                return;
            culledFaces[face.get2DDataValue()] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis()
                    .isVertical())
                return false;
            return culledFaces[face.get2DDataValue()];
        }
    }

}
