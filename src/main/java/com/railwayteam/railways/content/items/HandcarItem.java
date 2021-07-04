package com.railwayteam.railways.content.items;

import com.railwayteam.railways.content.entities.handcar.HandcarEntity;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.util.EntityItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import static com.railwayteam.railways.registry.CREntities.R_ENTITY_HANDCAR;

public class HandcarItem extends EntityItem<HandcarEntity> {
    public static HandcarItem g() {
        return CRItems.R_ITEM_HANDCAR.get();
    }

    public ItemStack create(HandcarEntity entity) {
        ItemStack stack = new ItemStack(CRItems.R_ITEM_HANDCAR.get());
        putEntityDataInItem(stack, entity);
        return stack;
    }

    public HandcarItem(Item.Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public HandcarEntity spawnEntity(PlayerEntity plr, ItemStack stack, Vector3d pos) {
        World world = plr.world;
        HandcarEntity entity = new HandcarEntity(R_ENTITY_HANDCAR.get(), world);
        entity.setPosition(pos.x, pos.y, pos.z);
        world.addEntity(entity);
        return entity;
    }

    @Override
    public void setHealthNonLiving(HandcarEntity entity) {
        entity.setHealth(entity.getMaxHealth());
    }
}