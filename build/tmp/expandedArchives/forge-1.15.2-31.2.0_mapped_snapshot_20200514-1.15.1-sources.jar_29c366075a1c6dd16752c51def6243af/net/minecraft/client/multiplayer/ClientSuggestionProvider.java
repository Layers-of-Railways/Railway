package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSuggestionProvider implements ISuggestionProvider {
   private final ClientPlayNetHandler connection;
   private final Minecraft mc;
   private int currentTransaction = -1;
   private CompletableFuture<Suggestions> future;

   public ClientSuggestionProvider(ClientPlayNetHandler p_i49558_1_, Minecraft p_i49558_2_) {
      this.connection = p_i49558_1_;
      this.mc = p_i49558_2_;
   }

   public Collection<String> getPlayerNames() {
      List<String> list = Lists.newArrayList();

      for(NetworkPlayerInfo networkplayerinfo : this.connection.getPlayerInfoMap()) {
         list.add(networkplayerinfo.getGameProfile().getName());
      }

      return list;
   }

   public Collection<String> getTargetedEntity() {
      return (Collection<String>)(this.mc.objectMouseOver != null && this.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY ? Collections.singleton(((EntityRayTraceResult)this.mc.objectMouseOver).getEntity().getCachedUniqueIdString()) : Collections.emptyList());
   }

   public Collection<String> getTeamNames() {
      return this.connection.getWorld().getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getSoundResourceLocations() {
      return this.mc.getSoundHandler().getAvailableSounds();
   }

   public Stream<ResourceLocation> getRecipeResourceLocations() {
      return this.connection.getRecipeManager().getKeys();
   }

   public boolean hasPermissionLevel(int level) {
      ClientPlayerEntity clientplayerentity = this.mc.player;
      return clientplayerentity != null ? clientplayerentity.hasPermissionLevel(level) : level == 0;
   }

   public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> context, SuggestionsBuilder suggestionsBuilder) {
      if (this.future != null) {
         this.future.cancel(false);
      }

      this.future = new CompletableFuture<>();
      int i = ++this.currentTransaction;
      this.connection.sendPacket(new CTabCompletePacket(i, context.getInput()));
      return this.future;
   }

   private static String formatDouble(double p_209001_0_) {
      return String.format(Locale.ROOT, "%.2f", p_209001_0_);
   }

   private static String formatInt(int p_209002_0_) {
      return Integer.toString(p_209002_0_);
   }

   public Collection<ISuggestionProvider.Coordinates> func_217294_q() {
      RayTraceResult raytraceresult = this.mc.objectMouseOver;
      if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
         return Collections.singleton(new ISuggestionProvider.Coordinates(formatInt(blockpos.getX()), formatInt(blockpos.getY()), formatInt(blockpos.getZ())));
      } else {
         return ISuggestionProvider.super.func_217294_q();
      }
   }

   public Collection<ISuggestionProvider.Coordinates> func_217293_r() {
      RayTraceResult raytraceresult = this.mc.objectMouseOver;
      if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
         Vec3d vec3d = raytraceresult.getHitVec();
         return Collections.singleton(new ISuggestionProvider.Coordinates(formatDouble(vec3d.x), formatDouble(vec3d.y), formatDouble(vec3d.z)));
      } else {
         return ISuggestionProvider.super.func_217293_r();
      }
   }

   public void handleResponse(int transaction, Suggestions result) {
      if (transaction == this.currentTransaction) {
         this.future.complete(result);
         this.future = null;
         this.currentTransaction = -1;
      }

   }
}