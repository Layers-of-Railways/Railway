package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerServer;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BogeyStyleSelectionPacket implements C2SPacket {
    final BogeyStyle style;
    @Nullable
    final BogeySize size;

    public BogeyStyleSelectionPacket(@NotNull BogeyStyle style) {
        this(style, null);
    }

    public BogeyStyleSelectionPacket(@NotNull BogeyStyle style, @Nullable BogeySize size) {
        this.style = style;
        this.size = size;
    }

    public BogeyStyleSelectionPacket(FriendlyByteBuf buf) {
        ResourceLocation loc = buf.readResourceLocation();
        style = AllBogeyStyles.BOGEY_STYLES.getOrDefault(loc, AllBogeyStyles.STANDARD);
        if (buf.readBoolean()) {
            ResourceLocation sizeLoc = buf.readResourceLocation();
            size = BogeySizes.getAllSizesSmallToLarge().stream()
                    .filter((s) -> s.location().equals(sizeLoc))
                    .findFirst().orElse(null);
        } else {
            size = null;
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(style.name);
        buffer.writeBoolean(size != null);
        if (size != null)
            buffer.writeResourceLocation(size.location());
    }

    @Override
    public void handle(ServerPlayer sender) {
        BogeyCategoryHandlerServer.selectedStyles.put(sender.getUUID(), Pair.of(style, size));
    }
}
