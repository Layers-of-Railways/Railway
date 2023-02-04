package com.railwayteam.railways.mixin_interfaces;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IMonorailBezier {

    @OnlyIn(Dist.CLIENT)
    MonorailAngles[] getBakedMonorails();

    @OnlyIn(Dist.CLIENT)
    class MonorailAngles {
        public Pose beam;
        public Couple<Pose> beamCaps;
        public BlockPos lightPosition;
    }
}
