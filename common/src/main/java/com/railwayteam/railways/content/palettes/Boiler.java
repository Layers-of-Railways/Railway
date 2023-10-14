package com.railwayteam.railways.content.palettes;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Boiler extends Block implements IWrenchable {
    public Boiler(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return IWrenchable.super.onWrenched(state, context);
    }
}
