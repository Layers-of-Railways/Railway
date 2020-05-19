package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NarratorChatListener implements IChatListener {
   public static final ITextComponent EMPTY = new StringTextComponent("");
   private static final Logger LOGGER = LogManager.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   /**
    * Called whenever this listener receives a chat message, if this listener is registered to the given type in {@link
    * net.minecraft.client.gui.GuiIngame#chatListeners chatListeners}
    */
   public void say(ChatType chatTypeIn, ITextComponent message) {
      NarratorStatus narratorstatus = getNarratorStatus();
      if (narratorstatus != NarratorStatus.OFF && this.narrator.active()) {
         if (narratorstatus == NarratorStatus.ALL || narratorstatus == NarratorStatus.CHAT && chatTypeIn == ChatType.CHAT || narratorstatus == NarratorStatus.SYSTEM && chatTypeIn == ChatType.SYSTEM) {
            ITextComponent itextcomponent;
            if (message instanceof TranslationTextComponent && "chat.type.text".equals(((TranslationTextComponent)message).getKey())) {
               itextcomponent = new TranslationTextComponent("chat.type.text.narrate", ((TranslationTextComponent)message).getFormatArgs());
            } else {
               itextcomponent = message;
            }

            this.say(chatTypeIn.getInterrupts(), itextcomponent.getString());
         }

      }
   }

   public void say(String p_216864_1_) {
      NarratorStatus narratorstatus = getNarratorStatus();
      if (this.narrator.active() && narratorstatus != NarratorStatus.OFF && narratorstatus != NarratorStatus.CHAT && !p_216864_1_.isEmpty()) {
         this.narrator.clear();
         this.say(true, p_216864_1_);
      }

   }

   private static NarratorStatus getNarratorStatus() {
      return Minecraft.getInstance().gameSettings.narrator;
   }

   private void say(boolean p_216866_1_, String p_216866_2_) {
      if (SharedConstants.developmentMode) {
         LOGGER.debug("Narrating: {}", (Object)p_216866_2_);
      }

      this.narrator.say(p_216866_2_, p_216866_1_);
   }

   public void announceMode(NarratorStatus p_216865_1_) {
      this.clear();
      this.narrator.say((new TranslationTextComponent("options.narrator")).getString() + " : " + (new TranslationTextComponent(p_216865_1_.getResourceKey())).getString(), true);
      ToastGui toastgui = Minecraft.getInstance().getToastGui();
      if (this.narrator.active()) {
         if (p_216865_1_ == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(toastgui, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.disabled"), (ITextComponent)null);
         } else {
            SystemToast.addOrUpdate(toastgui, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.enabled"), new TranslationTextComponent(p_216865_1_.getResourceKey()));
         }
      } else {
         SystemToast.addOrUpdate(toastgui, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.disabled"), new TranslationTextComponent("options.narrator.notavailable"));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      if (getNarratorStatus() != NarratorStatus.OFF && this.narrator.active()) {
         this.narrator.clear();
      }
   }

   public void close() {
      this.narrator.destroy();
   }
}