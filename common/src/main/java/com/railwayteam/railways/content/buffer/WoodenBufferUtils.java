package com.railwayteam.railways.content.buffer;

import com.jozufozu.flywheel.core.StitchedSprite;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.UnaryOperator;

@Environment(EnvType.CLIENT)
public class WoodenBufferUtils {
    public static final StitchedSprite SPRUCE_PLANKS_TEMPLATE = new StitchedSprite(new ResourceLocation("block/spruce_planks"));

    public static UnaryOperator<TextureAtlasSprite> getSwapper(BlockState planksState) {
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

    // ensure that sprite gets loaded
    public static void register() {}
}
