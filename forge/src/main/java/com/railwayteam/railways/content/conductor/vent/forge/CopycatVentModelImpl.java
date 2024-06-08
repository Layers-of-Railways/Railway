/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor.vent.forge;

import com.railwayteam.railways.content.conductor.ClientHandler;
import com.railwayteam.railways.content.conductor.vent.CopycatVentModel;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

public class CopycatVentModelImpl extends CopycatVentModel {

    public CopycatVentModelImpl(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material, ModelData wrappedData, RenderType renderType) {
        if (ClientHandler.isPlayerMountedOnCamera()) {
            material = CRBlocks.CONDUCTOR_VENT.getDefaultState().setValue(VentBlock.CONDUCTOR_VISIBLE, true);
            BakedModel originalModel = getModelOf(material);
            if (originalModel instanceof CopycatVentModelImpl impl)
                return impl.originalModel.getQuads(state, side, rand, wrappedData, renderType);
        }
        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);

        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad quad : templateQuads) {
            quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad,
                    BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), CUBE_AABB, Vec3.ZERO)));
        }

        return quads;
    }

    public static CopycatVentModel create(BakedModel bakedModel) {
        return new CopycatVentModelImpl(bakedModel);
    }
}
