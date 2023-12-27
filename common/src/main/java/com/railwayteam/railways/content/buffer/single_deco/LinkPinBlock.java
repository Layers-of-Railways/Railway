package com.railwayteam.railways.content.buffer.single_deco;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
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
public class LinkPinBlock extends AbstractDyeableSingleBufferBlock implements BlockStateBlockItemGroup.GroupedBlock {
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

    public enum Style implements StringRepresentable, BlockStateBlockItemGroup.IStyle<Void> {
        LINK("link_and_pin", "Link 'n Pin"),
        LINKLESS("link_and_pin_linkless", "Linkless Link 'n Pin"),
        KNUCKLE("knuckle_coupler", "Knuckle Coupler"),
        KNUCKLE_SPLIT("split_knuckle_coupler", "Split Knuckle Coupler")
        ;

        private final String model;
        private final String langName;
        Style(String model, String langName) {
            this.model = model;
            this.langName = langName;
        }

        public ResourceLocation getModel() {
            return Railways.asResource("block/buffer/single_deco/" + model);
        }


        @Override
        public ResourceLocation getModel(Void context) {
            return getModel();
        }

        @Override
        public String getBlockId(Void context) {
            return model;
        }

        @Override
        public String getLangName(Void context) {
            return langName;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return CRBlocks.LINK_AND_PIN_GROUP.get(state.getValue(STYLE)).asStack();
    }
}
