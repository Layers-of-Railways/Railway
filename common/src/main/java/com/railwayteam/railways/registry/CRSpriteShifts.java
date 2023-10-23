package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class CRSpriteShifts {
    public static final Map<DyeColor, CTSpriteShiftEntry>
            SLASHED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            RIVETED_LOCOMETAL = new EnumMap<>(DyeColor.class),
            BRASS_WRAPPED_LOCOMETAL = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            SLASHED_LOCOMETAL.put(color, locometal(color, "slashed"));
            RIVETED_LOCOMETAL.put(color, locometal(color, "riveted"));
            BRASS_WRAPPED_LOCOMETAL.put(color, locometal(color, "wrapped_slashed"));
        }
    }


    //
    private static CTSpriteShiftEntry locometal(DyeColor color, String name) {
        return omni("palettes/" + color.getName().toLowerCase(Locale.ROOT) + "/" + name);
    }

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name);
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
