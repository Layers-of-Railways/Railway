package com.railwayteam.railways.content.commands;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.items.ConductorItem;
import com.railwayteam.railways.content.items.engineers_cap.EngineersCapItem;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Railways.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RCSCommand {
    public static ArrayList<ServerPlayerEntity> enableRCS = new ArrayList<>();

    @SubscribeEvent
    public static void reg(RegisterCommandsEvent rce) {
        rce.getDispatcher().register(Commands.literal("railwayrcs")
                .requires(r -> r.hasPermissionLevel(2))
                .executes(ctx -> {
                    if (ctx.getSource().getEntity() != null) {
                        ServerPlayerEntity plr = ctx.getSource().asPlayer();
                        if (enableRCS.contains(plr)) {
                            enableRCS.remove(plr);
                        } else {
                            enableRCS.add(plr);
                        }
                        ctx.getSource().sendFeedback(new StringTextComponent("RCS " + (enableRCS.contains(plr) ? "enabled" : "disabled")), false);
                    }
                    return 1;
                })
        );
    }

    static int ticksToRcsUpdate = 20;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        ticksToRcsUpdate--;
        if (ticksToRcsUpdate <= 0) {
            ticksToRcsUpdate = 20;
            DyeColor color1 = DyeColor.byId(1);
            for (ServerPlayerEntity plr : enableRCS) {
                if (!(plr.getHeldItemMainhand().getItem() instanceof ConductorItem)) {
                    plr.setHeldItem(Hand.MAIN_HAND, new ItemStack(ConductorItem.g(color1)));
                }
                if (!(plr.getHeldItemOffhand().getItem() instanceof EngineersCapItem)) {
                    plr.setHeldItem(Hand.OFF_HAND, new ItemStack(CRItems.ENGINEERS_CAPS.get(color1).get()));
                }
                if (!(plr.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof EngineersCapItem)) {
                    plr.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(CRItems.ENGINEERS_CAPS.get(color1).get()));
                }
                int color = ((ConductorItem) plr.getHeldItemMainhand().getItem()).color.getId();
                color %= 16;
                color++;
                DyeColor dyeColor = DyeColor.byId(color);
                plr.setHeldItem(Hand.MAIN_HAND, new ItemStack(ConductorItem.g(dyeColor)));
                plr.setHeldItem(Hand.OFF_HAND, new ItemStack(CRItems.ENGINEERS_CAPS.get(dyeColor).get()));
                plr.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(CRItems.ENGINEERS_CAPS.get(dyeColor).get()));
            }
        }
    }
}
