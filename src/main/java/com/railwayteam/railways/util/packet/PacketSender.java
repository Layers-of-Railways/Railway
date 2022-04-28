package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketSender {
  private static final String PROTOCOL_VER = "1";
  private static final ResourceLocation CHANNEL_ID = new ResourceLocation(Railways.MODID, "main");

  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
    CHANNEL_ID, ()-> PROTOCOL_VER,
    PROTOCOL_VER::equals,
    PROTOCOL_VER::equals
  );

  public static void register () {
    int uid = 0;
    CHANNEL.registerMessage(uid++, JukeboxCartPacket.class, JukeboxCartPacket::encode, JukeboxCartPacket::new, JukeboxCartPacket::handle);
  }

  public static void updateJukeboxClientside (MinecartJukebox sender, ItemStack newDisc) {
    CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(()->sender), new JukeboxCartPacket(sender, newDisc));
  }
}
