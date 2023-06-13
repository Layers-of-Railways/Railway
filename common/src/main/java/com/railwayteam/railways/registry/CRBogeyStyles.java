package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyRenderer;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.resources.ResourceLocation;

public class CRBogeyStyles {

    public static final BogeyStyle MONOBOGEY = create("monobogey", "monobogey")
        .displayName(Components.translatable("railways.bogeys.styles.monobogey"))
        .size(BogeySizes.SMALL, () -> () -> new MonoBogeyRenderer.SmallMonoBogeyRenderer(), CRBlocks.MONO_BOGEY)
        .build();

    public static AllBogeyStyles.BogeyStyleBuilder create(String name, String cycleGroup) {
        return create(Railways.asResource(name), Railways.asResource(cycleGroup));
    }

    public static AllBogeyStyles.BogeyStyleBuilder create(ResourceLocation name, ResourceLocation cycleGroup) {
        return new AllBogeyStyles.BogeyStyleBuilder(name, cycleGroup);
    }

    public static void register() {
        Railways.LOGGER.info("Registered bogey styles from " + Railways.MODID);
    }
}