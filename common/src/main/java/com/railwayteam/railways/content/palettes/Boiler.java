package com.railwayteam.railways.content.palettes;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class Boiler extends Block implements IWrenchable {
    //public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class)

    public Boiler(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return IWrenchable.super.onWrenched(state, context);
    }

    enum Style {
        GULLET, SMOKEBOX,
    }
}
