package com.railwayteam.railways.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.Map;

public class CRBlockPartials {

  public static final Map<DyeColor, PartialModel> TOOLBOX_BODIES = new EnumMap<>(DyeColor.class);

  private static PartialModel block(String path) {
    return new PartialModel(Create.asResource("block/" + path));
  }

  static {
    for (DyeColor color : DyeColor.values())
      TOOLBOX_BODIES.put(color, block(Lang.asId(color.name())+"_toolbox"));
  }

  public static void init() {
  }
}
