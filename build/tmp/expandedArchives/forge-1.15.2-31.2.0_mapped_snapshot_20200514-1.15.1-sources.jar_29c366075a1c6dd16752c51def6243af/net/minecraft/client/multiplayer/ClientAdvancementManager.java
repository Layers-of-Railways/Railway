package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientAdvancementManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final AdvancementList advancementList = new AdvancementList();
   private final Map<Advancement, AdvancementProgress> advancementToProgress = Maps.newHashMap();
   @Nullable
   private ClientAdvancementManager.IListener listener;
   @Nullable
   private Advancement selectedTab;

   public ClientAdvancementManager(Minecraft p_i47380_1_) {
      this.mc = p_i47380_1_;
   }

   public void read(SAdvancementInfoPacket packetIn) {
      if (packetIn.isFirstSync()) {
         this.advancementList.clear();
         this.advancementToProgress.clear();
      }

      this.advancementList.removeAll(packetIn.getAdvancementsToRemove());
      this.advancementList.loadAdvancements(packetIn.getAdvancementsToAdd());

      for(Entry<ResourceLocation, AdvancementProgress> entry : packetIn.getProgressUpdates().entrySet()) {
         Advancement advancement = this.advancementList.getAdvancement(entry.getKey());
         if (advancement != null) {
            AdvancementProgress advancementprogress = entry.getValue();
            advancementprogress.update(advancement.getCriteria(), advancement.getRequirements());
            this.advancementToProgress.put(advancement, advancementprogress);
            if (this.listener != null) {
               this.listener.onUpdateAdvancementProgress(advancement, advancementprogress);
            }

            if (!packetIn.isFirstSync() && advancementprogress.isDone() && advancement.getDisplay() != null && advancement.getDisplay().shouldShowToast()) {
               this.mc.getToastGui().add(new AdvancementToast(advancement));
            }
         } else {
            LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
         }
      }

   }

   public AdvancementList getAdvancementList() {
      return this.advancementList;
   }

   public void setSelectedTab(@Nullable Advancement advancementIn, boolean tellServer) {
      ClientPlayNetHandler clientplaynethandler = this.mc.getConnection();
      if (clientplaynethandler != null && advancementIn != null && tellServer) {
         clientplaynethandler.sendPacket(CSeenAdvancementsPacket.openedTab(advancementIn));
      }

      if (this.selectedTab != advancementIn) {
         this.selectedTab = advancementIn;
         if (this.listener != null) {
            this.listener.setSelectedTab(advancementIn);
         }
      }

   }

   public void setListener(@Nullable ClientAdvancementManager.IListener listenerIn) {
      this.listener = listenerIn;
      this.advancementList.setListener(listenerIn);
      if (listenerIn != null) {
         for(Entry<Advancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
            listenerIn.onUpdateAdvancementProgress(entry.getKey(), entry.getValue());
         }

         listenerIn.setSelectedTab(this.selectedTab);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public interface IListener extends AdvancementList.IListener {
      void onUpdateAdvancementProgress(Advancement advancementIn, AdvancementProgress progress);

      void setSelectedTab(@Nullable Advancement advancementIn);
   }
}