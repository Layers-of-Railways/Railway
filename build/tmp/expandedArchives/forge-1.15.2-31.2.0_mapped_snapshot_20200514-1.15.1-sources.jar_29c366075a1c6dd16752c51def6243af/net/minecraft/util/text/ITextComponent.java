package net.minecraft.util.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public interface ITextComponent extends Message, Iterable<ITextComponent> {
   /**
    * Sets the style of this component and updates the parent style of all of the sibling components.
    */
   ITextComponent setStyle(Style style);

   /**
    * Gets the style of this component. Returns a direct reference; changes to this style will modify the style of this
    * component (IE, there is no need to call {@link #setStyle(Style)} again after modifying it).
    *  
    * If this component's style is currently <code>null</code>, it will be initialized to the default style, and the
    * parent style of all sibling components will be set to that style. (IE, changes to this style will also be
    * reflected in sibling components.)
    *  
    * This method never returns <code>null</code>.
    */
   Style getStyle();

   /**
    * Adds a new component to the end of the sibling list, with the specified text. Same as calling {@link
    * #appendSibling(ITextComponent)} with a new {@link TextComponentString}.
    *  
    * @return This component, for chaining (and not the newly added component)
    */
   default ITextComponent appendText(String text) {
      return this.appendSibling(new StringTextComponent(text));
   }

   /**
    * Adds a new component to the end of the sibling list, setting that component's style's parent style to this
    * component's style.
    *  
    * @return This component, for chaining (and not the newly added component)
    */
   ITextComponent appendSibling(ITextComponent component);

   /**
    * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
    * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
    * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
    */
   String getUnformattedComponentText();

   default String getString() {
      StringBuilder stringbuilder = new StringBuilder();
      this.stream().forEach((p_212635_1_) -> {
         stringbuilder.append(p_212635_1_.getUnformattedComponentText());
      });
      return stringbuilder.toString();
   }

   default String getStringTruncated(int maxLen) {
      StringBuilder stringbuilder = new StringBuilder();
      Iterator<ITextComponent> iterator = this.stream().iterator();

      while(iterator.hasNext()) {
         int i = maxLen - stringbuilder.length();
         if (i <= 0) {
            break;
         }

         String s = iterator.next().getUnformattedComponentText();
         stringbuilder.append(s.length() <= i ? s : s.substring(0, i));
      }

      return stringbuilder.toString();
   }

   /**
    * Gets the text of this component <em>and all sibling components</em>, with formatting codes added for rendering.
    */
   default String getFormattedText() {
      StringBuilder stringbuilder = new StringBuilder();
      String s = "";
      Iterator<ITextComponent> iterator = this.stream().iterator();

      while(iterator.hasNext()) {
         ITextComponent itextcomponent = iterator.next();
         String s1 = itextcomponent.getUnformattedComponentText();
         if (!s1.isEmpty()) {
            String s2 = itextcomponent.getStyle().getFormattingCode();
            if (!s2.equals(s)) {
               if (!s.isEmpty()) {
                  stringbuilder.append((Object)TextFormatting.RESET);
               }

               stringbuilder.append(s2);
               s = s2;
            }

            stringbuilder.append(s1);
         }
      }

      if (!s.isEmpty()) {
         stringbuilder.append((Object)TextFormatting.RESET);
      }

      return stringbuilder.toString();
   }

   /**
    * Gets the sibling components of this one.
    */
   List<ITextComponent> getSiblings();

   Stream<ITextComponent> stream();

   default Stream<ITextComponent> func_212637_f() {
      return this.stream().map(ITextComponent::copyWithoutSiblings);
   }

   default Iterator<ITextComponent> iterator() {
      return this.func_212637_f().iterator();
   }

   /**
    * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
    */
   ITextComponent shallowCopy();

   default ITextComponent deepCopy() {
      ITextComponent itextcomponent = this.shallowCopy();
      itextcomponent.setStyle(this.getStyle().createShallowCopy());

      for(ITextComponent itextcomponent1 : this.getSiblings()) {
         itextcomponent.appendSibling(itextcomponent1.deepCopy());
      }

      return itextcomponent;
   }

   default ITextComponent applyTextStyle(Consumer<Style> styleConsumer) {
      styleConsumer.accept(this.getStyle());
      return this;
   }

   default ITextComponent applyTextStyles(TextFormatting... colors) {
      for(TextFormatting textformatting : colors) {
         this.applyTextStyle(textformatting);
      }

      return this;
   }

   default ITextComponent applyTextStyle(TextFormatting color) {
      Style style = this.getStyle();
      if (color.isColor()) {
         style.setColor(color);
      }

      if (color.isFancyStyling()) {
         switch(color) {
         case OBFUSCATED:
            style.setObfuscated(true);
            break;
         case BOLD:
            style.setBold(true);
            break;
         case STRIKETHROUGH:
            style.setStrikethrough(true);
            break;
         case UNDERLINE:
            style.setUnderlined(true);
            break;
         case ITALIC:
            style.setItalic(true);
         }
      }

      return this;
   }

   static ITextComponent copyWithoutSiblings(ITextComponent textComponent) {
      ITextComponent itextcomponent = textComponent.shallowCopy();
      itextcomponent.setStyle(textComponent.getStyle().createDeepCopy());
      return itextcomponent;
   }

   public static class Serializer implements JsonDeserializer<ITextComponent>, JsonSerializer<ITextComponent> {
      private static final Gson GSON = Util.make(() -> {
         GsonBuilder gsonbuilder = new GsonBuilder();
         gsonbuilder.disableHtmlEscaping();
         gsonbuilder.registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer());
         gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         gsonbuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         return gsonbuilder.create();
      });
      private static final Field JSON_READER_POS_FIELD = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("pos");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", nosuchfieldexception);
         }
      });
      private static final Field JSON_READER_LINESTART_FIELD = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("lineStart");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", nosuchfieldexception);
         }
      });

      public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonPrimitive()) {
            return new StringTextComponent(p_deserialize_1_.getAsString());
         } else if (!p_deserialize_1_.isJsonObject()) {
            if (p_deserialize_1_.isJsonArray()) {
               JsonArray jsonarray1 = p_deserialize_1_.getAsJsonArray();
               ITextComponent itextcomponent1 = null;

               for(JsonElement jsonelement : jsonarray1) {
                  ITextComponent itextcomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);
                  if (itextcomponent1 == null) {
                     itextcomponent1 = itextcomponent2;
                  } else {
                     itextcomponent1.appendSibling(itextcomponent2);
                  }
               }

               return itextcomponent1;
            } else {
               throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
            }
         } else {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ITextComponent itextcomponent;
            if (jsonobject.has("text")) {
               itextcomponent = new StringTextComponent(JSONUtils.getString(jsonobject, "text"));
            } else if (jsonobject.has("translate")) {
               String s = JSONUtils.getString(jsonobject, "translate");
               if (jsonobject.has("with")) {
                  JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "with");
                  Object[] aobject = new Object[jsonarray.size()];

                  for(int i = 0; i < aobject.length; ++i) {
                     aobject[i] = this.deserialize(jsonarray.get(i), p_deserialize_2_, p_deserialize_3_);
                     if (aobject[i] instanceof StringTextComponent) {
                        StringTextComponent stringtextcomponent = (StringTextComponent)aobject[i];
                        if (stringtextcomponent.getStyle().isEmpty() && stringtextcomponent.getSiblings().isEmpty()) {
                           aobject[i] = stringtextcomponent.getText();
                        }
                     }
                  }

                  itextcomponent = new TranslationTextComponent(s, aobject);
               } else {
                  itextcomponent = new TranslationTextComponent(s);
               }
            } else if (jsonobject.has("score")) {
               JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonobject, "score");
               if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               itextcomponent = new ScoreTextComponent(JSONUtils.getString(jsonobject1, "name"), JSONUtils.getString(jsonobject1, "objective"));
               if (jsonobject1.has("value")) {
                  ((ScoreTextComponent)itextcomponent).setValue(JSONUtils.getString(jsonobject1, "value"));
               }
            } else if (jsonobject.has("selector")) {
               itextcomponent = new SelectorTextComponent(JSONUtils.getString(jsonobject, "selector"));
            } else if (jsonobject.has("keybind")) {
               itextcomponent = new KeybindTextComponent(JSONUtils.getString(jsonobject, "keybind"));
            } else {
               if (!jsonobject.has("nbt")) {
                  throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
               }

               String s1 = JSONUtils.getString(jsonobject, "nbt");
               boolean flag = JSONUtils.getBoolean(jsonobject, "interpret", false);
               if (jsonobject.has("block")) {
                  itextcomponent = new NBTTextComponent.Block(s1, flag, JSONUtils.getString(jsonobject, "block"));
               } else if (jsonobject.has("entity")) {
                  itextcomponent = new NBTTextComponent.Entity(s1, flag, JSONUtils.getString(jsonobject, "entity"));
               } else {
                  if (!jsonobject.has("storage")) {
                     throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
                  }

                  itextcomponent = new NBTTextComponent.Storage(s1, flag, new ResourceLocation(JSONUtils.getString(jsonobject, "storage")));
               }
            }

            if (jsonobject.has("extra")) {
               JsonArray jsonarray2 = JSONUtils.getJsonArray(jsonobject, "extra");
               if (jsonarray2.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int j = 0; j < jsonarray2.size(); ++j) {
                  itextcomponent.appendSibling(this.deserialize(jsonarray2.get(j), p_deserialize_2_, p_deserialize_3_));
               }
            }

            itextcomponent.setStyle(p_deserialize_3_.deserialize(p_deserialize_1_, Style.class));
            return itextcomponent;
         }
      }

      private void serializeChatStyle(Style style, JsonObject object, JsonSerializationContext ctx) {
         JsonElement jsonelement = ctx.serialize(style);
         if (jsonelement.isJsonObject()) {
            JsonObject jsonobject = (JsonObject)jsonelement;

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               object.add(entry.getKey(), entry.getValue());
            }
         }

      }

      public JsonElement serialize(ITextComponent p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (!p_serialize_1_.getStyle().isEmpty()) {
            this.serializeChatStyle(p_serialize_1_.getStyle(), jsonobject, p_serialize_3_);
         }

         if (!p_serialize_1_.getSiblings().isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(ITextComponent itextcomponent : p_serialize_1_.getSiblings()) {
               jsonarray.add(this.serialize(itextcomponent, itextcomponent.getClass(), p_serialize_3_));
            }

            jsonobject.add("extra", jsonarray);
         }

         if (p_serialize_1_ instanceof StringTextComponent) {
            jsonobject.addProperty("text", ((StringTextComponent)p_serialize_1_).getText());
         } else if (p_serialize_1_ instanceof TranslationTextComponent) {
            TranslationTextComponent translationtextcomponent = (TranslationTextComponent)p_serialize_1_;
            jsonobject.addProperty("translate", translationtextcomponent.getKey());
            if (translationtextcomponent.getFormatArgs() != null && translationtextcomponent.getFormatArgs().length > 0) {
               JsonArray jsonarray1 = new JsonArray();

               for(Object object : translationtextcomponent.getFormatArgs()) {
                  if (object instanceof ITextComponent) {
                     jsonarray1.add(this.serialize((ITextComponent)object, object.getClass(), p_serialize_3_));
                  } else {
                     jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                  }
               }

               jsonobject.add("with", jsonarray1);
            }
         } else if (p_serialize_1_ instanceof ScoreTextComponent) {
            ScoreTextComponent scoretextcomponent = (ScoreTextComponent)p_serialize_1_;
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("name", scoretextcomponent.getName());
            jsonobject1.addProperty("objective", scoretextcomponent.getObjective());
            jsonobject1.addProperty("value", scoretextcomponent.getUnformattedComponentText());
            jsonobject.add("score", jsonobject1);
         } else if (p_serialize_1_ instanceof SelectorTextComponent) {
            SelectorTextComponent selectortextcomponent = (SelectorTextComponent)p_serialize_1_;
            jsonobject.addProperty("selector", selectortextcomponent.getSelector());
         } else if (p_serialize_1_ instanceof KeybindTextComponent) {
            KeybindTextComponent keybindtextcomponent = (KeybindTextComponent)p_serialize_1_;
            jsonobject.addProperty("keybind", keybindtextcomponent.getKeybind());
         } else {
            if (!(p_serialize_1_ instanceof NBTTextComponent)) {
               throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
            }

            NBTTextComponent nbttextcomponent = (NBTTextComponent)p_serialize_1_;
            jsonobject.addProperty("nbt", nbttextcomponent.func_218676_i());
            jsonobject.addProperty("interpret", nbttextcomponent.func_218677_j());
            if (p_serialize_1_ instanceof NBTTextComponent.Block) {
               NBTTextComponent.Block nbttextcomponent$block = (NBTTextComponent.Block)p_serialize_1_;
               jsonobject.addProperty("block", nbttextcomponent$block.func_218683_k());
            } else if (p_serialize_1_ instanceof NBTTextComponent.Entity) {
               NBTTextComponent.Entity nbttextcomponent$entity = (NBTTextComponent.Entity)p_serialize_1_;
               jsonobject.addProperty("entity", nbttextcomponent$entity.func_218687_k());
            } else {
               if (!(p_serialize_1_ instanceof NBTTextComponent.Storage)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
               }

               NBTTextComponent.Storage nbttextcomponent$storage = (NBTTextComponent.Storage)p_serialize_1_;
               jsonobject.addProperty("storage", nbttextcomponent$storage.func_229726_k_().toString());
            }
         }

         return jsonobject;
      }

      /**
       * Serializes a component into JSON.
       */
      public static String toJson(ITextComponent component) {
         return GSON.toJson(component);
      }

      public static JsonElement toJsonTree(ITextComponent p_200528_0_) {
         return GSON.toJsonTree(p_200528_0_);
      }

      /**
       * Parses a JSON string into a {@link ITextComponent}, with strict parsing.
       *  
       * @see #fromJsonLenient(String)
       * @see {@link com.google.gson.stream.JsonReader#setLenient(boolean)}
       */
      @Nullable
      public static ITextComponent fromJson(String json) {
         return JSONUtils.fromJson(GSON, json, ITextComponent.class, false);
      }

      @Nullable
      public static ITextComponent fromJson(JsonElement p_197672_0_) {
         return GSON.fromJson(p_197672_0_, ITextComponent.class);
      }

      /**
       * Parses a JSON string into a {@link ITextComponent}, being lenient upon parse errors.
       *  
       * @see #jsonToComponent(String)
       * @see {@link com.google.gson.stream.JsonReader#setLenient(boolean)}
       */
      @Nullable
      public static ITextComponent fromJsonLenient(String json) {
         return JSONUtils.fromJson(GSON, json, ITextComponent.class, true);
      }

      public static ITextComponent fromJson(com.mojang.brigadier.StringReader p_197671_0_) {
         try {
            JsonReader jsonreader = new JsonReader(new StringReader(p_197671_0_.getRemaining()));
            jsonreader.setLenient(false);
            ITextComponent itextcomponent = GSON.getAdapter(ITextComponent.class).read(jsonreader);
            p_197671_0_.setCursor(p_197671_0_.getCursor() + getPos(jsonreader));
            return itextcomponent;
         } catch (StackOverflowError | IOException ioexception) {
            throw new JsonParseException(ioexception);
         }
      }

      private static int getPos(JsonReader p_197673_0_) {
         try {
            return JSON_READER_POS_FIELD.getInt(p_197673_0_) - JSON_READER_LINESTART_FIELD.getInt(p_197673_0_) + 1;
         } catch (IllegalAccessException illegalaccessexception) {
            throw new IllegalStateException("Couldn't read position of JsonReader", illegalaccessexception);
         }
      }
   }
}