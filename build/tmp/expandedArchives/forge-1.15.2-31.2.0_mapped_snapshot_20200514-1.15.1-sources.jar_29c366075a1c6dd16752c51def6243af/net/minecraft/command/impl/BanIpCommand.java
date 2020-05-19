package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.server.management.IPBanList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BanIpCommand {
   public static final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType IP_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.invalid"));
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("ban-ip").requires((p_198222_0_) -> {
         return p_198222_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198222_0_.hasPermissionLevel(3);
      }).then(Commands.argument("target", StringArgumentType.word()).executes((p_198219_0_) -> {
         return banUsernameOrIp(p_198219_0_.getSource(), StringArgumentType.getString(p_198219_0_, "target"), (ITextComponent)null);
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198221_0_) -> {
         return banUsernameOrIp(p_198221_0_.getSource(), StringArgumentType.getString(p_198221_0_, "target"), MessageArgument.getMessage(p_198221_0_, "reason"));
      }))));
   }

   private static int banUsernameOrIp(CommandSource source, String username, @Nullable ITextComponent reason) throws CommandSyntaxException {
      Matcher matcher = IP_PATTERN.matcher(username);
      if (matcher.matches()) {
         return banIpAddress(source, username, reason);
      } else {
         ServerPlayerEntity serverplayerentity = source.getServer().getPlayerList().getPlayerByUsername(username);
         if (serverplayerentity != null) {
            return banIpAddress(source, serverplayerentity.getPlayerIP(), reason);
         } else {
            throw IP_INVALID.create();
         }
      }
   }

   private static int banIpAddress(CommandSource source, String ip, @Nullable ITextComponent reason) throws CommandSyntaxException {
      IPBanList ipbanlist = source.getServer().getPlayerList().getBannedIPs();
      if (ipbanlist.isBanned(ip)) {
         throw FAILED_EXCEPTION.create();
      } else {
         List<ServerPlayerEntity> list = source.getServer().getPlayerList().getPlayersMatchingAddress(ip);
         IPBanEntry ipbanentry = new IPBanEntry(ip, (Date)null, source.getName(), (Date)null, reason == null ? null : reason.getString());
         ipbanlist.addEntry(ipbanentry);
         source.sendFeedback(new TranslationTextComponent("commands.banip.success", ip, ipbanentry.getBanReason()), true);
         if (!list.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent("commands.banip.info", list.size(), EntitySelector.joinNames(list)), true);
         }

         for(ServerPlayerEntity serverplayerentity : list) {
            serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.ip_banned"));
         }

         return list.size();
      }
   }
}