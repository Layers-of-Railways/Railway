package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;

public class DataPackCommand {
   private static final DynamicCommandExceptionType UNKNOWN_DATA_PACK_EXCEPTION = new DynamicCommandExceptionType((p_208808_0_) -> {
      return new TranslationTextComponent("commands.datapack.unknown", p_208808_0_);
   });
   private static final DynamicCommandExceptionType ENABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208818_0_) -> {
      return new TranslationTextComponent("commands.datapack.enable.failed", p_208818_0_);
   });
   private static final DynamicCommandExceptionType DISABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208815_0_) -> {
      return new TranslationTextComponent("commands.datapack.disable.failed", p_208815_0_);
   });
   private static final SuggestionProvider<CommandSource> SUGGEST_ENABLED_PACK = (p_198305_0_, p_198305_1_) -> {
      return ISuggestionProvider.suggest(p_198305_0_.getSource().getServer().getResourcePacks().getEnabledPacks().stream().map(ResourcePackInfo::getName).map(StringArgumentType::escapeIfRequired), p_198305_1_);
   };
   private static final SuggestionProvider<CommandSource> SUGGEST_AVAILABLE_PACK = (p_198296_0_, p_198296_1_) -> {
      return ISuggestionProvider.suggest(p_198296_0_.getSource().getServer().getResourcePacks().getAvailablePacks().stream().map(ResourcePackInfo::getName).map(StringArgumentType::escapeIfRequired), p_198296_1_);
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("datapack").requires((p_198301_0_) -> {
         return p_198301_0_.hasPermissionLevel(2);
      }).then(Commands.literal("enable").then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_AVAILABLE_PACK).executes((p_198292_0_) -> {
         return enablePack(p_198292_0_.getSource(), parsePackInfo(p_198292_0_, "name", true), (p_198289_0_, p_198289_1_) -> {
            p_198289_1_.getPriority().insert(p_198289_0_, p_198289_1_, (p_198304_0_) -> {
               return p_198304_0_;
            }, false);
         });
      }).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes((p_198307_0_) -> {
         return enablePack(p_198307_0_.getSource(), parsePackInfo(p_198307_0_, "name", true), (p_198308_1_, p_198308_2_) -> {
            p_198308_1_.add(p_198308_1_.indexOf(parsePackInfo(p_198307_0_, "existing", false)) + 1, p_198308_2_);
         });
      }))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes((p_198311_0_) -> {
         return enablePack(p_198311_0_.getSource(), parsePackInfo(p_198311_0_, "name", true), (p_198302_1_, p_198302_2_) -> {
            p_198302_1_.add(p_198302_1_.indexOf(parsePackInfo(p_198311_0_, "existing", false)), p_198302_2_);
         });
      }))).then(Commands.literal("last").executes((p_198298_0_) -> {
         return enablePack(p_198298_0_.getSource(), parsePackInfo(p_198298_0_, "name", true), List::add);
      })).then(Commands.literal("first").executes((p_198300_0_) -> {
         return enablePack(p_198300_0_.getSource(), parsePackInfo(p_198300_0_, "name", true), (p_198310_0_, p_198310_1_) -> {
            p_198310_0_.add(0, p_198310_1_);
         });
      })))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes((p_198295_0_) -> {
         return disablePack(p_198295_0_.getSource(), parsePackInfo(p_198295_0_, "name", false));
      }))).then(Commands.literal("list").executes((p_198290_0_) -> {
         return listAllPacks(p_198290_0_.getSource());
      }).then(Commands.literal("available").executes((p_198288_0_) -> {
         return listAvailablePacks(p_198288_0_.getSource());
      })).then(Commands.literal("enabled").executes((p_198309_0_) -> {
         return listEnabledPacks(p_198309_0_.getSource());
      }))));
   }

   /**
    * Enables the given pack.
    *  
    * @return The number of packs that are loaded after this operation.
    */
   private static int enablePack(CommandSource source, ResourcePackInfo pack, DataPackCommand.IHandler priorityCallback) throws CommandSyntaxException {
      ResourcePackList<ResourcePackInfo> resourcepacklist = source.getServer().getResourcePacks();
      List<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getEnabledPacks());
      priorityCallback.apply(list, pack);
      resourcepacklist.setEnabledPacks(list);
      WorldInfo worldinfo = source.getServer().getWorld(DimensionType.OVERWORLD).getWorldInfo();
      worldinfo.getEnabledDataPacks().clear();
      resourcepacklist.getEnabledPacks().forEach((p_198294_1_) -> {
         worldinfo.getEnabledDataPacks().add(p_198294_1_.getName());
      });
      worldinfo.getDisabledDataPacks().remove(pack.getName());
      source.sendFeedback(new TranslationTextComponent("commands.datapack.enable.success", pack.getChatLink(true)), true);
      source.getServer().reload();
      return resourcepacklist.getEnabledPacks().size();
   }

   /**
    * Disables the given pack.
    *  
    * @return The number of packs that are loaded after this operation.
    */
   private static int disablePack(CommandSource source, ResourcePackInfo pack) {
      ResourcePackList<ResourcePackInfo> resourcepacklist = source.getServer().getResourcePacks();
      List<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getEnabledPacks());
      list.remove(pack);
      resourcepacklist.setEnabledPacks(list);
      WorldInfo worldinfo = source.getServer().getWorld(DimensionType.OVERWORLD).getWorldInfo();
      worldinfo.getEnabledDataPacks().clear();
      resourcepacklist.getEnabledPacks().forEach((p_198291_1_) -> {
         worldinfo.getEnabledDataPacks().add(p_198291_1_.getName());
      });
      worldinfo.getDisabledDataPacks().add(pack.getName());
      source.sendFeedback(new TranslationTextComponent("commands.datapack.disable.success", pack.getChatLink(true)), true);
      source.getServer().reload();
      return resourcepacklist.getEnabledPacks().size();
   }

   /**
    * Sends a list of both enabled and available packs to the user.
    *  
    * @return The total number of packs.
    */
   private static int listAllPacks(CommandSource source) {
      return listEnabledPacks(source) + listAvailablePacks(source);
   }

   /**
    * Sends a list of available packs to the user.
    *  
    * @return The number of available packs.
    */
   private static int listAvailablePacks(CommandSource source) {
      ResourcePackList<ResourcePackInfo> resourcepacklist = source.getServer().getResourcePacks();
      if (resourcepacklist.getAvailablePacks().isEmpty()) {
         source.sendFeedback(new TranslationTextComponent("commands.datapack.list.available.none"), false);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.datapack.list.available.success", resourcepacklist.getAvailablePacks().size(), TextComponentUtils.makeList(resourcepacklist.getAvailablePacks(), (p_198293_0_) -> {
            return p_198293_0_.getChatLink(false);
         })), false);
      }

      return resourcepacklist.getAvailablePacks().size();
   }

   /**
    * Sends a list of enabled packs to the user.
    *  
    * @return The number of enabled packs.
    */
   private static int listEnabledPacks(CommandSource source) {
      ResourcePackList<ResourcePackInfo> resourcepacklist = source.getServer().getResourcePacks();
      if (resourcepacklist.getEnabledPacks().isEmpty()) {
         source.sendFeedback(new TranslationTextComponent("commands.datapack.list.enabled.none"), false);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.datapack.list.enabled.success", resourcepacklist.getEnabledPacks().size(), TextComponentUtils.makeList(resourcepacklist.getEnabledPacks(), (p_198306_0_) -> {
            return p_198306_0_.getChatLink(true);
         })), false);
      }

      return resourcepacklist.getEnabledPacks().size();
   }

   private static ResourcePackInfo parsePackInfo(CommandContext<CommandSource> context, String name, boolean enabling) throws CommandSyntaxException {
      String s = StringArgumentType.getString(context, name);
      ResourcePackList<ResourcePackInfo> resourcepacklist = context.getSource().getServer().getResourcePacks();
      ResourcePackInfo resourcepackinfo = resourcepacklist.getPackInfo(s);
      if (resourcepackinfo == null) {
         throw UNKNOWN_DATA_PACK_EXCEPTION.create(s);
      } else {
         boolean flag = resourcepacklist.getEnabledPacks().contains(resourcepackinfo);
         if (enabling && flag) {
            throw ENABLE_FAILED_EXCEPTION.create(s);
         } else if (!enabling && !flag) {
            throw DISABLE_FAILED_EXCEPTION.create(s);
         } else {
            return resourcepackinfo;
         }
      }
   }

   interface IHandler {
      void apply(List<ResourcePackInfo> p_apply_1_, ResourcePackInfo p_apply_2_) throws CommandSyntaxException;
   }
}