package com.railwayteam.railways.items.handcar;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.entities.handcar.HandcarEntity;
import com.railwayteam.railways.util.Animatable;
import com.railwayteam.railways.util.EntityItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HandcarItem extends EntityItem<HandcarEntity> implements Animatable {
    public static HandcarItem g() {
        return ModSetup.R_ITEM_HANDCAR.get();
    }

    public ItemStack create(HandcarEntity entity) {
        ItemStack stack = new ItemStack(ModSetup.R_ITEM_HANDCAR.get());
        putEntityDataInItem(stack, entity);
        return stack;
    }

    public HandcarItem(Item.Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public HandcarEntity spawnEntity(PlayerEntity plr, ItemStack stack, Vector3d pos) {
        World world = plr.world;
        HandcarEntity entity = new HandcarEntity(ModSetup.R_ENTITY_HANDCAR.get(), world);
        entity.setPosition(pos.x, pos.y, pos.z);
        world.addEntity(entity);
        return entity;
    }

    @Override
    public <E extends IAnimatable> AnimationBuilder getAnimation(AnimationEvent<E> event) {
        return anim("push");
    }

    protected AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}