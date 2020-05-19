package net.minecraft.network.status.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SServerInfoPacket implements IPacket<IClientStatusNetHandler> {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ServerStatusResponse.Version.class, new ServerStatusResponse.Version.Serializer()).registerTypeAdapter(ServerStatusResponse.Players.class, new ServerStatusResponse.Players.Serializer()).registerTypeAdapter(ServerStatusResponse.class, new ServerStatusResponse.Serializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
   private ServerStatusResponse response;

   public SServerInfoPacket() {
   }

   public SServerInfoPacket(ServerStatusResponse responseIn) {
      this.response = responseIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.response = JSONUtils.fromJson(GSON, buf.readString(32767), ServerStatusResponse.class);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.response.getJson()); //Forge: Let the response cache the JSON
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientStatusNetHandler handler) {
      handler.handleServerInfo(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ServerStatusResponse getResponse() {
      return this.response;
   }
}