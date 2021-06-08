package com.railwayteam.railways.goals;

import com.railwayteam.railways.entities.conductor.ConductorEntity;
import com.railwayteam.railways.items.engineers_cap.EngineersCapItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class WalkToNearestPlayerWithCapGoal extends MoveTowardsClosestEntityGoal<PlayerEntity> {
    public WalkToNearestPlayerWithCapGoal(ConductorEntity entity, double speed, int targetChance, int aabbSize, float minDistance) {
        super(entity, PlayerEntity.class, speed, targetChance, aabbSize, minDistance);
    }

    public WalkToNearestPlayerWithCapGoal(ConductorEntity entity, double speed, int aabbSize, float minDistance) {
        super(entity, PlayerEntity.class, speed, aabbSize, minDistance);
    }

    @Override
    public boolean checkTarget(PlayerEntity entity) {
        NonNullList<ItemStack> armor = entity.inventory.armorInventory;
        Item item = armor.get(3).getItem();
        if(item instanceof EngineersCapItem) {
            return super.checkTarget(entity) && ((EngineersCapItem) item).color.equals(((ConductorEntity) goalOwner).getColor());
        }
        return false;
    }
}
