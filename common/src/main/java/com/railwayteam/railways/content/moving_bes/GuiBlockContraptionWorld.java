package com.railwayteam.railways.content.moving_bes;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class GuiBlockContraptionWorld extends ContraptionWorld {
    public final Contraption contraption;
    public final BlockPos blockPos;

    public GuiBlockContraptionWorld(Level level, Contraption contraption, BlockPos blockPos) {
        super(level, contraption);
        this.contraption = contraption;
        this.blockPos = blockPos;
    }
}
