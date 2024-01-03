package com.railwayteam.railways.content.legacy.selection_menu;

import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Deprecated(forRemoval = true)
public class BogeyCategoryHandlerServer {
    public static Map<UUID, Pair<BogeyStyle, @Nullable BogeySize>> selectedStyles = new HashMap<>();

    public static Pair<BogeyStyle, @Nullable BogeySize> getStyle(UUID uuid) {
        if (selectedStyles.containsKey(uuid))
            return selectedStyles.get(uuid);
        return Pair.of(AllBogeyStyles.STANDARD, BogeySizes.SMALL);
    }

    @Nullable
    public static UUID currentPlayer = null;
}
