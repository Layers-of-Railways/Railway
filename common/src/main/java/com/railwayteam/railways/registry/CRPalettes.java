package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;

import java.util.EnumMap;

public class CRPalettes {
    private static final CreateRegistrate REGISTRATE = Railways.registrate().creativeModeTab(
            () -> CRItems.palettesCreativeTab, "Steam 'n' Rails Palettes"
    );

    public static final EnumMap<DyeColor, BlockEntry<Block>> SLASHED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> RIVETED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> LOCOMETAL_PILLAR = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> LOCOMETAL_SMOKEBOX = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> PLATED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> FLAT_SLASHED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> FLAT_RIVETED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> BRASS_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> BRASS_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> COPPER_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> COPPER_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> IRON_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, BlockEntry<Block>> IRON_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            String colorString = color.toString();
            String capsName = colorString.substring(0, 1).toUpperCase() + colorString.substring(1);
            // Slashed Loco-metal
            SLASHED_LOCOMETAL.put(color,
                    REGISTRATE.block("slashed_" + colorString + "_locometal", Block::new)
                            .properties(p -> p
                                    .color(ColorUtils.materialColorFromDye(color))
                                    .sound(SoundType.NETHERITE_BLOCK)
                            )
                            .register()
            );
        }
    }

    public static void register() {}
}
