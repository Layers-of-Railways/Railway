package com.railwayteam.railways.util;

import com.simibubi.create.AllItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public interface WrenchableEntity {
    default ActionResultType onWrenched(PlayerEntity plr, Hand hand, Entity entity) {
        ItemStack stack = plr.getHeldItem(hand);
        if (stack.getItem().equals(AllItems.WRENCH.get()) && plr.isSneaking()) {
            entity.remove();
            afterWrenched(plr, hand);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    default void afterWrenched(PlayerEntity plr, Hand hand) {
    }
}