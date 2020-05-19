package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DisplayInfo {
   private final ITextComponent title;
   private final ITextComponent description;
   private final ItemStack icon;
   private final ResourceLocation background;
   private final FrameType frame;
   private final boolean showToast;
   private final boolean announceToChat;
   private final boolean hidden;
   private float x;
   private float y;

   public DisplayInfo(ItemStack icon, ITextComponent title, ITextComponent description, @Nullable ResourceLocation background, FrameType frame, boolean showToast, boolean announceToChat, boolean hidden) {
      this.title = title;
      this.description = description;
      this.icon = icon;
      this.background = background;
      this.frame = frame;
      this.showToast = showToast;
      this.announceToChat = announceToChat;
      this.hidden = hidden;
   }

   public void setPosition(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public ITextComponent getTitle() {
      return this.title;
   }

   public ITextComponent getDescription() {
      return this.description;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getIcon() {
      return this.icon;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getBackground() {
      return this.background;
   }

   public FrameType getFrame() {
      return this.frame;
   }

   @OnlyIn(Dist.CLIENT)
   public float getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public float getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldShowToast() {
      return this.showToast;
   }

   public boolean shouldAnnounceToChat() {
      return this.announceToChat;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public static DisplayInfo deserialize(JsonObject object, JsonDeserializationContext context) {
      ITextComponent itextcomponent = JSONUtils.deserializeClass(object, "title", context, ITextComponent.class);
      ITextComponent itextcomponent1 = JSONUtils.deserializeClass(object, "description", context, ITextComponent.class);
      if (itextcomponent != null && itextcomponent1 != null) {
         ItemStack itemstack = deserializeIcon(JSONUtils.getJsonObject(object, "icon"));
         ResourceLocation resourcelocation = object.has("background") ? new ResourceLocation(JSONUtils.getString(object, "background")) : null;
         FrameType frametype = object.has("frame") ? FrameType.byName(JSONUtils.getString(object, "frame")) : FrameType.TASK;
         boolean flag = JSONUtils.getBoolean(object, "show_toast", true);
         boolean flag1 = JSONUtils.getBoolean(object, "announce_to_chat", true);
         boolean flag2 = JSONUtils.getBoolean(object, "hidden", false);
         return new DisplayInfo(itemstack, itextcomponent, itextcomponent1, resourcelocation, frametype, flag, flag1, flag2);
      } else {
         throw new JsonSyntaxException("Both title and description must be set");
      }
   }

   private static ItemStack deserializeIcon(JsonObject object) {
      if (!object.has("item")) {
         throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
      } else {
         Item item = JSONUtils.getItem(object, "item");
         if (object.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            ItemStack itemstack = new ItemStack(item);
            if (object.has("nbt")) {
               try {
                  CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(JSONUtils.getString(object.get("nbt"), "nbt"));
                  itemstack.setTag(compoundnbt);
               } catch (CommandSyntaxException commandsyntaxexception) {
                  throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
               }
            }

            return itemstack;
         }
      }
   }

   public void write(PacketBuffer buf) {
      buf.writeTextComponent(this.title);
      buf.writeTextComponent(this.description);
      buf.writeItemStack(this.icon);
      buf.writeEnumValue(this.frame);
      int i = 0;
      if (this.background != null) {
         i |= 1;
      }

      if (this.showToast) {
         i |= 2;
      }

      if (this.hidden) {
         i |= 4;
      }

      buf.writeInt(i);
      if (this.background != null) {
         buf.writeResourceLocation(this.background);
      }

      buf.writeFloat(this.x);
      buf.writeFloat(this.y);
   }

   public static DisplayInfo read(PacketBuffer buf) {
      ITextComponent itextcomponent = buf.readTextComponent();
      ITextComponent itextcomponent1 = buf.readTextComponent();
      ItemStack itemstack = buf.readItemStack();
      FrameType frametype = buf.readEnumValue(FrameType.class);
      int i = buf.readInt();
      ResourceLocation resourcelocation = (i & 1) != 0 ? buf.readResourceLocation() : null;
      boolean flag = (i & 2) != 0;
      boolean flag1 = (i & 4) != 0;
      DisplayInfo displayinfo = new DisplayInfo(itemstack, itextcomponent, itextcomponent1, resourcelocation, frametype, flag, false, flag1);
      displayinfo.setPosition(buf.readFloat(), buf.readFloat());
      return displayinfo;
   }

   public JsonElement serialize() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("icon", this.serializeIcon());
      jsonobject.add("title", ITextComponent.Serializer.toJsonTree(this.title));
      jsonobject.add("description", ITextComponent.Serializer.toJsonTree(this.description));
      jsonobject.addProperty("frame", this.frame.getName());
      jsonobject.addProperty("show_toast", this.showToast);
      jsonobject.addProperty("announce_to_chat", this.announceToChat);
      jsonobject.addProperty("hidden", this.hidden);
      if (this.background != null) {
         jsonobject.addProperty("background", this.background.toString());
      }

      return jsonobject;
   }

   private JsonObject serializeIcon() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("item", Registry.ITEM.getKey(this.icon.getItem()).toString());
      if (this.icon.hasTag()) {
         jsonobject.addProperty("nbt", this.icon.getTag().toString());
      }

      return jsonobject;
   }
}