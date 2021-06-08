package com.railwayteam.railways.items;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.entities.conductor.ConductorEntity;
import com.railwayteam.railways.util.EntityItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static com.tterrag.registrate.providers.RegistrateLangProvider.toEnglishName;

public class ConductorItem extends EntityItem<ConductorEntity> {
    public final DyeColor color;

    public static ConductorItem g(DyeColor color) {
        return ModSetup.CONDUCTOR_ITEMS.get(color).get();
    }

    public ItemStack create(ConductorEntity entity) {
        ItemStack stack = new ItemStack(g(color));
        putEntityDataInItem(stack, entity);
        return stack;
    }

    @Override
    public ConductorEntity spawnEntity(PlayerEntity plr, ItemStack stack, Vector3d pos) {
        return ConductorEntity.spawn(plr.world, pos, ConductorEntity.getDefaultColor());
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
