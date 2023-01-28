package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.railwayteam.railways.mixin.AccessorCarriageBogey;
import com.simibubi.create.content.logistics.trains.IBogeyBlock;
import com.simibubi.create.content.logistics.trains.entity.CarriageBogey;
import net.minecraft.world.level.block.state.BlockState;

public interface IPotentiallyUpsideDownBogeyBlock extends IBogeyBlock {
    boolean isUpsideDown();
    BlockState getVersion(BlockState base, boolean upsideDown);

    static boolean isUpsideDown(Object object) {
        if (object instanceof CarriageBogey bogey && isUpsideDown(((AccessorCarriageBogey)bogey).getType()))
            return true;
        return object instanceof IPotentiallyUpsideDownBogeyBlock pudb && pudb.isUpsideDown();
    }
}
