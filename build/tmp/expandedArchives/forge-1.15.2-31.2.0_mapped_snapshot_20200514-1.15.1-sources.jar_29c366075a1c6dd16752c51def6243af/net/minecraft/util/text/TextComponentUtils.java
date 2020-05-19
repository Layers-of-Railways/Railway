package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public class TextComponentUtils {
   public static ITextComponent mergeStyles(ITextComponent component, Style styleIn) {
      if (styleIn.isEmpty()) {
         return component;
      } else {
         return component.getStyle().isEmpty() ? component.setStyle(styleIn.createShallowCopy()) : (new StringTextComponent("")).appendSibling(component).setStyle(styleIn.createShallowCopy());
      }
   }

   public static ITextComponent updateForEntity(@Nullable CommandSource p_197680_0_, ITextComponent p_197680_1_, @Nullable Entity p_197680_2_, int p_197680_3_) throws CommandSyntaxException {
      if (p_197680_3_ > 100) {
         return p_197680_1_;
      } else {
         ++p_197680_3_;
         ITextComponent itextcomponent = p_197680_1_ instanceof ITargetedTextComponent ? ((ITargetedTextComponent)p_197680_1_).createNames(p_197680_0_, p_197680_2_, p_197680_3_) : p_197680_1_.shallowCopy();

         for(ITextComponent itextcomponent1 : p_197680_1_.getSiblings()) {
            itextcomponent.appendSibling(updateForEntity(p_197680_0_, itextcomponent1, p_197680_2_, p_197680_3_));
         }

         return mergeStyles(itextcomponent, p_197680_1_.getStyle());
      }
   }

   public static ITextComponent getDisplayName(GameProfile profile) {
      if (profile.getName() != null) {
         return new StringTextComponent(profile.getName());
      } else {
         return profile.getId() != null ? new StringTextComponent(profile.getId().toString()) : new StringTextComponent("(unknown)");
      }
   }

   public static ITextComponent makeGreenSortedList(Collection<String> collection) {
      return makeSortedList(collection, (p_197681_0_) -> {
         return (new StringTextComponent(p_197681_0_)).applyTextStyle(TextFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> ITextComponent makeSortedList(Collection<T> collection, Function<T, ITextComponent> toTextComponent) {
      if (collection.isEmpty()) {
         return new StringTextComponent("");
      } else if (collection.size() == 1) {
         return toTextComponent.apply(collection.iterator().next());
      } else {
         List<T> list = Lists.newArrayList(collection);
         list.sort(Comparable::compareTo);
         return makeList(list, toTextComponent);
      }
   }

   public static <T> ITextComponent makeList(Collection<T> collection, Function<T, ITextComponent> toTextComponent) {
      if (collection.isEmpty()) {
         return new StringTextComponent("");
      } else if (collection.size() == 1) {
         return toTextComponent.apply(collection.iterator().next());
      } else {
         ITextComponent itextcomponent = new StringTextComponent("");
         boolean flag = true;

         for(T t : collection) {
            if (!flag) {
               itextcomponent.appendSibling((new StringTextComponent(", ")).applyTextStyle(TextFormatting.GRAY));
            }

            itextcomponent.appendSibling(toTextComponent.apply(t));
            flag = false;
         }

         return itextcomponent;
      }
   }

   public static ITextComponent wrapInSquareBrackets(ITextComponent component) {
      return (new StringTextComponent("[")).appendSibling(component).appendText("]");
   }

   public static ITextComponent toTextComponent(Message message) {
      return (ITextComponent)(message instanceof ITextComponent ? (ITextComponent)message : new StringTextComponent(message.getString()));
   }
}