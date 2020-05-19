package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CustomServerBossInfoManager {
   private final MinecraftServer server;
   private final Map<ResourceLocation, CustomServerBossInfo> bars = Maps.newHashMap();

   public CustomServerBossInfoManager(MinecraftServer serverIn) {
      this.server = serverIn;
   }

   @Nullable
   public CustomServerBossInfo get(ResourceLocation id) {
      return this.bars.get(id);
   }

   public CustomServerBossInfo add(ResourceLocation id, ITextComponent p_201379_2_) {
      CustomServerBossInfo customserverbossinfo = new CustomServerBossInfo(id, p_201379_2_);
      this.bars.put(id, customserverbossinfo);
      return customserverbossinfo;
   }

   public void remove(CustomServerBossInfo bossbar) {
      this.bars.remove(bossbar.getId());
   }

   public Collection<ResourceLocation> getIDs() {
      return this.bars.keySet();
   }

   public Collection<CustomServerBossInfo> getBossbars() {
      return this.bars.values();
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();

      for(CustomServerBossInfo customserverbossinfo : this.bars.values()) {
         compoundnbt.put(customserverbossinfo.getId().toString(), customserverbossinfo.write());
      }

      return compoundnbt;
   }

   public void read(CompoundNBT p_201381_1_) {
      for(String s : p_201381_1_.keySet()) {
         ResourceLocation resourcelocation = new ResourceLocation(s);
         this.bars.put(resourcelocation, CustomServerBossInfo.read(p_201381_1_.getCompound(s), resourcelocation));
      }

   }

   public void onPlayerLogin(ServerPlayerEntity player) {
      for(CustomServerBossInfo customserverbossinfo : this.bars.values()) {
         customserverbossinfo.onPlayerLogin(player);
      }

   }

   public void onPlayerLogout(ServerPlayerEntity player) {
      for(CustomServerBossInfo customserverbossinfo : this.bars.values()) {
         customserverbossinfo.onPlayerLogout(player);
      }

   }
}