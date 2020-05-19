package net.minecraft.client.gui.chat;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IChatListener {
   /**
    * Called whenever this listener receives a chat message, if this listener is registered to the given type in {@link
    * net.minecraft.client.gui.GuiIngame#chatListeners chatListeners}
    */
   void say(ChatType chatTypeIn, ITextComponent message);
}