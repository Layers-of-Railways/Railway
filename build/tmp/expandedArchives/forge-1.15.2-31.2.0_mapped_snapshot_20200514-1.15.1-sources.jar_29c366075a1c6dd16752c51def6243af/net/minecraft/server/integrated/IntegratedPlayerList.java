package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedPlayerList extends PlayerList {
   private CompoundNBT hostPlayerData;

   public IntegratedPlayerList(IntegratedServer server) {
      super(server, 8);
      this.setViewDistance(10);
   }

   /**
    * also stores the NBTTags if this is an intergratedPlayerList
    */
   protected void writePlayerData(ServerPlayerEntity playerIn) {
      if (playerIn.getName().getString().equals(this.getServer().getServerOwner())) {
         this.hostPlayerData = playerIn.writeWithoutTypeId(new CompoundNBT());
      }

      super.writePlayerData(playerIn);
   }

   public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      return (ITextComponent)(p_206258_2_.getName().equalsIgnoreCase(this.getServer().getServerOwner()) && this.getPlayerByUsername(p_206258_2_.getName()) != null ? new TranslationTextComponent("multiplayer.disconnect.name_taken") : super.canPlayerLogin(p_206258_1_, p_206258_2_));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   /**
    * On integrated servers, returns the host's player data to be written to level.dat.
    */
   public CompoundNBT getHostPlayerData() {
      return this.hostPlayerData;
   }
}