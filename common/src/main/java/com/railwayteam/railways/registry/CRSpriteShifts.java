package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CRSpriteShifts {
    public static final Map<@Nullable DyeColor, CTSpriteShiftEntry>
            SLASHED_LOCOMETAL = new HashMap<>(17, 2),
            RIVETED_LOCOMETAL = new HashMap<>(17, 2),
            BRASS_WRAPPED_LOCOMETAL = new HashMap<>(17, 2),
            BOILER_SIDE = new HashMap<>(17, 2),
            BRASS_WRAPPED_BOILER_SIDE = new HashMap<>(17, 2);
    
    private static void initLocometal(@Nullable DyeColor color) {
        SLASHED_LOCOMETAL.put(color, locometal(color, "slashed"));
        RIVETED_LOCOMETAL.put(color, locometal(color, "riveted"));
        BRASS_WRAPPED_LOCOMETAL.put(color, locometal(color, "wrapped_slashed"));
        BOILER_SIDE.put(color, locometalBoiler(color, "boiler_side"));
        BRASS_WRAPPED_BOILER_SIDE.put(color, locometalBoiler(color, "wrapped_boiler_side"));
    }

    static {
        initLocometal(null);
        for (DyeColor color : DyeColor.values()) {
            initLocometal(color);
        }
    }


    //
    private static CTSpriteShiftEntry locometal(@Nullable DyeColor color, String name) {
        String colorName = color == null ? "netherite" : color.name().toLowerCase(Locale.ROOT);
        return omni("palettes/" + colorName + "/" + name);
    }

    private static CTSpriteShiftEntry locometalBoiler(@Nullable DyeColor color, String name) {
        String colorName = color == null ? "netherite" : color.name().toLowerCase(Locale.ROOT);
        return horizontalKryppers("palettes/" + colorName + "/" + name);
    }

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name);
    }

    private static CTSpriteShiftEntry horizontalKryppers(String name) {
        return getCT(AllCTTypes.HORIZONTAL_KRYPPERS, name);
    }

    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }

    //

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Railways.asResource(originalLocation), Railways.asResource(targetLocation));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, Railways.asResource("block/" + blockTextureName),
                Railways.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    public static void register() {}
}
