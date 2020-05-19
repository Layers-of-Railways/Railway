package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderComponentsUtil {
   public static String removeTextColorsIfConfigured(String text, boolean forceColor) {
      return !forceColor && !Minecraft.getInstance().gameSettings.chatColor ? TextFormatting.getTextWithoutFormattingCodes(text) : text;
   }

   public static List<ITextComponent> splitText(ITextComponent textComponent, int maxTextLenght, FontRenderer fontRendererIn, boolean trimSpace, boolean forceTextColor) {
      int i = 0;
      ITextComponent itextcomponent = new StringTextComponent("");
      List<ITextComponent> list = Lists.newArrayList();
      List<ITextComponent> list1 = Lists.newArrayList(textComponent);

      for(int j = 0; j < list1.size(); ++j) {
         ITextComponent itextcomponent1 = list1.get(j);
         String s = itextcomponent1.getUnformattedComponentText();
         boolean flag = false;
         if (s.contains("\n")) {
            int k = s.indexOf(10);
            String s1 = s.substring(k + 1);
            s = s.substring(0, k + 1);
            ITextComponent itextcomponent2 = (new StringTextComponent(s1)).setStyle(itextcomponent1.getStyle().createShallowCopy());
            list1.add(j + 1, itextcomponent2);
            flag = true;
         }

         String s4 = removeTextColorsIfConfigured(itextcomponent1.getStyle().getFormattingCode() + s, forceTextColor);
         String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
         int i1 = fontRendererIn.getStringWidth(s5);
         ITextComponent itextcomponent3 = (new StringTextComponent(s5)).setStyle(itextcomponent1.getStyle().createShallowCopy());
         if (i + i1 > maxTextLenght) {
            String s2 = fontRendererIn.trimStringToWidth(s4, maxTextLenght - i, false);
            String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;
            if (s3 != null && !s3.isEmpty()) {
               int l = s3.charAt(0) != ' ' ? s2.lastIndexOf(32) : s2.length();
               if (l >= 0 && fontRendererIn.getStringWidth(s4.substring(0, l)) > 0) {
                  s2 = s4.substring(0, l);
                  if (trimSpace) {
                     ++l;
                  }

                  s3 = s4.substring(l);
               } else if (i > 0 && !s4.contains(" ")) {
                  s2 = "";
                  s3 = s4;
               }
               s3 = TextFormatting.getFormatString(s2) + s3; //Forge: Fix chat formatting not surviving line wrapping.

               ITextComponent itextcomponent4 = (new StringTextComponent(s3)).setStyle(itextcomponent1.getStyle().createShallowCopy());
               list1.add(j + 1, itextcomponent4);
            }

            i1 = fontRendererIn.getStringWidth(s2);
            itextcomponent3 = new StringTextComponent(s2);
            itextcomponent3.setStyle(itextcomponent1.getStyle().createShallowCopy());
            flag = true;
         }

         if (i + i1 <= maxTextLenght) {
            i += i1;
            itextcomponent.appendSibling(itextcomponent3);
         } else {
            flag = true;
         }

         if (flag) {
            list.add(itextcomponent);
            i = 0;
            itextcomponent = new StringTextComponent("");
         }
      }

      list.add(itextcomponent);
      return list;
   }
}