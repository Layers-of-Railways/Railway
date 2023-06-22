package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.CRBogeyRenderer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.CubeParticleData;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import static com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerClient.registerStyleCategory;
import static com.simibubi.create.AllBogeyStyles.STANDARD_CYCLE_GROUP;

public class CRBogeyStyles {

    public static final BogeyStyle MONOBOGEY = create("monobogey", "monobogey")
        .displayName(Components.translatable("railways.bogeys.styles.monobogey"))
        .size(BogeySizes.SMALL, () -> MonoBogeyRenderer.SmallMonoBogeyRenderer::new, CRBlocks.MONO_BOGEY)
        .build();

    public static final BogeyStyle INVISIBLE = create("invisible", Create.asResource(STANDARD_CYCLE_GROUP))
            .displayName(Components.translatable("railways.bogeys.styles.invisible"))
            .size(BogeySizes.SMALL, () -> InvisibleBogeyRenderer::new, CRBlocks.INVISIBLE_BOGEY)
            .contactParticle(new CubeParticleData())
            .build();

    public static final String SINGLEAXLE_CYCLE_GROUP = "singleaxles";
    public static final String DOUBLEAXLE_CYCLE_GROUP = "doubleaxles";
    public static final String TRIPLEAXLE_CYCLE_GROUP = "tripleaxles";

    // Single Axles
    public static final BogeyStyle
            SINGLEAXLE = create("singleaxle", SINGLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.SingleaxleBogeyRenderer::new, CRBlocks.SINGLEAXLE_BOGEY)
                    .build(),
            LEAFSPRING = create("leafspring", SINGLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.LeafspringBogeyRenderer::new, CRBlocks.SINGLEAXLE_BOGEY)
                    .build(),
            COILSPRING = create("coilspring", SINGLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.CoilspringBogeyRenderer::new, CRBlocks.SINGLEAXLE_BOGEY)
                    .build()
    ;

    // Double Axles
    public static final BogeyStyle
            FREIGHT = create("freight", DOUBLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.FreightBogeyRenderer::new, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY)
                    .build(),
            ARCHBAR = create("archbar", DOUBLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.ArchbarBogeyRenderer::new, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY)
                    .build(),
            PASSENGER = create("passenger", DOUBLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.PassengerBogeyRenderer::new, CRBlocks.DOUBLEAXLE_BOGEY)
                    .build(),
            MODERN = create("modern", DOUBLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.ModernBogeyRenderer::new, CRBlocks.DOUBLEAXLE_BOGEY)
                    .build(),
            BLOMBERG = create("blomberg", DOUBLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.BlombergBogeyRenderer::new, CRBlocks.DOUBLEAXLE_BOGEY)
                    .build(),
            Y25 = create("y25", DOUBLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.Y25BogeyRenderer::new, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY)
                    .build()
    ;

    // Triple Axles
    public static final BogeyStyle
            HEAVYWEIGHT = create("heavyweight", TRIPLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.HeavyweightBogeyRenderer::new, CRBlocks.TRIPLEAXLE_BOGEY)
                    .build(),
            RADIAL = create("radial", TRIPLEAXLE_CYCLE_GROUP)
                    .size(BogeySizes.SMALL, () -> CRBogeyRenderer.RadialBogeyRenderer::new, CRBlocks.TRIPLEAXLE_BOGEY)
                    .build()
    ;


    public static AllBogeyStyles.BogeyStyleBuilder create(String name, String cycleGroup) {
        return create(Railways.asResource(name), Railways.asResource(cycleGroup))
                .displayName(Components.translatable("railways.bogeys.styles."+name));
    }

    public static AllBogeyStyles.BogeyStyleBuilder create(String name, ResourceLocation cycleGroup) {
        return create(Railways.asResource(name), cycleGroup);
    }

    public static AllBogeyStyles.BogeyStyleBuilder create(ResourceLocation name, ResourceLocation cycleGroup) {
        return new AllBogeyStyles.BogeyStyleBuilder(name, cycleGroup);
    }

    public static void register() {
        Railways.LOGGER.info("Registered bogey styles from " + Railways.MODID);
        registerStyleCategory(Create.asResource(STANDARD_CYCLE_GROUP), AllBlocks.COGWHEEL);
        registerStyleCategory(SINGLEAXLE_CYCLE_GROUP, CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.RED));
        registerStyleCategory(DOUBLEAXLE_CYCLE_GROUP, CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.ORANGE));
        registerStyleCategory(TRIPLEAXLE_CYCLE_GROUP, CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.YELLOW));
    }
}