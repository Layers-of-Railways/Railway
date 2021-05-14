package com.railwayteam.railways.entities.conductor.engineers_cap;

import com.railwayteam.railways.entities.conductor.ConductorEntity;
import com.railwayteam.railways.items.EngineersCapItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class EngineersCapModel extends AnimatedGeoModel<EngineersCapItem> {
    public ResourceLocation getConductorEntityModel() {
        return new ResourceLocation("railways", "geo/engineers_cap_conductor.geo.json");
    }

    @Override
    public ResourceLocation getModelLocation(EngineersCapItem object)
    {
        return new ResourceLocation("railways", "geo/engineers_cap.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EngineersCapItem object)
    {
        return new ResourceLocation("railways", "textures/models/armor/" + object.color.getTranslationKey() + "_golem_hat.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EngineersCapItem object)
    {
        return new ResourceLocation("railways", "animations/engineers_cap.animation.json");
    }
}
