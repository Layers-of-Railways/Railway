package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.palettes.cycle_menu.TagCycleHandlerServer;
import com.railwayteam.railways.multiloader.C2SPacket;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class TagCycleSelectionPacket implements C2SPacket {
    final Item target;

    public TagCycleSelectionPacket(Item target) {
        this.target = target;
    }

    public TagCycleSelectionPacket(FriendlyByteBuf buf) {
        target = Registry.ITEM.get(buf.readResourceLocation());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(Registry.ITEM.getKey(target));
    }

    @Override
    public void handle(ServerPlayer sender) {
        TagCycleHandlerServer.select(sender, target);
    }
}
