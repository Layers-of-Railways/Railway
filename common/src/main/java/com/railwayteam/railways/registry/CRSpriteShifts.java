package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;

public class CRSpriteShifts {
    public static final CTSpriteShiftEntry FUEL_TANK = getCT(AllCTTypes.RECTANGLE, "fuel_tank"),
            FUEL_TANK_TOP = getCT(AllCTTypes.RECTANGLE, "fuel_tank_top"),
            FUEL_TANK_INNER = getCT(AllCTTypes.RECTANGLE, "fuel_tank_inner");


    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, Railways.asResource("block/" + blockTextureName),
                Railways.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }
}