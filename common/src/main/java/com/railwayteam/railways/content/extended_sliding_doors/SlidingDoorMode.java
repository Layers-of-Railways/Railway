package com.railwayteam.railways.content.extended_sliding_doors;

import com.railwayteam.railways.registry.CRIcons;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public enum SlidingDoorMode implements INamedIconOptions {
    NORMAL(CRIcons.I_DOOR_NORMAL), //shouldOpen -> noChange; shouldUpdate -> noChange; - default behaviour
    MANUAL(CRIcons.I_DOOR_MANUAL), //shouldOpen -> noChange; shouldUpdate -> never; // done block redstone operation
    SPECIAL(CRIcons.I_DOOR_SPECIAL), //shouldOpen -> &= at right station; shouldUpdate -> noChange; // done block hand operation (shift-use works on trains/contraptions though)
    ;

    private final String translationKey;
    private final AllIcons icon;

    SlidingDoorMode(AllIcons icon) {
        this(icon, false);
    }

    SlidingDoorMode(AllIcons icon, boolean stationBased) {
        this.icon = icon;
        this.translationKey = "sliding_door.mode." + Lang.asId(name());
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
            return SlidingDoorMode.NORMAL;
        return SlidingDoorMode.values()[Math.min(2, Math.max(0, nbt.getInt("ScrollValue")))];
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
        public Vec3 getLocalOffset(BlockState state) {
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
        SlidingDoorMode snr$getSlidingDoorMode();
    }
}
