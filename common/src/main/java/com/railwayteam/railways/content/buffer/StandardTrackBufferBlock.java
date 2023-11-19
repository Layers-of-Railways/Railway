package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.Railways;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StandardTrackBufferBlock extends TrackBufferBlock {
    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);
    public StandardTrackBufferBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(STYLE, Style.STANDARD));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(STYLE));
    }

    @Override
    protected BlockState getCycledStyle(BlockState originalState, Direction targetedFace) {
        return originalState.cycle(STYLE);
    }

    public enum Style implements StringRepresentable {
        STANDARD("buffer_stop"),
        SHORT("buffer_stop_short_support")
        ;

        private final String model;
        Style(String model) {
            this.model = model;
        }

        public ResourceLocation getModel() {
            return Railways.asResource("block/buffer/" + model);
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
