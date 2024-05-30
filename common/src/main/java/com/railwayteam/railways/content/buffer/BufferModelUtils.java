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

package com.railwayteam.railways.content.buffer;

import com.jozufozu.flywheel.core.StitchedSprite;
import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.function.UnaryOperator;

@Environment(EnvType.CLIENT)
public class BufferModelUtils {
    public static final StitchedSprite SPRUCE_PLANKS_TEMPLATE = new StitchedSprite(new ResourceLocation("block/spruce_planks"));
    public static final StitchedSprite BIG_BUFFER_TEMPLATE = new StitchedSprite(Railways.asResource("block/buffer/big_buffer"));
    public static final StitchedSprite SMALL_BUFFER_TEMPLATE = new StitchedSprite(Railways.asResource("block/buffer/small_buffer"));
    public static final StitchedSprite SMALL_BUFFER_MONORAIL_TEMPLATE = new StitchedSprite(Railways.asResource("block/buffer/small_buffer_monorail"));
    public static final EnumMap<DyeColor, StitchedSprite> BIG_BUFFER_COLORS = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, StitchedSprite> SMALL_BUFFER_COLORS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            BIG_BUFFER_COLORS.put(color, new StitchedSprite(Railways.asResource("block/buffer/big_buffer/big_buffer_" + color.getName())));
            SMALL_BUFFER_COLORS.put(color, new StitchedSprite(Railways.asResource("block/buffer/small_buffer/small_buffer_" + color.getName())));
        }
    }

    public static UnaryOperator<TextureAtlasSprite> getSwapper(@Nullable BlockState planksState) {
        if (planksState == null) return sprite -> null;
        Block planksBlock = planksState.getBlock();
        ResourceLocation id = RegisteredObjects.getKeyOrThrow(planksBlock);
        String path = id.getPath();

        if (path.endsWith("_planks")) {
            return sprite -> {
                if (sprite == SPRUCE_PLANKS_TEMPLATE.get()) {
                    return getSpriteOnSide(planksState, Direction.UP);
                }
                return null;
            };
        } else {
            return sprite -> null;
        }
    }

    @SafeVarargs
    public static UnaryOperator<TextureAtlasSprite> combineSwappers(@Nullable UnaryOperator<TextureAtlasSprite>... swappers) {
        return sprite -> {
            for (UnaryOperator<TextureAtlasSprite> swapper : swappers) {
                if (swapper == null) continue;
                TextureAtlasSprite newSprite = swapper.apply(sprite);
                if (newSprite != null) {
                    return newSprite;
                }
            }
            return null;
        };
    }

    public static UnaryOperator<TextureAtlasSprite> getSwapper(@Nullable DyeColor color) {
        if (color == null) return sprite -> null;
        return sprite -> {
            if (sprite == SMALL_BUFFER_TEMPLATE.get() || sprite == SMALL_BUFFER_MONORAIL_TEMPLATE.get()) {
                return SMALL_BUFFER_COLORS.get(color).get();
            }
            if (sprite == BIG_BUFFER_TEMPLATE.get()) {
                return BIG_BUFFER_COLORS.get(color).get();
            }
            return null;
        };
    }

    private static TextureAtlasSprite getSpriteOnSide(BlockState state, Direction side) {
        BakedModel model = Minecraft.getInstance()
            .getBlockRenderer()
            .getBlockModel(state);
        if (model == null)
            return null;
        RandomSource random = RandomSource.create();
        random.setSeed(42L);
        List<BakedQuad> quads = model.getQuads(state, side, random);
        if (!quads.isEmpty()) {
            return quads.get(0)
                .getSprite();
        }
        random.setSeed(42L);
        quads = model.getQuads(state, null, random);
        if (!quads.isEmpty()) {
            for (BakedQuad quad : quads) {
                if (quad.getDirection() == side) {
                    return quad.getSprite();
                }
            }
        }
        return model.getParticleIcon();
    }

    // ensure that sprites get loaded
    public static void register() {}
}
