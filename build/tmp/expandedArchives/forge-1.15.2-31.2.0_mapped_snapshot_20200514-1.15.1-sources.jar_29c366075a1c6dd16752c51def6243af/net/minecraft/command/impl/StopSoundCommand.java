package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;

public class StopSoundCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      RequiredArgumentBuilder<CommandSource, EntitySelector> requiredargumentbuilder = Commands.argument("targets", EntityArgument.players()).executes((p_198729_0_) -> {
         return stopSound(p_198729_0_.getSource(), EntityArgument.getPlayers(p_198729_0_, "targets"), (SoundCategory)null, (ResourceLocation)null);
      }).then(Commands.literal("*").then(Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_198732_0_) -> {
         return stopSound(p_198732_0_.getSource(), EntityArgument.getPlayers(p_198732_0_, "targets"), (SoundCategory)null, ResourceLocationArgument.getResourceLocation(p_198732_0_, "sound"));
      })));

      for(SoundCategory soundcategory : SoundCategory.values()) {
         requiredargumentbuilder.then(Commands.literal(soundcategory.getName()).executes((p_198731_1_) -> {
            return stopSound(p_198731_1_.getSource(), EntityArgument.getPlayers(p_198731_1_, "targets"), soundcategory, (ResourceLocation)null);
         }).then(Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_198728_1_) -> {
            return stopSound(p_198728_1_.getSource(), EntityArgument.getPlayers(p_198728_1_, "targets"), soundcategory, ResourceLocationArgument.getResourceLocation(p_198728_1_, "sound"));
         })));
      }

      dispatcher.register(Commands.literal("stopsound").requires((p_198734_0_) -> {
         return p_198734_0_.hasPermissionLevel(2);
      }).then(requiredargumentbuilder));
   }

   private static int stopSound(CommandSource source, Collection<ServerPlayerEntity> targets, @Nullable SoundCategory category, @Nullable ResourceLocation soundIn) {
      SStopSoundPacket sstopsoundpacket = new SStopSoundPacket(soundIn, category);

      for(ServerPlayerEntity serverplayerentity : targets) {
         serverplayerentity.connection.sendPacket(sstopsoundpacket);
      }

      if (category != null) {
         if (soundIn != null) {
            source.sendFeedback(new TranslationTextComponent("commands.stopsound.success.source.sound", soundIn, category.getName()), true);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.stopsound.success.source.any", category.getName()), true);
         }
      } else if (soundIn != null) {
         source.sendFeedback(new TranslationTextComponent("commands.stopsound.success.sourceless.sound", soundIn), true);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.stopsound.success.sourceless.any"), true);
      }

      return targets.size();
   }
}