/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor.vent.fabric;

import com.railwayteam.railways.content.conductor.ClientHandler;
import com.railwayteam.railways.content.conductor.vent.CopycatVentModel;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.registry.CRBlocks;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class CopycatVentModelImpl extends CopycatVentModel {
    public CopycatVentModelImpl(BakedModel originalModel) {
        super(originalModel);
    }

    /*@Override
    protected void emitBlockQuadsInner(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context, BlockState material, CullFaceRemovalData cullFaceRemovalData, OcclusionData occlusionData) {
        if (ClientHandler.isPlayerMountedOnCamera() || state.getValue(VentBlock.CONDUCTOR_VISIBLE)) {
            material = CRBlocks.CONDUCTOR_VENT.getDefaultState().setValue(VentBlock.CONDUCTOR_VISIBLE, true);
            if (getModelOf(material) instanceof CopycatVentModelImpl impl)
                ((FabricBakedModel) impl.wrapped).emitBlockQuads(blockView, material, pos, randomSupplier, context);
            return;
        }
        BakedModel model = getModelOf(material);

        SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS));

        // Use a mesh to defer quad emission since quads cannot be emitted inside a transform
        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();
        context.pushTransform(quad -> {
            if (cullFaceRemovalData.shouldRemove(quad.cullFace())) {
                quad.cullFace(null);
            } else if (occlusionData.isOccluded(quad.cullFace())) {
                // Add quad to mesh and do not render original quad to preserve quad render order
                // copyTo does not copy the material
                RenderMaterial quadMaterial = quad.material();
                quad.copyTo(emitter);
                emitter.material(quadMaterial);
                emitter.emit();
                return false;
            }

            // copyTo does not copy the material
            RenderMaterial quadMaterial = quad.material();
            quad.copyTo(emitter);
            emitter.material(quadMaterial);
            BakedModelHelper.cropAndMove(emitter, spriteFinder.find(emitter, 0), CUBE_AABB, Vec3.ZERO);
            emitter.emit();


            return false;
        });
        ((FabricBakedModel) model).emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
        context.meshConsumer().accept(meshBuilder.build());
    }*/

    @Override
    protected void emitBlockQuadsInner(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context, BlockState material, CullFaceRemovalData cullFaceRemovalData, OcclusionData occlusionData) {
        if (ClientHandler.isPlayerMountedOnCamera() || state.getValue(VentBlock.CONDUCTOR_VISIBLE)) {
            material = CRBlocks.CONDUCTOR_VENT.getDefaultState().setValue(VentBlock.CONDUCTOR_VISIBLE, true);
            if (getModelOf(material) instanceof CopycatVentModelImpl impl)
                ((FabricBakedModel) impl.wrapped).emitBlockQuads(blockView, material, pos, randomSupplier, context);
            return;
        }

        BakedModel model = getModelOf(material);

        // Use a mesh to defer quad emission since quads cannot be emitted inside a transform
        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        ((FabricBakedModel) model).emitBlockQuads(blockView, material, pos, randomSupplier, context);
        context.meshConsumer().accept(meshBuilder.build());
    }

    public static CopycatVentModel create(BakedModel bakedModel) {
        return new CopycatVentModelImpl(bakedModel);
    }
}
