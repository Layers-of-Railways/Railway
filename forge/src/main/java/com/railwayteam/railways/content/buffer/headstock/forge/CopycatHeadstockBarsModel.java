package com.railwayteam.railways.content.buffer.headstock.forge;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

public class CopycatHeadstockBarsModel extends CopycatModel {
    public CopycatHeadstockBarsModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, RenderType renderType) {
        BakedModel model = getModelOf(material);
        List<BakedQuad> superQuads = originalModel.getQuads(state, side, rand, wrappedData, renderType);
        TextureAtlasSprite targetSprite = model.getParticleIcon(wrappedData);

        if (side != null && side.getAxis() == Direction.Axis.Y) {
            List<BakedQuad> templateQuads = model.getQuads(material, null, rand, wrappedData, renderType);
            for (int i = 0; i < templateQuads.size(); i++) {
                BakedQuad quad = templateQuads.get(i);
                if (quad.getDirection() != Direction.UP)
                    continue;
                targetSprite = quad.getSprite();
                break;
            }
        }

        if (targetSprite == null)
            return superQuads;

        List<BakedQuad> quads = new ArrayList<>();

        for (int i = 0; i < superQuads.size(); i++) {
            BakedQuad quad = superQuads.get(i);
            TextureAtlasSprite original = quad.getSprite();
            BakedQuad newQuad = BakedQuadHelper.clone(quad);
            int[] vertexData = newQuad.getVertices();
            for (int vertex = 0; vertex < 4; vertex++) {
                BakedQuadHelper.setU(vertexData, vertex, targetSprite
                    .getU(SpriteShiftEntry.getUnInterpolatedU(original, BakedQuadHelper.getU(vertexData, vertex))));
                BakedQuadHelper.setV(vertexData, vertex, targetSprite
                    .getV(SpriteShiftEntry.getUnInterpolatedV(original, BakedQuadHelper.getV(vertexData, vertex))));
            }
            quads.add(newQuad);
        }

        return quads;
    }
}
