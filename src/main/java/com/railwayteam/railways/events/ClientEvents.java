package com.railwayteam.railways.events;

import com.railwayteam.railways.Railways;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    private static final String ITEM_PREFIX = "item." + Railways.MODID;
    private static final String BLOCK_PREFIX = "block." + Railways.MODID;

    @SubscribeEvent
    public static void addToItemTooltip(ItemTooltipEvent event) {
        if (!AllConfigs.CLIENT.tooltips.get())
            return;
        if (event.getEntity() == null)
            return;

        ItemStack stack = event.getItemStack();
        String translationKey = stack.getItem()
            .getDescriptionId(stack);

        if (translationKey.startsWith(ITEM_PREFIX) || translationKey.startsWith(BLOCK_PREFIX))
            if (TooltipHelper.hasTooltip(stack, event.getEntity())) {
                List<Component> itemTooltip = event.getToolTip();
                List<Component> toolTip = new ArrayList<>();
                toolTip.add(itemTooltip.remove(0));
                TooltipHelper.getTooltip(stack)
                    .addInformation(toolTip);
                itemTooltip.addAll(0, toolTip);
            }
    }
}
