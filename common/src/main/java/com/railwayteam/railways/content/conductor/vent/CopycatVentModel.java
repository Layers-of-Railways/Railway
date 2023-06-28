package com.railwayteam.railways.content.conductor.vent;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public abstract class CopycatVentModel extends CopycatModel {
    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);
    public CopycatVentModel(BakedModel originalModel) {
        super(originalModel);
    }

    @ExpectPlatform
    public static CopycatVentModel create(BakedModel bakedModel) {
        throw new AssertionError();
    }
}
