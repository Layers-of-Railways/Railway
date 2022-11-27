package com.railwayteam.railways.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.world.item.DyeColor;

import javax.annotation.Nullable;
import java.util.*;

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

  public record ModelTransform(double x, double y, double z, float rx, float ry, float rz) {
    public static final ModelTransform ZERO = new ModelTransform(0, 0, 0, 0, 0, 0);

    //                                                            pitch?    YAW       roll?
    public static ModelTransform of(double x, double y, double z, float rx, float ry, float rz) {
      return new ModelTransform(x, y, z, rx, ry, rz);
    }

    public static ModelTransform of(double x, double y, double z) {
      return of(x, y, z, 0, 0, 0);
    }

    public static ModelTransform of(double x, double z) {
      return of(x, 0, z);
    }
  }

  public static final class TrackCasingSpec {
    public final PartialModel model;
    public final ModelTransform transform;
    public final List<ModelTransform> additionalTransforms = new ArrayList<>();

    protected TrackCasingSpec altSpec;

    public TrackCasingSpec(PartialModel model, ModelTransform transform) {
      this.model = model;
      this.transform = transform;
    }

    public TrackCasingSpec addTransform(ModelTransform transform) {
      additionalTransforms.add(transform);
      return this;
    }

    public TrackCasingSpec addTransform(double x, double z) {
      return addTransform(ModelTransform.of(x, z));
    }

    public TrackCasingSpec withAltSpec(@Nullable TrackCasingSpec altSpec) {
      this.altSpec = altSpec;
      return this;
    }

    public TrackCasingSpec getAltSpec() {
      return altSpec!=null ? altSpec : this;
    }
  }

  public static final EnumMap<TrackShape, TrackCasingSpec> TRACK_CASINGS = new EnumMap<>(TrackShape.class);

  public static final PartialModel TRACK_CASING_FLAT = block("track_casing/flat");
  public static final PartialModel TRACK_CASING_FLAT_THICK = block("track_casing/flat_thick");

  static {
    PartialModel xo = block("track_casing/xo");
    PartialModel zo = block("track_casing/zo");
    PartialModel pd = block("track_casing/pd");
    PartialModel nd = block("track_casing/nd");
    PartialModel cr_o = block("track_casing/cr_o");

    TrackCasingSpec spec_3x3 = new TrackCasingSpec(TRACK_CASING_FLAT, ModelTransform.ZERO)
        .addTransform(1, 1).addTransform(0, 1).addTransform(-1, 1)
        .addTransform(1, 0).addTransform(-1, 0)
        .addTransform(1, -1).addTransform(0, -1).addTransform(-1, -1);

    TrackCasingSpec flat_x = new TrackCasingSpec(TRACK_CASING_FLAT, ModelTransform.ZERO)
        .addTransform(0, 1).addTransform(0, -1);
    TrackCasingSpec flat_z = new TrackCasingSpec(TRACK_CASING_FLAT, ModelTransform.ZERO)
        .addTransform(1, 0).addTransform(-1, 0);

    TRACK_CASINGS.put(TrackShape.XO, new TrackCasingSpec(xo, ModelTransform.ZERO).withAltSpec(flat_x));
    TRACK_CASINGS.put(TrackShape.ZO, new TrackCasingSpec(zo, ModelTransform.ZERO).withAltSpec(flat_z));


    TRACK_CASINGS.put(TrackShape.PD, new TrackCasingSpec(pd, ModelTransform.ZERO)
            .withAltSpec(new TrackCasingSpec(TRACK_CASING_FLAT, ModelTransform.ZERO)
                .addTransform(1, 0).addTransform(1, -1)
                .addTransform(0, 1).addTransform(-1, 1)
            ));
    TRACK_CASINGS.put(TrackShape.ND, new TrackCasingSpec(nd, ModelTransform.ZERO)
        .withAltSpec(new TrackCasingSpec(TRACK_CASING_FLAT, ModelTransform.ZERO)
            .addTransform(-1, 0).addTransform(-1, -1)
            .addTransform(0, 1).addTransform(1, 1)
        ));

    TRACK_CASINGS.put(TrackShape.TE, new TrackCasingSpec(xo, ModelTransform.ZERO).withAltSpec(flat_x));
    TRACK_CASINGS.put(TrackShape.TW, new TrackCasingSpec(xo, ModelTransform.ZERO).withAltSpec(flat_x));
    TRACK_CASINGS.put(TrackShape.TN, new TrackCasingSpec(zo, ModelTransform.ZERO).withAltSpec(flat_z));
    TRACK_CASINGS.put(TrackShape.TS, new TrackCasingSpec(zo, ModelTransform.ZERO).withAltSpec(flat_z));

    TRACK_CASINGS.put(TrackShape.CR_O, new TrackCasingSpec(cr_o, ModelTransform.ZERO).withAltSpec(spec_3x3));

    TRACK_CASINGS.put(TrackShape.CR_D, new TrackCasingSpec(TRACK_CASING_FLAT, ModelTransform.ZERO)
        .addTransform(1, 2).addTransform(0, 2).addTransform(-1, 2)
        .addTransform(2, 1).addTransform(1, 1).addTransform(0, 1).addTransform(-1, 1).addTransform(-2, 1)
        .addTransform(2, 0).addTransform(1, 0).addTransform(-1, 0).addTransform(-2, 0)
        .addTransform(1, -1).addTransform(0, -1).addTransform(-1, -1)
        .addTransform(0, -2)
        .withAltSpec(spec_3x3)
    );

    TRACK_CASINGS.put(TrackShape.CR_PDX, spec_3x3);
    TRACK_CASINGS.put(TrackShape.CR_PDZ, spec_3x3);
    TRACK_CASINGS.put(TrackShape.CR_NDX, spec_3x3);
    TRACK_CASINGS.put(TrackShape.CR_NDZ, spec_3x3);
  }

  /*public static ModelTransform getTransform() {
    return ModelTransform.of(-0.5d, 0, 0, 0, 45, 0);
  }*/

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
