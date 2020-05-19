package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class SeedCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("seed").requires((p_198673_0_) -> {
         return p_198673_0_.getServer().isSinglePlayer() || p_198673_0_.hasPermissionLevel(2);
      }).executes((p_198672_0_) -> {
         long i = p_198672_0_.getSource().getWorld().getSeed();
         ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets((new StringTextComponent(String.valueOf(i))).applyTextStyle((p_211752_2_) -> {
            p_211752_2_.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(i))).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.copy.click"))).setInsertion(String.valueOf(i));
         }));
         p_198672_0_.getSource().sendFeedback(new TranslationTextComponent("commands.seed.success", itextcomponent), false);
         return (int)i;
      }));
   }
}