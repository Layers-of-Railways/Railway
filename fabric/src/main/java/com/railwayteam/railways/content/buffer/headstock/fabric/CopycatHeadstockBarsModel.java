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

package com.railwayteam.railways.content.buffer.headstock.fabric;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class CopycatHeadstockBarsModel extends CopycatModel {
    public CopycatHeadstockBarsModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    public void emitBlockQuadsInner(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context, BlockState material, boolean[] cullFaceRemovalData, boolean[] occlusionData) {
        CullFaceRemovalData actualCullFaceRemovalData = new CullFaceRemovalData();
        OcclusionData actualOcclusionData = new OcclusionData();
        for (int i = 0; i < 6; i++) {
            if (cullFaceRemovalData[i]) {
                actualCullFaceRemovalData.remove(Direction.from3DDataValue(i));
            }
            if (occlusionData[i]) {
                actualOcclusionData.occlude(Direction.from3DDataValue(i));
            }
        }
        emitBlockQuadsInner(blockView, state, pos, randomSupplier, context, material, actualCullFaceRemovalData, actualOcclusionData);
    }

    @Override
    protected void emitBlockQuadsInner(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context, BlockState material, CullFaceRemovalData cullFaceRemovalData, OcclusionData occlusionData) {
        BakedModel model = getModelOf(material);
        TextureAtlasSprite mainTargetSprite = model.getParticleIcon();

        SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS));

        // This is very cursed
        SpriteAndBool altTargetSpriteHolder = new SpriteAndBool(mainTargetSprite, true);
        context.pushTransform(quad -> {
            if (altTargetSpriteHolder.bool && quad.cullFace() == null && quad.lightFace() == Direction.UP) {
                altTargetSpriteHolder.sprite = spriteFinder.find(quad, 0);
                altTargetSpriteHolder.bool = false;
            }
            return false;
        });
        ((FabricBakedModel) model).emitBlockQuads(blockView, material, pos, randomSupplier, context);
        context.popTransform();
        TextureAtlasSprite altTargetSprite = altTargetSpriteHolder.sprite;

        context.pushTransform(quad -> {
            TextureAtlasSprite targetSprite;
            Direction cullFace = quad.cullFace();
            if (cullFace != null && cullFace.getAxis() == Direction.Axis.Y) {
                targetSprite = altTargetSprite;
            } else {
                targetSprite = mainTargetSprite;
            }

            if (cullFaceRemovalData.shouldRemove(quad.cullFace())) {
                quad.cullFace(null);
            }

            TextureAtlasSprite original = spriteFinder.find(quad, 0);
            for (int vertex = 0; vertex < 4; vertex++) {
                float u = targetSprite.getU(SpriteShiftEntry.getUnInterpolatedU(original, quad.spriteU(vertex, 0)));
                float v = targetSprite.getV(SpriteShiftEntry.getUnInterpolatedV(original, quad.spriteV(vertex, 0)));
                quad.sprite(vertex, 0, u, v);
            }
            return true;
        });
        ((FabricBakedModel) wrapped).emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }

    private static class SpriteAndBool {
        public TextureAtlasSprite sprite;
        public boolean bool;

        public SpriteAndBool(TextureAtlasSprite sprite, boolean bool) {
            this.sprite = sprite;
            this.bool = bool;
        }
    }
}
