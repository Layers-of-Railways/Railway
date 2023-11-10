package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerCTBehaviour;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.railwayteam.railways.util.TextUtils.joinSpace;
import static com.railwayteam.railways.util.TextUtils.joinUnderscore;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class CRPalettes {
    private static final CreateRegistrate REGISTRATE = Railways.registrate().creativeModeTab(
        () -> CRItems.palettesCreativeTab, "Create Steam 'n' Rails: Palettes"
    );

    public static void register() { // registration order is important for a clean inventory layout
        for (Styles style : Styles.values())
            style.register(null);

        for (DyeColor dyeColor : DyeColor.values()) {
            for (Styles style : Styles.values())
                style.register(dyeColor);
        }
    }

    public enum Styles {
        SLASHED(CRPalettes::slashedLocometal),
        RIVETED(CRPalettes::rivetedLocometal),
        PILLAR(CRPalettes::locometalPillar),
        SMOKEBOX(CRPalettes::locometalSmokebox),
        PLATED(CRPalettes::platedLocometal),
        FLAT_SLASHED(CRPalettes::flatSlashedLocometal),
        FLAT_RIVETED(CRPalettes::flatRivetedLocometal),
        BRASS_WRAPPED_SLASHED(CRPalettes::brassWrappedLocometal),
        BOILER(CRPalettes::locometalBoiler),
        BRASS_WRAPPED_BOILER(CRPalettes::brassWrappedLocometalBoiler)
        ;

        private final Map<@Nullable DyeColor, BlockEntry<?>> blocks = new HashMap<>(17, 2);
        private final PaletteBlockRegistrar registrar;

        Styles(PaletteBlockRegistrar registrar) {
            this.registrar = registrar;
        }
        private void register(@Nullable DyeColor dyeColor) {
            blocks.put(dyeColor, registrar.register(dyeColor));
        }

        public BlockEntry<?> get(@Nullable DyeColor color) {
            return blocks.get(color);
        }
    }

    @FunctionalInterface
    private interface PaletteBlockRegistrar {
        @ApiStatus.NonExtendable
        default BlockEntry<?> register(@Nullable DyeColor color) {
            String colorString = color == null ? "" : color.name().toLowerCase(Locale.ROOT);
            return register(color, colorString, color == null ? "" : ColorUtils.coloredName(colorString));
        }

        @ApiStatus.OverrideOnly
        BlockEntry<?> register(@Nullable DyeColor color, String colorString, String colorName);
    }

    // no textures for these yet
//            COPPER_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
//            COPPER_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class),
//            IRON_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class),
//            IRON_WRAPPED_LOCOMETAL_BOILER = new EnumMap<>(DyeColor.class);

    private static BlockEntry<?> slashedLocometal(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "slashed_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.SLASHED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Slashed Locometal"))
            .item()
            .build()
            .register();
    }

    private static BlockEntry<?> rivetedLocometal(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "riveted_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "riveted"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.RIVETED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Riveted Locometal"))
            .item()
            .build()
            .register();
    }

    private static BlockEntry<?> locometalPillar(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_pillar"), RotatedPillarBlock::new)
            .transform(BuilderTransformers.locoMetalPillar(color))
            .lang(joinSpace(colorName, "Locometal Pillar"))
            .item()
            .build()
            .register();
    }

    public static BlockEntry<?> locometalSmokebox(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_smokebox"), RotatedPillarBlock::new)
            .transform(BuilderTransformers.locoMetalSmokeBox(color))
            .lang(joinSpace(colorName, "Locometal Smokebox"))
            .item()
            .build()
            .register();
    }

    public static BlockEntry<?> platedLocometal(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "plated_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "sheeting"))
            .lang(joinSpace("Plated", colorName, "Locometal"))
            .item()
            .build()
            .register();
    }

    public static BlockEntry<?> flatSlashedLocometal(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "flat_slashed_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "annexed_slashed"))
            .lang(joinSpace("Flat", colorName, "Slashed Locometal"))
            .item()
            .build()
            .register();
    }

    public static BlockEntry<?> flatRivetedLocometal(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "flat_riveted_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "annexed_riveted"))
            .lang(joinSpace("Flat", colorName, "Riveted Locometal"))
            .item()
            .build()
            .register();
    }

    public static BlockEntry<?> brassWrappedLocometal(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "brass_wrapped_locometal"), Block::new)
            .transform(BuilderTransformers.locoMetalBase(color, "wrapped_slashed"))
            .onRegister(connectedTextures(() -> new SimpleCTBehaviour(CRSpriteShifts.BRASS_WRAPPED_LOCOMETAL.get(color))))
            .lang(joinSpace(colorName, "Brass Wrapped Locometal"))
            .item()
            .build()
            .register();
    }

    public static BlockEntry<?> locometalBoiler(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "locometal_boiler"), BoilerBlock::new)
            .transform(BuilderTransformers.locoMetalBoiler(color))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Locometal Boiler"))
            .item()
            .transform(customItemModel(joinUnderscore(colorString, "locometal_boiler_gullet_x")))
            .register();
    }

    public static BlockEntry<?> brassWrappedLocometalBoiler(@Nullable DyeColor color, String colorString, String colorName) {
        return REGISTRATE.block(joinUnderscore(colorString, "brass_wrapped_locometal_boiler"), BoilerBlock::new)
            .transform(BuilderTransformers.locoMetalBoiler(color))
            .onRegister(connectedTextures(() -> new BoilerCTBehaviour(CRSpriteShifts.BRASS_WRAPPED_BOILER_SIDE.get(color))))
            .lang(joinSpace(colorName, "Brass Wrapped Locometal Boiler"))
            .item()
            .transform(customItemModel(joinUnderscore(colorString, "brass_wrapped_locometal_boiler_gullet_x")))
            .register();
    }
}
