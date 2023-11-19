package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MonoTrackBufferBlock extends WoodVariantTrackBufferBlock {
    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);
    public static final BooleanProperty UPSIDE_DOWN = MonoBogeyBlock.UPSIDE_DOWN;

    public MonoTrackBufferBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(STYLE, Style.STANDARD).setValue(UPSIDE_DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(STYLE, UPSIDE_DOWN));
    }

    @Override
    protected BlockState getCycledStyle(BlockState originalState, Direction targetedFace) {
        return originalState.cycle(STYLE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        boolean hanging = context.getClickedFace() == Direction.DOWN;
        Style style = context.getClickedFace().getAxis().isHorizontal() ? Style.SIDE : Style.STANDARD;
        return state.setValue(UPSIDE_DOWN, hanging).setValue(STYLE, style);
    }

    public enum Style implements StringRepresentable {
        STANDARD("monorail_buffer_stop"),
        MONO("monorail_mono_buffer_stop"),
        SIDE("monorail_side_buffer_stop"),
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
