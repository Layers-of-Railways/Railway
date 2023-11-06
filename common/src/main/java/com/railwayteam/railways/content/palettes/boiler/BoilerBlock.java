package com.railwayteam.railways.content.palettes.boiler;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class BoilerBlock extends Block implements IWrenchable {
    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);

    public BoilerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(STYLE, Style.GULLET));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE);
    }

    @Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        return IWrenchable.super.updateAfterWrenched(newState, context).cycle(STYLE);
    }

    public enum Style implements StringRepresentable {
        GULLET, SMOKEBOX;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}
