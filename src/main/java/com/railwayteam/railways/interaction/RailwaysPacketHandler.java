package com.railwayteam.railways.interaction;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.CustomPacketBase;
import com.railwayteam.railways.interaction.packets.CustomPacketStationList;
import com.railwayteam.railways.interaction.packets.CustomPacketUpdateOrders;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RailwaysPacketHandler {
  public static final String PROTOCOL_VERSION       = new ResourceLocation(Railways.MODID, "1").toString();
  public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Railways.MODID, "cart-update");
  public static SimpleChannel channel;

  private static final CustomPacketRegistrar<CustomPacketStationList> cprCartSchedule =
    new CustomPacketRegistrar<>(CustomPacketStationList.class, CustomPacketStationList::new);

  private static final CustomPacketRegistrar<CustomPacketUpdateOrders> cprStationOrders =
    new CustomPacketRegistrar<>(CustomPacketUpdateOrders.class, CustomPacketUpdateOrders::new);

  public static void register () {
  //  channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
  //    .serverAcceptedVersions(s->true)
  //    .clientAcceptedVersions(s->true)
  //    .networkProtocolVersion( ()->PROTOCOL_VERSION).simpleChannel();
    channel = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    cprCartSchedule.register();
    cprStationOrders.register();
  }

  private static class CustomPacketRegistrar <T extends CustomPacketBase> {
    private static int index = 0;
    BiConsumer<T, PacketBuffer> encoder;
    Function<PacketBuffer, T> decoder;
    BiConsumer<T, Supplier<Context>> handler;
    Class<T> type;

    public CustomPacketRegistrar (Class<T> type, Function<PacketBuffer,T> factory) {
      encoder = T::write;
      decoder = factory;
      handler = T::handle;
      this.type = type;
    }

    public void register () {
    //  channel.messageBuilder(type, index++).encoder(encoder).decoder(decoder).consumer(handler).add();
      channel.registerMessage(index++, type, encoder, decoder, handler);
    }
  }
}
