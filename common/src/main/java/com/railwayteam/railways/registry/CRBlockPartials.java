package com.railwayteam.railways.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType.NARROW_GAUGE;
import static com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType.WIDE_GAUGE;

public class CRBlockPartials {

    public static final Map<DyeColor, PartialModel> TOOLBOX_BODIES = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, PartialModel> CONDUCTOR_WHISTLE_FLAGS = new EnumMap<>(DyeColor.class);
    public static final Map<String, PartialModel> CUSTOM_CONDUCTOR_CAPS = new HashMap<>();
    public static final Map<String, PartialModel> CUSTOM_CONDUCTOR_ONLY_CAPS = new HashMap<>();
    public static final Map<String, ResourceLocation> CUSTOM_CONDUCTOR_SKINS = new HashMap<>();
    public static final Set<String> NO_TILT_CAPS = new HashSet<>();
    public static final Map<String, ResourceLocation> CUSTOM_CONDUCTOR_SKINS_FOR_NAME = new HashMap<>(); // for if a conductor is renamed, rather than the cap

    public static void registerCustomCap(String itemName, String modelLoc) {
        CUSTOM_CONDUCTOR_CAPS.put(itemName, new PartialModel(Railways.asResource("item/dev_caps/"+modelLoc)));
    }

    public static void registerCustomCap(String itemName, String modelLoc, boolean preventTilting) {
        CUSTOM_CONDUCTOR_CAPS.put(itemName, new PartialModel(Railways.asResource("item/dev_caps/"+modelLoc)));
        if (preventTilting)
            NO_TILT_CAPS.add(itemName);
    }

    public static void registerCustomConductorOnlyCap(String itemName, String modelLoc) {
        CUSTOM_CONDUCTOR_ONLY_CAPS.put(itemName, new PartialModel(Railways.asResource("item/dev_caps/"+modelLoc)));
    }

    public static void registerCustomConductorOnlyCap(String itemName, String modelLoc, boolean preventTilting) {
        CUSTOM_CONDUCTOR_ONLY_CAPS.put(itemName, new PartialModel(Railways.asResource("item/dev_caps/"+modelLoc)));
        if (preventTilting)
            NO_TILT_CAPS.add(itemName);
    }

    public static boolean shouldPreventTiltingCap(String name) {
        return NO_TILT_CAPS.contains(name);
    }

    public static void registerCustomSkin(String itemName, String textureLoc) {
        CUSTOM_CONDUCTOR_SKINS.put(itemName, Railways.asResource("textures/entity/custom_conductors/"+textureLoc));
    }

    public static void registerCustomConductorNameBasedSkin(String conductorName, String textureLoc) {
        CUSTOM_CONDUCTOR_SKINS_FOR_NAME.put(conductorName, Railways.asResource("textures/entity/custom_conductors/"+textureLoc));
    }

    public static final PartialModel
        SEMAPHORE_ARM_RED = block("semaphore/red_arm"),
        SEMAPHORE_ARM_YELLOW = block("semaphore/yellow_arm"),
        SEMAPHORE_ARM_RED_FLIPPED = block("semaphore/red_arm_flipped"),
        SEMAPHORE_ARM_YELLOW_FLIPPED = block("semaphore/yellow_arm_flipped"),
        SEMAPHORE_ARM_RED_UPSIDE_DOWN = block("semaphore/red_arm_down"),
        SEMAPHORE_ARM_YELLOW_UPSIDE_DOWN = block("semaphore/yellow_arm_down"),
        SEMAPHORE_ARM_RED_FLIPPED_UPSIDE_DOWN = block("semaphore/red_arm_flipped_down"),
        SEMAPHORE_ARM_YELLOW_FLIPPED_UPSIDE_DOWN = block("semaphore/yellow_arm_flipped_down"),
        SEMAPHORE_LAMP_RED = block("semaphore/red_lamp"),
        SEMAPHORE_LAMP_YELLOW = block("semaphore/yellow_lamp"),
        SEMAPHORE_LAMP_WHITE = block("semaphore/white_lamp");

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

