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
import java.util.Map;

public class CRSpriteShifts {
    //public static final Map<DyeColor, SpriteShiftEntry> DYED_BELTS = new EnumMap<>(DyeColor.class);

//    public static final CTSpriteShiftEntry FUEL_TANK = getCT(AllCTTypes.RECTANGLE, "fuel_tank"),
//            FUEL_TANK_TOP = getCT(AllCTTypes.RECTANGLE, "fuel_tank_top"),
//            FUEL_TANK_INNER = getCT(AllCTTypes.RECTANGLE, "fuel_tank_inner");

    //

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
