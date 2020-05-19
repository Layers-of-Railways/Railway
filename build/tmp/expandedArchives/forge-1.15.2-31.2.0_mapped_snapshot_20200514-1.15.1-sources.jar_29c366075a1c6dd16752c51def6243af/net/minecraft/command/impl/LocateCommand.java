package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class LocateCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.locate.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("locate").requires((p_198533_0_) -> {
         return p_198533_0_.hasPermissionLevel(2);
      }).then(Commands.literal("Pillager_Outpost").executes((p_198530_0_) -> {
         return locateStructure(p_198530_0_.getSource(), "Pillager_Outpost");
      })).then(Commands.literal("Mineshaft").executes((p_198535_0_) -> {
         return locateStructure(p_198535_0_.getSource(), "Mineshaft");
      })).then(Commands.literal("Mansion").executes((p_198527_0_) -> {
         return locateStructure(p_198527_0_.getSource(), "Mansion");
      })).then(Commands.literal("Igloo").executes((p_198529_0_) -> {
         return locateStructure(p_198529_0_.getSource(), "Igloo");
      })).then(Commands.literal("Desert_Pyramid").executes((p_198526_0_) -> {
         return locateStructure(p_198526_0_.getSource(), "Desert_Pyramid");
      })).then(Commands.literal("Jungle_Pyramid").executes((p_198531_0_) -> {
         return locateStructure(p_198531_0_.getSource(), "Jungle_Pyramid");
      })).then(Commands.literal("Swamp_Hut").executes((p_198525_0_) -> {
         return locateStructure(p_198525_0_.getSource(), "Swamp_Hut");
      })).then(Commands.literal("Stronghold").executes((p_198532_0_) -> {
         return locateStructure(p_198532_0_.getSource(), "Stronghold");
      })).then(Commands.literal("Monument").executes((p_202686_0_) -> {
         return locateStructure(p_202686_0_.getSource(), "Monument");
      })).then(Commands.literal("Fortress").executes((p_202685_0_) -> {
         return locateStructure(p_202685_0_.getSource(), "Fortress");
      })).then(Commands.literal("EndCity").executes((p_202687_0_) -> {
         return locateStructure(p_202687_0_.getSource(), "EndCity");
      })).then(Commands.literal("Ocean_Ruin").executes((p_204104_0_) -> {
         return locateStructure(p_204104_0_.getSource(), "Ocean_Ruin");
      })).then(Commands.literal("Buried_Treasure").executes((p_204297_0_) -> {
         return locateStructure(p_204297_0_.getSource(), "Buried_Treasure");
      })).then(Commands.literal("Shipwreck").executes((p_204758_0_) -> {
         return locateStructure(p_204758_0_.getSource(), "Shipwreck");
      })).then(Commands.literal("Village").executes((p_218858_0_) -> {
         return locateStructure(p_218858_0_.getSource(), "Village");
      // FORGE: Support modded structures via registry name
      })).then(Commands.argument("structure_type", net.minecraft.command.arguments.ResourceLocationArgument.resourceLocation())
              .suggests((ctx, sb) -> net.minecraft.command.ISuggestionProvider.suggest(
                      net.minecraftforge.registries.GameData.getStructureFeatures().keySet().stream()
                          .map(net.minecraft.util.ResourceLocation::toString), sb))
              .executes(ctx -> locateStructure(ctx.getSource(), ctx.getArgument("structure_type", net.minecraft.util.ResourceLocation.class).toString().replace("minecraft:", ""))
      )));
   }

   private static int locateStructure(CommandSource source, String structureName) throws CommandSyntaxException {
      BlockPos blockpos = new BlockPos(source.getPos());
      BlockPos blockpos1 = source.getWorld().findNearestStructure(structureName, blockpos, 100, false);
      if (blockpos1 == null) {
         throw FAILED_EXCEPTION.create();
      } else {
         int i = MathHelper.floor(getDistance(blockpos.getX(), blockpos.getZ(), blockpos1.getX(), blockpos1.getZ()));
         ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", blockpos1.getX(), "~", blockpos1.getZ())).applyTextStyle((p_211746_1_) -> {
            p_211746_1_.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockpos1.getX() + " ~ " + blockpos1.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")));
         });
         source.sendFeedback(new TranslationTextComponent("commands.locate.success", structureName, itextcomponent, i), false);
         return i;
      }
   }

   private static float getDistance(int x1, int z1, int x2, int z2) {
      int i = x2 - x1;
      int j = z2 - z1;
      return MathHelper.sqrt((float)(i * i + j * j));
   }
}