        private TrackCasingSpec altSpec;
        private final Map<TrackType, TrackCasingSpec> specsByTrackType = new HashMap<>();
        private final int topSurfacePixelHeight;

        private double xShift = 0; // for track pads
        private double zShift = 0;

        private boolean shiftSet = false;
        
        public TrackCasingSpec(PartialModel model, int topSurfacePixelHeight) {
            this(model, ModelTransform.ZERO, topSurfacePixelHeight);
        }

        public TrackCasingSpec(PartialModel model, ModelTransform transform, int topSurfacePixelHeight) {
            this.model = model;
            this.transform = transform;
            this.topSurfacePixelHeight = topSurfacePixelHeight;
        }

        public TrackCasingSpec getFor(@Nullable TrackType type) {
            if (type == null || !specsByTrackType.containsKey(type))
                return this;
            return specsByTrackType.get(type);
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
            return altSpec != null ? altSpec : this;
        }

        public int getTopSurfacePixelHeight(boolean alt) {
            return (alt && altSpec != null) ? altSpec.topSurfacePixelHeight : topSurfacePixelHeight;
        }

        @Nullable
        public TrackCasingSpec getAltSpec(@Nullable TrackType type) {
            if (type == null) {
                return getAltSpec();
            } else if (altSpec != null && altSpec.specsByTrackType.containsKey(type)) {
                return altSpec.specsByTrackType.get(type);
            } else if (specsByTrackType.containsKey(type) && specsByTrackType.get(type).altSpec != null) {
                return specsByTrackType.get(type).altSpec;
            } else {
                return null;
            }
        }

        public TrackCasingSpec getNonNullAltSpec(@Nullable TrackType type) {
            TrackCasingSpec spec = getAltSpec(type);
            if (spec == null)
                spec = getAltSpec();
            return spec;
        }

        public int getTopSurfacePixelHeight(@Nullable TrackType type, boolean alt) {
            if (type == null)
                return getTopSurfacePixelHeight(alt);
            TrackCasingSpec altSpec;
            if (alt && (altSpec = getAltSpec(type)) != null) {
                return altSpec.topSurfacePixelHeight;
            } else {
                return getTopSurfacePixelHeight(alt);
            }
        }

        public TrackCasingSpec withTrackType(@NotNull TrackType type, @Nullable TrackCasingSpec spec) {
            if (spec == null) {
                specsByTrackType.remove(type);
            } else {
                specsByTrackType.put(type, spec);
            }
            return this;
        }

        public TrackCasingSpec overlayShift(double x, double z) {
            this.xShift = x;
            this.zShift = z;
            shiftSet = true;
            return this;
        }

        public double getXShift() {
            return xShift;
        }

        public double getZShift() {
            return zShift;
        }

        public double getXShift(@Nullable TrackType type) {
            return getFor(type).shiftSet ? getFor(type).getXShift() : getXShift();
        }

        public double getZShift(@Nullable TrackType type) {
            return getFor(type).shiftSet ? getFor(type).getZShift() : getZShift();
        }

