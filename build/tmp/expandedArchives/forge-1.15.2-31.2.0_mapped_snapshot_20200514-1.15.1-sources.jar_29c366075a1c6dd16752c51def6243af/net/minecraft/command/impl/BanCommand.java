package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class BanCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.ban.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("ban").requires((p_198238_0_) -> {
         return p_198238_0_.getServer().getPlayerList().getBannedPlayers().isLanServer() && p_198238_0_.hasPermissionLevel(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).executes((p_198234_0_) -> {
         return banGameProfiles(p_198234_0_.getSource(), GameProfileArgument.getGameProfiles(p_198234_0_, "targets"), (ITextComponent)null);
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198237_0_) -> {
         return banGameProfiles(p_198237_0_.getSource(), GameProfileArgument.getGameProfiles(p_198237_0_, "targets"), MessageArgument.getMessage(p_198237_0_, "reason"));
      }))));
   }

   private static int banGameProfiles(CommandSource source, Collection<GameProfile> gameProfiles, @Nullable ITextComponent reason) throws CommandSyntaxException {
      BanList banlist = source.getServer().getPlayerList().getBannedPlayers();
      int i = 0;

      for(GameProfile gameprofile : gameProfiles) {
         if (!banlist.isBanned(gameprofile)) {
            ProfileBanEntry profilebanentry = new ProfileBanEntry(gameprofile, (Date)null, source.getName(), (Date)null, reason == null ? null : reason.getString());
            banlist.addEntry(profilebanentry);
            ++i;
            source.sendFeedback(new TranslationTextComponent("commands.ban.success", TextComponentUtils.getDisplayName(gameprofile), profilebanentry.getBanReason()), true);
            ServerPlayerEntity serverplayerentity = source.getServer().getPlayerList().getPlayerByUUID(gameprofile.getId());
            if (serverplayerentity != null) {
               serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.banned"));
            }
         }
      }

      if (i == 0) {
         throw FAILED_EXCEPTION.create();
      } else {
         return i;
      }
   }
}