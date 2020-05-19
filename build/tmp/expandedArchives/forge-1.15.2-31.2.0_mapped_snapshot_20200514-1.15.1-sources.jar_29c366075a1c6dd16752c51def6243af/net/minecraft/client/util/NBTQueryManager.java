package net.minecraft.client.util;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NBTQueryManager {
   private final ClientPlayNetHandler connection;
   private int transactionId = -1;
   @Nullable
   private Consumer<CompoundNBT> handler;

   public NBTQueryManager(ClientPlayNetHandler p_i49773_1_) {
      this.connection = p_i49773_1_;
   }

   public boolean handleResponse(int p_211548_1_, @Nullable CompoundNBT p_211548_2_) {
      if (this.transactionId == p_211548_1_ && this.handler != null) {
         this.handler.accept(p_211548_2_);
         this.handler = null;
         return true;
      } else {
         return false;
      }
   }

   private int setHandler(Consumer<CompoundNBT> p_211546_1_) {
      this.handler = p_211546_1_;
      return ++this.transactionId;
   }

   public void queryEntity(int entId, Consumer<CompoundNBT> p_211549_2_) {
      int i = this.setHandler(p_211549_2_);
      this.connection.sendPacket(new CQueryEntityNBTPacket(i, entId));
   }

   public void queryTileEntity(BlockPos p_211547_1_, Consumer<CompoundNBT> p_211547_2_) {
      int i = this.setHandler(p_211547_2_);
      this.connection.sendPacket(new CQueryTileEntityNBTPacket(i, p_211547_1_));
   }
}