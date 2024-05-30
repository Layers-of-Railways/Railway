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

package com.railwayteam.railways.content.custom_tracks.casing;

import com.railwayteam.railways.Railways;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpriteCopyingBakedModel implements BakedModel {

  protected final BakedModel baseModel;
  protected final BakedModel spriteSourceModel;

  public SpriteCopyingBakedModel(BakedModel baseModel, BakedModel spriteSourceModel) {
    this.baseModel = baseModel;
    this.spriteSourceModel = spriteSourceModel;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pSide, RandomSource pRand) {
    ArrayList<BakedQuad> quads = new ArrayList<>();
    TextureAtlasSprite overrideSprite = spriteSourceModel.getParticleIcon();
    BakedQuad overrideQuad = null;
    List<BakedQuad> sourceQuads = spriteSourceModel.getQuads(pState, pSide, pRand);
    if (!sourceQuads.isEmpty()) {
      overrideSprite = sourceQuads.get(0).getSprite();
      overrideQuad = sourceQuads.get(0);
      //Railways.LOGGER.warn("Overridesprite: "+ overrideSprite.toString());
    } else if (pSide != null) {
      List<BakedQuad> nullQuads = spriteSourceModel.getQuads(pState, null, pRand);
      if (!nullQuads.isEmpty()) {
        overrideSprite = nullQuads.get(0).getSprite();
        overrideQuad = nullQuads.get(0);
      }
    }
    for (BakedQuad quad : baseModel.getQuads(pState, pSide, pRand)) {
      if (overrideSprite == null || overrideQuad == null) {
        Railways.LOGGER.error("No overriding sprites found for side "+(pSide==null?"null":pSide.toString())+" blockstate: "+(pState==null?"null":pState.toString()));
      }
      quads.add(new BakedQuad(transformVertices(quad.getVertices(), quad.getSprite(), (overrideQuad!=null?overrideQuad:quad)), quad.getTintIndex(), quad.getDirection(),
          overrideSprite != null ? overrideSprite : quad.getSprite(), true));
    }
    return quads;
  }

  private int[] transformVertices(int[] baseVertices, TextureAtlasSprite baseSprite, BakedQuad uvSource) {
    TextureAtlasSprite goalSprite = uvSource.getSprite();
    int[] newVertices = baseVertices.clone();
    for (int i = 0; i < baseVertices.length; i += 8) {
      newVertices[i + 4] = Float.floatToRawIntBits(Float.intBitsToFloat(baseVertices[i + 4]) - baseSprite.getU0() + goalSprite.getU0());
      newVertices[i + 5] = Float.floatToRawIntBits(Float.intBitsToFloat(baseVertices[i + 5]) - baseSprite.getV0() + goalSprite.getV0());
    }
    return newVertices;
  }

  @Override
  public boolean useAmbientOcclusion() {
    return spriteSourceModel.useAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return spriteSourceModel.isGui3d();
  }

  @Override
  public boolean usesBlockLight() {
    return spriteSourceModel.usesBlockLight();
  }

  @Override
  public boolean isCustomRenderer() {
    return baseModel.isCustomRenderer();
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return spriteSourceModel.getParticleIcon();
  }

  @Override
  public ItemTransforms getTransforms() {
    return ItemTransforms.NO_TRANSFORMS;
  }

  @Override
  public ItemOverrides getOverrides() {
    return baseModel.getOverrides();
  }
}
