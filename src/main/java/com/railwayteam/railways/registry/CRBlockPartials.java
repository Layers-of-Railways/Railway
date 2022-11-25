package com.railwayteam.railways.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.Map;

public class CRBlockPartials {

  public static class TrackModelHolder {
    public final PartialModel tie;
    public final PartialModel segment_left;
    public final PartialModel segment_right;



    protected TrackModelHolder(PartialModel tie, PartialModel segment_left, PartialModel segment_right) {
      this.tie = tie;
      this.segment_left = segment_left;
      this.segment_right = segment_right;
    }
  }

  public static final Map<DyeColor, PartialModel> TOOLBOX_BODIES = new EnumMap<>(DyeColor.class);
  public static final Map<TrackMaterial, TrackModelHolder> TRACK_PARTS = new EnumMap<>(TrackMaterial.class);

  public static final PartialModel
      SEMAPHORE_ARM_RED=block("semaphore/red_arm"),
      SEMAPHORE_ARM_YELLOW=block("semaphore/yellow_arm"),
      SEMAPHORE_ARM_RED_FLIPPED=block("semaphore/red_arm_flipped"),
      SEMAPHORE_ARM_YELLOW_FLIPPED=block("semaphore/yellow_arm_flipped"),
      SEMAPHORE_ARM_RED_UPSIDE_DOWN=block("semaphore/red_arm_down"),
      SEMAPHORE_ARM_YELLOW_UPSIDE_DOWN=block("semaphore/yellow_arm_down"),
      SEMAPHORE_ARM_RED_FLIPPED_UPSIDE_DOWN=block("semaphore/red_arm_flipped_down"),
      SEMAPHORE_ARM_YELLOW_FLIPPED_UPSIDE_DOWN=block("semaphore/yellow_arm_flipped_down"),
      SEMAPHORE_LAMP_RED=block("semaphore/red_lamp"),
      SEMAPHORE_LAMP_YELLOW=block("semaphore/yellow_lamp"),
      SEMAPHORE_LAMP_WHITE=block("semaphore/white_lamp");

  public static final PartialModel TRACK_CASING = block("track_casing");

  private static PartialModel createBlock(String path) {
    return new PartialModel(Create.asResource("block/" + path));
  }

  private static PartialModel block(String path) {
    return new PartialModel(Railways.asResource("block/" + path));
  }

  static {
    for (DyeColor color : DyeColor.values())
      TOOLBOX_BODIES.put(color, createBlock(Lang.asId(color.name())+"_toolbox"));

    for (TrackMaterial material : TrackMaterial.allCustom()) {
      String prefix = "track/" + material.resName() + "/";
      TRACK_PARTS.put(material, new TrackModelHolder(block(prefix + "tie"), block(prefix + "segment_left"), block(prefix + "segment_right")));
    }
  }

  @SuppressWarnings("EmptyMethod")
  public static void init() {
  }
}
