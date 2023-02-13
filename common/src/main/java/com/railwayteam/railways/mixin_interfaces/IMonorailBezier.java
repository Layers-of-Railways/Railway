package com.railwayteam.railways.mixin_interfaces;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.simibubi.create.foundation.utility.Couple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;

public interface IMonorailBezier {

    @Environment(EnvType.CLIENT)
    MonorailAngles[] getBakedMonorails();

    @Environment(EnvType.CLIENT)
    class MonorailAngles {
        public Pose beam;
        public Couple<Pose> beamCaps;
        public BlockPos lightPosition;
    }
}
