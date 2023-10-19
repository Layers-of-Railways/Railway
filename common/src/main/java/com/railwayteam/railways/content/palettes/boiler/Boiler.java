package com.railwayteam.railways.content.palettes.boiler;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Boiler extends Block implements IWrenchable {
    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);

    public Boiler(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(STYLE, Style.GULLET));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        switch (state.getValue(STYLE)) {
            case GULLET -> state.setValue(STYLE, Style.SMOKEBOX);
            case SMOKEBOX -> state.setValue(STYLE, Style.GULLET);
        }

        return InteractionResult.SUCCESS;
    }

    public enum Style implements StringRepresentable {
        GULLET, SMOKEBOX;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}
