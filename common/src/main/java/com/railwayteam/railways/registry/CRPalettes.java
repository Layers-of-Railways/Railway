package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;

import java.util.EnumMap;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;

public class CRPalettes {
    private static final CreateRegistrate REGISTRATE = Railways.registrate().creativeModeTab(
            () -> CRItems.palettesCreativeTab, "Create Steam 'n' Rails: Palettes"
    );

    public static final EnumMap<DyeColor, BlockEntry<?>>
            SLASHED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            RIVETED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            LOCOMETAL_PILLAR = new EnumMap<>(DyeColor.class),
            LOCOMETAL_SMOKEBOX = new EnumMap<>(DyeColor.class),
            PLATED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            FLAT_SLASHED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            FLAT_RIVETED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class),
            BRASS_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            BRASS_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class),
            COPPER_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            COPPER_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class),
            IRON_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            IRON_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            String colorString = color.name().toLowerCase();
            String colorName = ColorUtils.coloredName(colorString);

            // Slashed Locometal
            SLASHED_LOCOMETAL.put(color,
                    REGISTRATE.block(colorString + "_slashed_locometal", Block::new)
                            .transform(BuilderTransformers.locoMetalBase(color, "slashed"))
                            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.SLASHED_LOCOMETAL.get(color))))
                            .lang(colorName + " Slashed Locometal")
                            .item()
                            .build()
                            .register()
            );

            // Riveted Locometal
            RIVETED_LOCOMETAL.put(color,
                    REGISTRATE.block(colorString + "_riveted_locometal", Block::new)
                            .transform(BuilderTransformers.locoMetalBase(color, "riveted"))
                            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.RIVETED_LOCOMETAL.get(color))))
                            .lang(colorName + " Riveted Locometal")
                            .item()
                            .build()
                            .register()
            );

            // Locometal Pillar
            LOCOMETAL_PILLAR.put(color,
                    REGISTRATE.block(colorString + "_locometal_pillar", RotatedPillarBlock::new)
                            .transform(BuilderTransformers.locoMetalPillar(color))
                            .lang(colorName + " Locometal Pillar")
                            .item()
                            .build()
                            .register()
            );

            // Locometal SmokeBox
            // LOCOMETAL_SMOKEBOX.put

            PLATED_LOCOMETAL.put(color,
                    REGISTRATE.block(colorString + "_plated_locometal", RotatedPillarBlock::new)
                            .transform(BuilderTransformers.locoMetalSmokeBox(color))
                            .lang(colorName + " Plated Locometal")
                            .item()
                            .build()
                            .register()
            );
        }
    }

    public static void register() {}
}
