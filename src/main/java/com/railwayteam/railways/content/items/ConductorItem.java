package com.railwayteam.railways.content.items;

import com.railwayteam.railways.content.entities.conductor.ConductorEntity;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.util.EntityItem;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;

public class ConductorItem extends EntityItem<ConductorEntity> {
    public final DyeColor color;

    public static ConductorItem g(DyeColor color) {
        return CRItems.CONDUCTOR_ITEMS.get(color).get();
    }

    public ItemStack create(ConductorEntity entity) {
        ItemStack stack = new ItemStack(g(color));
        putEntityDataInItem(stack, entity);
        return stack;
    }

    @Override
    public ConductorEntity spawnEntity(PlayerEntity plr, ItemStack stack, Vector3d pos) {
        ConductorEntity entity = ConductorEntity.spawn(plr.world, pos, ConductorEntity.getDefaultColor());
        entity.setColor(color);
        return entity;
    }

    public ConductorItem(Properties p_i48487_1_, DyeColor color) {
        super(p_i48487_1_);
        this.color = color;
    }

    public ActionResultType onMinecartRightClicked(PlayerEntity plr, ItemStack stack, Hand hand, MinecartEntity entity) {
        ConductorEntity conductor = spawn(stack, entity.getBlockPos(), plr, Direction.UP);
        return conductor.startRiding(entity) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }
}
