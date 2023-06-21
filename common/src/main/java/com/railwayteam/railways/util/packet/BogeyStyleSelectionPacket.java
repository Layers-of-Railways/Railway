package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerServer;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class BogeyStyleSelectionPacket implements C2SPacket {
    final BogeyStyle style;

    public BogeyStyleSelectionPacket(@NotNull BogeyStyle style) {
        this.style = style;
    }

    public BogeyStyleSelectionPacket(FriendlyByteBuf buf) {
        ResourceLocation loc = buf.readResourceLocation();
        style = AllBogeyStyles.BOGEY_STYLES.getOrDefault(loc, AllBogeyStyles.STANDARD);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(style.name);
    }

    @Override
    public void handle(ServerPlayer sender) {
        BogeyCategoryHandlerServer.selectedStyles.put(sender.getUUID(), style);
    }
}
