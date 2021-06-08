package com.railwayteam.railways.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class EntityItem<E extends Entity> extends Item {
    public abstract E spawnEntity(PlayerEntity plr, ItemStack stack, Vector3d pos);

    public EntityItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public E spawnNew(PlayerEntity plr, ItemStack stack, Vector3d pos) {
        if(!plr.isCreative()) {
            stack.shrink(1);
        }
//        return ConductorEntity.spawn(plr.world, pos, ConductorEntity.getDefaultColor());
        return spawnEntity(plr, stack, pos);
    }

    public ITextComponent getDisplayName(ItemStack stack) {
        if(hasEntity(stack)) {
            E entity = getEntityFromItem(stack, Minecraft.getInstance().world);
            return entity.hasCustomName() ? new StringTextComponent(entity.getCustomName().getString()).styled(style -> style.withItalic(true)) : super.getDisplayName(stack);
        }
        return super.getDisplayName(stack);
    }

    public void setHealthNonLiving(E entity) {

    }

    public E spawn(ItemStack stack, BlockPos pos, PlayerEntity plr, Direction face) {
        Vector3d spawn = VectorUtils.blockPosToVector3d(pos.offset(face)).add(0.5, 0, 0.5);
        if(!hasEntity(stack)) { // if the item is created using /give or through creative, it doesnt have an entity
            return spawnNew(plr, stack, spawn);
        } else {
            E entity = getEntityFromItem(stack, plr.world);
            entity.setPositionAndRotation(spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);
            stack.shrink(1);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).setHealth(((LivingEntity) entity).getMaxHealth());
                if(stack.hasDisplayName()) {
                    entity.setCustomName(stack.getDisplayName());
                }
            }
            else setHealthNonLiving(entity);
            entity.setUniqueId(UUID.randomUUID()); // to prevent UUID conflicts, the UUID is changed but the data is kept, so its pretty much a clone of the original
            plr.world.addEntity(entity);
            entity.fallDistance = 0; // prevent instant death if died from fall damage
            entity.setFireTicks(0); // prevent fire if died from fire
            entity.setAir(entity.getMaxAir()); // prevent starting to drown immediately if died from drowning
            entity.setMotion(0, 0.1, 0);
            return entity;
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        spawn(ctx.getItem(), ctx.getPos(), ctx.getPlayer(), ctx.getFace());
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onCreated(ItemStack stack, World world, PlayerEntity player) {
        super.onCreated(stack, world, player);
        stack.setTag(new CompoundNBT());
    }

    public boolean hasEntity(CompoundNBT nbt)  {
        return nbt.contains("entity");
    }

    public boolean hasEntity(ItemStack stack) {
        return hasEntity(stack.getOrCreateTag());
    }

    public void putEntityDataInNbt(CompoundNBT nbt, E entity) {
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.writeWithoutTypeId(nbt);
    }

    public void putEntityDataInItem(ItemStack item, E entity) {
        putEntityDataInNbt(item.getOrCreateTag(), entity);
    }

    public E getEntityFromNBT(CompoundNBT nbt, World world) {
        EntityType type = EntityType.byKey(nbt.getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(world);
            entity.read(nbt);
            return (E) entity;
        }
        return null;
    }

    public E getEntityFromItem(ItemStack item, World world) {
        return getEntityFromNBT(item.getOrCreateTag(), world);
    }
}
