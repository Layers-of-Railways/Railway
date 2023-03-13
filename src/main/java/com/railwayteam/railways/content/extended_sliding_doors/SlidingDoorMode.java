package com.railwayteam.railways.content.extended_sliding_doors;

import com.railwayteam.railways.registry.CRIcons;
import com.simibubi.create.content.curiosities.deco.SlidingDoorBlock;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.INamedIconOptions;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public enum SlidingDoorMode implements INamedIconOptions {
    AUTO(CRIcons.I_DOOR_AUTO), //shouldOpen -> noChange; shouldUpdate -> noChange;
    MANUAL(CRIcons.I_DOOR_MANUAL), //shouldOpen -> noChange; shouldUpdate -> never;
    STATION_RIGHT(CRIcons.I_DOOR_STATION_RIGHT, true), //shouldOpen -> &= at right station; shouldUpdate -> noChange;
    STATION_LEFT(CRIcons.I_DOOR_STATION_LEFT, true); //shouldOpen -> &= at left station; shouldUpdate -> noChange;

    private final String translationKey;
    private final AllIcons icon;
    public final boolean stationBased;

    SlidingDoorMode(AllIcons icon) {
        this(icon, false);
    }

    SlidingDoorMode(AllIcons icon, boolean stationBased) {
        this.icon = icon;
        this.translationKey = "sliding_door.mode." + Lang.asId(name());
        this.stationBased = stationBased;
    }

    public SlidingDoorMode flipped() {
        if (stationBased) {
            return this == STATION_LEFT ? STATION_RIGHT : STATION_LEFT;
        } else {
            return this;
        }
    }

    @Override
    public AllIcons getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    public static SlidingDoorMode fromNbt(CompoundTag nbt) {
        if (nbt == null)
            return SlidingDoorMode.AUTO;
        return SlidingDoorMode.values()[Math.min(3, Math.max(0, nbt.getInt("ScrollValue")))];
    }

    public static class SlidingDoorValueBoxTransform extends CenteredSideValueBoxTransform {
        public SlidingDoorValueBoxTransform() {
            super((state, d) -> {
                Direction facing = state.getValue(SlidingDoorBlock.FACING);
                boolean showAtAll = state.getValue(SlidingDoorBlock.VISIBLE) && !state.getValue(SlidingDoorBlock.OPEN);
                return showAtAll && (d == facing || d == facing.getOpposite());
            });
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16); // z is depth
        }

        @Override
        protected Vec3 getLocalOffset(BlockState state) {
            Vec3 location = VecHelper.voxelSpace(8, 8, state.getValue(SlidingDoorBlock.FACING) == direction ? 3 : 16);
            location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
            location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);
            return location;
        }

        /*@Override
        protected void rotate(BlockState state, PoseStack ms) {
            float yRot = AngleHelper.horizontalAngle(getSide()) + 180;
            float yRot1 = yRot % 90;
            float yRot2 = yRot - yRot1;
            float xRot = getSide() == Direction.UP ? 90 : getSide() == Direction.DOWN ? 270 : 0;
            TransformStack.cast(ms)
                .rotateY(yRot2)
//                .translateZ(yRot1 > 45 ? -0.5 : 0.5)
                .rotateY(yRot1)
                .rotateX(xRot);
        }*/
    }

    public interface IHasDoorMode {
        SlidingDoorMode getSlidingDoorMode();
    }
}
