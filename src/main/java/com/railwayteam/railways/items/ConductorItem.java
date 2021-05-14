package com.railwayteam.railways.items;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.Util;
import com.railwayteam.railways.entities.conductor.ConductorEntity;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ConductorItem extends Item {
    public static ItemStack create(Entity entity) {
        ItemStack stack = new ItemStack(ModSetup.R_ITEM_CONDUCTOR.get());
        putEntityDataInItem(stack, entity);
        return stack;
    }

    public ConductorItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public static ConductorEntity spawnNew(PlayerEntity plr, ItemStack stack, BlockPos pos) {
        if(!plr.isCreative()) {
            stack.shrink(1);
        }
        return ConductorEntity.spawn(plr.world, pos, ConductorEntity.getDefaultColor());
    }

    public static ConductorEntity spawn(ItemStack stack, BlockPos pos, PlayerEntity plr, Direction face) {
        BlockPos spawn = pos.offset(face);
        if(!hasEntity(stack)) { // if the item is created using /give or through creative, it doesnt have an entity
            return spawnNew(plr, stack, spawn);
        } else {
            LivingEntity entity = (LivingEntity) getEntityFromItem(stack, plr.world);
            entity.setPositionAndRotation(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, 0, 0);
            stack.shrink(1);
            entity.setHealth(entity.getMaxHealth());
            entity.setUniqueId(UUID.randomUUID()); // to prevent UUID conflicts, the UUID is changed but the data is kept, so its pretty much a clone of the original
            plr.world.addEntity(entity);
            entity.fallDistance = 0; // prevent instant death if died from fall damage
            entity.setFireTicks(0); // prevent fire if died from fire
            entity.setAir(entity.getMaxAir()); // prevent starting to drown immediately if died from drowning
            entity.setMotion(0, 0.1, 0);
            return (ConductorEntity) entity;
        }
    }

    public static ActionResultType onMinecartRightClicked(PlayerEntity plr, ItemStack stack, Hand hand, MinecartEntity entity) {
        ConductorEntity conductor = spawn(stack, entity.getBlockPos(), plr, Direction.UP);
        return conductor.startRiding(entity) ? ActionResultType.SUCCESS : ActionResultType.PASS;
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

    public static boolean hasEntity(CompoundNBT nbt)  {
        return nbt.contains("entity");
    }

    public static boolean hasEntity(ItemStack stack) {
        return hasEntity(stack.getOrCreateTag());
    }

    public static void putEntityDataInNbt(CompoundNBT nbt, Entity entity) {
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.writeWithoutTypeId(nbt);
    }

    public static void putEntityDataInItem(ItemStack item, Entity entity) {
        putEntityDataInNbt(item.getOrCreateTag(), entity);
    }

    public static Entity getEntityFromNBT(CompoundNBT nbt, World world) {
        EntityType type = EntityType.byKey(nbt.getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(world);
            entity.read(nbt);
            return entity;
        }
        return null;
    }

    public static Entity getEntityFromItem(ItemStack item, World world) {
        return getEntityFromNBT(item.getOrCreateTag(), world);
    }

    public static CompoundNBT getOrCreateNbt(ItemStack stack) {
        return stack.getOrCreateTag();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> lines, ITooltipFlag p_77624_4_) {
        super.addInformation(stack, world, lines, p_77624_4_);
        ConductorEntity entity = (ConductorEntity) getEntityFromItem(stack, world);
        if(entity != null) {
            DyeColor color = entity.getColor();
            lines.add(new StringTextComponent("Cap color: " + Util.colorToColoredText(color)));
            if(entity.getCustomName() != null) {
                lines.add(new StringTextComponent("Name: " + entity.getCustomName().getString()));
            }
        } else {
            lines.add(new StringTextComponent("Cap color: " + Util.colorToColoredText(DyeColor.BLUE)));
        }
    }
}
