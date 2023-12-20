package com.railwayteam.railways.content.buffer.single_deco;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LinkPinBlock extends AbstractDyeableSingleBufferBlock {
    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);

    public LinkPinBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(STYLE, Style.LINK));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(STYLE));
    }

    @Override
    protected BlockState cycleStyle(BlockState originalState, Direction targetedFace) {
        return originalState.cycle(STYLE);
    }

    @Override
    protected VoxelShaper getShaper(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(STYLE)) {
            case LINK, LINKLESS -> CRShapes.LINK_PIN;
            case KNUCKLE, KNUCKLE_SPLIT -> CRShapes.KNUCKLE;
        };
    }

    public enum Style implements StringRepresentable {
        LINK("link_and_pin"),
        LINKLESS("link_and_pin_linkless"),
        KNUCKLE("knuckle_coupler"),
        KNUCKLE_SPLIT("split_knuckle_coupler")
        ;

        private final String model;
        Style(String model) {
            this.model = model;
        }

        public ResourceLocation getModel() {
            return Railways.asResource("block/buffer/single_deco/" + model);
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