        /**
         * @return a copy of this spec, without other gauges etc
         */
        public TrackCasingSpec copy() {
            TrackCasingSpec copy = new TrackCasingSpec(this.model, this.transform, this.topSurfacePixelHeight);
            copy.xShift = xShift;
            copy.zShift = zShift;
            copy.shiftSet = shiftSet;

            copy.additionalTransforms.addAll(additionalTransforms);
            return copy;
        }
    }

    public static final EnumMap<TrackShape, TrackCasingSpec> TRACK_CASINGS = new EnumMap<>(TrackShape.class);

    public static final PartialModel TRACK_CASING_FLAT = block("track_casing/flat");
    public static final PartialModel TRACK_CASING_FLAT_THICK = block("track_casing/flat_thick");

    private static final PartialModel
        xo = block("track_casing/xo"),
        zo = block("track_casing/zo"),
        pd = block("track_casing/pd"),
        nd = block("track_casing/nd"),
        cr_o = block("track_casing/cr_o"),
        an = block("track_casing/an"),
        as = block("track_casing/as"),
        ae = block("track_casing/ae"),
        aw = block("track_casing/aw"),


        xo_wide = block("track_casing/xo_wide"),
        zo_wide = block("track_casing/zo_wide"),
        pd_wide = block("track_casing/pd_wide"),
        nd_wide = block("track_casing/nd_wide"),
        cr_o_wide = block("track_casing/cr_o_wide"),
        an_wide = block("track_casing/an_wide"),
        as_wide = block("track_casing/as_wide"),
        ae_wide = block("track_casing/ae_wide"),
        aw_wide = block("track_casing/aw_wide"),

        xo_narrow = block("track_casing/xo_narrow"),
        zo_narrow = block("track_casing/zo_narrow"),
        pd_narrow = block("track_casing/pd_narrow"),
        nd_narrow = block("track_casing/nd_narrow"),
        cr_o_narrow = block("track_casing/cr_o_narrow"),
        an_narrow = block("track_casing/an_narrow"),
        as_narrow = block("track_casing/as_narrow"),
        ae_narrow = block("track_casing/ae_narrow"),
        aw_narrow = block("track_casing/aw_narrow");

    public static void registerCasingSpecs() {
        TRACK_CASINGS.clear();
        
        TrackCasingSpec spec_5x5 = new TrackCasingSpec(TRACK_CASING_FLAT, 3);
        for (int xOff = -2; xOff <= 2; xOff++) {
            for (int zOff = -2; zOff <= 2; zOff++) {
                if (xOff == 0 && zOff == 0)
                    continue;
                spec_5x5.addTransform(xOff, zOff);
            }
        }

        TrackCasingSpec spec_3x3_cross = new TrackCasingSpec(TRACK_CASING_FLAT, 3)
            .addTransform(-1, 0).addTransform(1, 0)
            .addTransform(0, -1).addTransform(0, 1);

        TrackCasingSpec spec_3x3 = new TrackCasingSpec(TRACK_CASING_FLAT, 3)
            .addTransform(1, 1).addTransform(0, 1).addTransform(-1, 1)
            .addTransform(1, 0).addTransform(-1, 0)
            .addTransform(1, -1).addTransform(0, -1).addTransform(-1, -1)
            .withTrackType(WIDE_GAUGE, spec_5x5);
        spec_3x3.withTrackType(NARROW_GAUGE, spec_3x3.copy()
            .withAltSpec(spec_3x3_cross));

        TrackCasingSpec flat_x = new TrackCasingSpec(TRACK_CASING_FLAT, 3)
            .addTransform(0, 1).addTransform(0, -1)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(TRACK_CASING_FLAT, 3)
                .addTransform(0, -2).addTransform(0, -1)
                .addTransform(0, 1).addTransform(0, 2));
        TrackCasingSpec flat_z = new TrackCasingSpec(TRACK_CASING_FLAT, 3)
            .addTransform(1, 0).addTransform(-1, 0)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(TRACK_CASING_FLAT, 3)
                .addTransform(-2, 0).addTransform(-1, 0)
                .addTransform(1, 0).addTransform(2, 0));

        TRACK_CASINGS.put(TrackShape.XO, new TrackCasingSpec(xo, 8).withAltSpec(flat_x)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(xo_wide, 8))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(xo_narrow, 8)));
        TRACK_CASINGS.put(TrackShape.ZO, new TrackCasingSpec(zo, 8).withAltSpec(flat_z)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(zo_wide, 8))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(zo_narrow, 8)));


        TRACK_CASINGS.put(TrackShape.PD, new TrackCasingSpec(pd, 8)
            .withAltSpec(new TrackCasingSpec(TRACK_CASING_FLAT, 3)
                .addTransform(1, 0).addTransform(1, -1)
                .addTransform(0, 1).addTransform(-1, 1)
            )
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(pd_wide, 8)
                .withAltSpec(new TrackCasingSpec(TRACK_CASING_FLAT, 3)
                    .addTransform(1, 0).addTransform(1, -1).addTransform(2, -1)
                    .addTransform(0, 1).addTransform(-1, 1).addTransform(-1, 2)
                )
            )
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(pd_narrow, 8)));
        TRACK_CASINGS.put(TrackShape.ND, new TrackCasingSpec(nd, 8)
            .withAltSpec(new TrackCasingSpec(TRACK_CASING_FLAT, 3)
                .addTransform(-1, 0).addTransform(-1, -1)
                .addTransform(0, 1).addTransform(1, 1)
            )
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(nd_wide, 8)
                .withAltSpec(new TrackCasingSpec(TRACK_CASING_FLAT, 3)
                    .addTransform(-1, 0).addTransform(-1, -1).addTransform(-2, -1)
                    .addTransform(0, 1).addTransform(1, 1).addTransform(1, 2)
                )
            )
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(nd_narrow, 8)));

        TRACK_CASINGS.put(TrackShape.TE, new TrackCasingSpec(xo, 8).withAltSpec(flat_x)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(xo_wide, 8))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(xo_narrow, 8)));
        TRACK_CASINGS.put(TrackShape.TW, new TrackCasingSpec(xo, 8).withAltSpec(flat_x)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(xo_wide, 8))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(xo_narrow, 8)));
        TRACK_CASINGS.put(TrackShape.TN, new TrackCasingSpec(zo, 8).withAltSpec(flat_z)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(zo_wide, 8))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(zo_narrow, 8)));
        TRACK_CASINGS.put(TrackShape.TS, new TrackCasingSpec(zo, 8).withAltSpec(flat_z)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(zo_wide, 8))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(zo_narrow, 8)));

        TRACK_CASINGS.put(TrackShape.CR_O, new TrackCasingSpec(cr_o, 8).withAltSpec(spec_3x3)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(cr_o_wide, 8))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(cr_o_narrow, 8)));

        TRACK_CASINGS.put(TrackShape.CR_D, new TrackCasingSpec(TRACK_CASING_FLAT, 3)
            .addTransform(1, 2).addTransform(0, 2).addTransform(-1, 2)
            .addTransform(2, 1).addTransform(1, 1).addTransform(0, 1).addTransform(-1, 1).addTransform(-2, 1)
            .addTransform(2, 0).addTransform(1, 0).addTransform(-1, 0).addTransform(-2, 0)
            .addTransform(1, -1).addTransform(0, -1).addTransform(-1, -1)
            .addTransform(0, -2)
            .withAltSpec(spec_3x3)
            .withTrackType(NARROW_GAUGE, spec_3x3_cross)
        );

        // CR_.D. are all fine for wide
        TRACK_CASINGS.put(TrackShape.CR_PDX, spec_3x3);
        TRACK_CASINGS.put(TrackShape.CR_PDZ, spec_3x3);
        TRACK_CASINGS.put(TrackShape.CR_NDX, spec_3x3);
        TRACK_CASINGS.put(TrackShape.CR_NDZ, spec_3x3);

        TRACK_CASINGS.put(TrackShape.AN, new TrackCasingSpec(an, 5).overlayShift(0, 3 / 16f)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(an_wide, 5))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(an_narrow, 5)));
        TRACK_CASINGS.put(TrackShape.AS, new TrackCasingSpec(as, 5).overlayShift(0, -3 / 16f)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(as_wide, 5))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(as_narrow, 5)));
        TRACK_CASINGS.put(TrackShape.AE, new TrackCasingSpec(ae, 5).overlayShift(-3 / 16f, 0)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(ae_wide, 5))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(ae_narrow, 5)));
        TRACK_CASINGS.put(TrackShape.AW, new TrackCasingSpec(aw, 5).overlayShift(3 / 16f, 0)
            .withTrackType(WIDE_GAUGE, new TrackCasingSpec(aw_wide, 5))
            .withTrackType(NARROW_GAUGE, new TrackCasingSpec(aw_narrow, 5)));
    }

    static {
        registerCasingSpecs();
    }

    public static final PartialModel
        COUPLER_COUPLE = block("track_overlay/coupler_couple"),
        COUPLER_DECOUPLE = block("track_overlay/coupler_decouple"),
        COUPLER_BOTH = block("track_overlay/coupler_both"),
        COUPLER_NONE = block("track_overlay/coupler_none");

    public static final PartialModel
        ANDESITE_SWITCH_FLAG = block("track_switch_andesite/flag"),
        ANDESITE_SWITCH_HANDLE = block("track_switch_andesite/handle"),
        BRASS_SWITCH_FLAG = block("track_switch_brass/flag"),
        SWITCH_NONE = block("track_overlay/switch_none"),
        SWITCH_RIGHT_STRAIGHT = block("track_overlay/switch_right_straight"),
        SWITCH_RIGHT_TURN = block("track_overlay/switch_right_turn"),
        SWITCH_LEFT_STRAIGHT = block("track_overlay/switch_left_straight"),
        SWITCH_LEFT_TURN = block("track_overlay/switch_left_turn"),
        SWITCH_3WAY_STRAIGHT = block("track_overlay/switch_3way_straight"),
        SWITCH_3WAY_LEFT = block("track_overlay/switch_3way_left"),
        SWITCH_3WAY_RIGHT = block("track_overlay/switch_3way_right"),
        SWITCH_2WAY_LEFT = block("track_overlay/switch_2way_left"),
        SWITCH_2WAY_RIGHT = block("track_overlay/switch_2way_right")
    ;

    public static final PartialModel
        MONORAIL_SEGMENT_TOP = block("monorail/monorail/segment_top"),
        MONORAIL_SEGMENT_BOTTOM = block("monorail/monorail/segment_bottom"),
        MONORAIL_SEGMENT_MIDDLE = block("monorail/monorail/segment_middle"),
        MONORAIL_TRACK_ASSEMBLING_OVERLAY = block("monorail/monorail/assembling_overlay"),
        MONOBOGEY_FRAME = block("bogey/monorail/frame"),
        MONOBOGEY_WHEEL = block("bogey/monorail/wheel");

    public static final PartialModel
        SINGLEAXLE_FRAME = block("bogey/singleaxle/singleaxle_frame"),
        LEAFSPRING_FRAME = block("bogey/leafspring/leafspring_frame"),
        COILSPRING_FRAME = block("bogey/coilspring/coilspring_frame"),
        FREIGHT_FRAME = block("bogey/freight/freight_frame"),
        ARCHBAR_FRAME = block("bogey/archbar/archbar_frame"),
        PASSENGER_FRAME = block("bogey/passenger/passenger_frame"),
        MODERN_FRAME = block("bogey/modern/modern_frame"),
        BLOMBERG_FRAME = block("bogey/blomberg/blomberg_frame"),
        Y25_FRAME = block("bogey/y25/y25_frame"),
        HEAVYWEIGHT_FRAME = block("bogey/heavyweight/heavyweight_frame"),
        RADIAL_FRAME = block("bogey/radial/radial_frame"),
        CR_BOGEY_WHEELS = block("bogey/wheels/cr_bogey_wheels"),
        CR_WIDE_BOGEY_WHEELS = block("bogey/wide/wheels"),
        WIDE_DEFAULT_FRAME = block("bogey/wide/default/frame")
    ;

    public static final PartialModel
        WIDE_SCOTCH_FRAME = block("bogey/wide/scotch_yoke/frame"),
        WIDE_SCOTCH_PINS = block("bogey/wide/scotch_yoke/pins"),
        WIDE_SCOTCH_PISTONS = block("bogey/wide/scotch_yoke/pistons"),
        WIDE_SCOTCH_WHEELS = block("bogey/wide/scotch_yoke/wheels")
    ;

    public static final PartialModel
        WIDE_COMICALLY_LARGE_FRAME = block("bogey/wide/comically_large/frame"),
        WIDE_COMICALLY_LARGE_PINS = block("bogey/wide/comically_large/pins"),
        WIDE_COMICALLY_LARGE_PISTONS = block("bogey/wide/comically_large/pistons"),
        WIDE_COMICALLY_LARGE_WHEELS = block("bogey/wide/comically_large/wheels")
    ;

    public static final PartialModel
        NARROW_WHEELS = block("bogey/narrow/wheels"),
        NARROW_FRAME = block("bogey/narrow/default_small/frame"),
        NARROW_SCOTCH_WHEELS = block("bogey/narrow/scotch_wheels"),
        NARROW_SCOTCH_WHEEL_PINS = block("bogey/narrow/scotch_wheel_pins"),
        NARROW_SCOTCH_FRAME = block("bogey/narrow/default_scotch/frame"),
        NARROW_SCOTCH_PISTONS = block("bogey/narrow/default_scotch/pistons"),
        NARROW_DOUBLE_SCOTCH_FRAME = block("bogey/narrow/double_scotch/frame"),
        NARROW_DOUBLE_SCOTCH_PISTONS = block("bogey/narrow/double_scotch/pistons")
    ;

    public static final PartialModel
        HANDCAR_COUPLING = block("bogey/handcar/coupling"),
        HANDCAR_FRAME = block("bogey/handcar/frame"),
        HANDCAR_HANDLE = block("bogey/handcar/handle"),
        HANDCAR_LARGE_COG = block("bogey/handcar/large_cog"),
        HANDCAR_SMALL_COG = block("bogey/handcar/small_cog")
    ;

    public static final PartialModel
            MEDIUM_SHARED_WHEELS = block("bogey/medium/shared/wheels"),


            MEDIUM_STANDARD_FRAME = block("bogey/medium/standard/frame"),
            MEDIUM_SINGLE_WHEEL_FRAME = block("bogey/medium/single_wheel/frame"),
            MEDIUM_TRIPLE_WHEEL_FRAME = block("bogey/medium/triple_wheel/frame"),
            MEDIUM_QUADRUPLE_WHEEL_FRAME = block("bogey/medium/quadruple_wheel/frame"),
            MEDIUM_QUINTUPLE_WHEEL_FRAME = block("bogey/medium/quintuple_wheel/frame"),

            MEDIUM_2_0_2_TRAILING_FRAME = block("bogey/medium/2-0-2_trailing/frame"),
            MEDIUM_4_0_4_TRAILING_FRAME = block("bogey/medium/4-0-4_trailing/frame"),
            MEDIUM_6_0_6_TRAILING_FRAME = block("bogey/medium/6-0-6_trailing/frame"),
            MEDIUM_6_0_6_TENDER_FRAME = block("bogey/medium/6-0-6_tender/frame"),
            MEDIUM_8_0_8_TENDER_FRAME = block("bogey/medium/8-0-8_tender/frame"),
            MEDIUM_10_0_10_TENDER_FRAME = block("bogey/medium/10-0-10_tender/frame")
    ;


    public static final PartialModel DIESEL_STACK_FAN = block("smokestack/block_diesel_fan");
    public static final PartialModel CONDUCTOR_ANTENNA = block("conductor_antenna");

    private static PartialModel createBlock(String path) {
        return new PartialModel(Create.asResource("block/" + path));
    }

    private static PartialModel block(String path) {
        return new PartialModel(Railways.asResource("block/" + path));
    }

    static {
        for (DyeColor color : DyeColor.values()) {
            TOOLBOX_BODIES.put(color, createBlock(Lang.asId(color.name()) + "_toolbox"));
            CONDUCTOR_WHISTLE_FLAGS.put(color, block("conductor_whistle/flag_"+Lang.asId(color.name())));
        }
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }
}
