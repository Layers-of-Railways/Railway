package com.railwayteam.railways.content.smokestack.block.be;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock;
import com.railwayteam.railways.util.ColorUtils;
import com.railwayteam.railways.util.IHaveCustomGoggleIcon;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmokeStackBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveCustomGoggleIcon {

    public @Nullable DyeColor getColor() {
        return color;
    }

    public void setColor(@Nullable DyeColor color) {
        if (this.color == color) return;
        this.color = color;
        this.isSoul = false;
        notifyUpdate();
    }

    protected @Nullable DyeColor color = null;

    protected boolean isSoul = false;

    public void setSoul(boolean isSoul) {
        if (this.isSoul == isSoul) return;
        this.color = null;
        this.isSoul = isSoul;
        notifyUpdate();
    }

    public boolean isSoul() {
        return color == null && isSoul;
    }

    public SmokeStackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("color", Tag.TAG_INT)) {
            int colorOrdinal = tag.getInt("color");
            color = DyeColor.byId(colorOrdinal);
        } else {
            color = null;
        }
        isSoul = tag.getBoolean("isSoul");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (color != null) {
            tag.putInt("color", color.getId());
        }
        tag.putBoolean("isSoul", isSoul());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (color != null)
            Lang.builder(Railways.MODID)
                    .translate("smokestack.goggle.tooltip.color", ColorUtils.coloredName(color.getName()))
                    .forGoggles(tooltip);

        if (isSoul || color == null) {
            Lang.builder(Railways.MODID)
                    .translate("smokestack.goggle.tooltip",
                            isSoul ? "Soul" : ColorUtils.coloredName(DyeColor.BLACK.toString())
                    )
                    .forGoggles(tooltip);
        }

        return true;
    }

    @Override
    public ItemStack railways$setGoggleIcon(boolean isPlayerSneaking) {
        if (color != null)
            return ColorUtils.getDyeColorDyeItem(color).getDefaultInstance();

        return isSoul ? Items.SOUL_SOIL.getDefaultInstance() : Items.BLACK_DYE.getDefaultInstance();
    }

    @Override
    public void tick() {
        super.tick();

        if (level != null && level.isClientSide) {
            if (this.level.getRandom().nextFloat() < 0.11f) {
                ((SmokeStackBlock) this.getBlockState().getBlock()).blockEntityAnimateTick(this.getBlockState(), this.level, this.getBlockPos(), this.level.getRandom());
            }
        }
    }
}
