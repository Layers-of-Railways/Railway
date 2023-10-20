package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.content.palettes.boiler.Boiler;
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
            BRASS_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);
    // no textures for these yet
//            COPPER_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
//            COPPER_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class),
//            IRON_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
//            IRON_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);

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
            LOCOMETAL_SMOKEBOX.put(color,
                    REGISTRATE.block(colorString + "locometal_smokebox", RotatedPillarBlock::new)
                            .transform(BuilderTransformers.locoMetalSmokeBox(color))
                            .lang(colorName + " Locometal Smokebox")
                            .item()
                            .build()
                            .register()
            );

            // Plated Locometal
            PLATED_LOCOMETAL.put(color,
                    REGISTRATE.block(colorString + "_plated_locometal", Block::new)
                            .transform(BuilderTransformers.locoMetalBase(color, "sheeting"))
                            .lang("Plated " + colorName + " Locometal")
                            .item()
                            .build()
                            .register()
            );

            // Flat Slashed Locometal
            FLAT_SLASHED_LOCOMETAL.put(color,
                    REGISTRATE.block(colorString + "_flat_slashed_locometal", Block::new)
                            .transform(BuilderTransformers.locoMetalBase(color, "annexed_slashed"))
                            .lang("Flat " + colorName + " Slashed Locometal")
                            .item()
                            .build()
                            .register()
            );

            // Flat Riveted Locometal
            FLAT_RIVETED_LOCOMETAL.put(color,
                    REGISTRATE.block(colorString + "_flat_riveted_locometal", Block::new)
                            .transform(BuilderTransformers.locoMetalBase(color, "annexed_riveted"))
                            .lang("Flat " + colorName + " Riveted Locometal")
                            .item()
                            .build()
                            .register()
            );

            // Brass Wrapped Locometal
            BRASS_WRAPPED_LOCOMETAL.put(color,
                    REGISTRATE.block(colorString + "_brass_wrapped_locometal", Block::new)
                            .transform(BuilderTransformers.locoMetalBase(color, "wrapped_slashed"))
                            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.BRASS_WRAPPED_LOCOMETAL.get(color))))
                            .lang(colorName + " Brass Wrapped Locometal")
                            .item()
                            .build()
                            .register()
            );

            // Locometal Boiler
            LOCOMETAL_BOILER.put(color,
                    REGISTRATE.block(colorString + "_locometal_boiler", Boiler::new)
                            .transform(BuilderTransformers.locoMetalBoiler(color))
                            .lang(colorName + " Locometal Boiler")
                            .item()
                            .build()
                            .register()
            );
        }
    }

    public static void register() {}
}
