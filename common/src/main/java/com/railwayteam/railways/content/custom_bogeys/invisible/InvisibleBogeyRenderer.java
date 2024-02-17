package com.railwayteam.railways.content.custom_bogeys.invisible;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;

public class InvisibleBogeyRenderer extends BogeyRenderer {
    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) { }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) { }
}
