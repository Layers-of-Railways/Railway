package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyBinding implements Comparable<KeyBinding>, net.minecraftforge.client.extensions.IForgeKeybinding {
   private static final Map<String, KeyBinding> KEYBIND_ARRAY = Maps.newHashMap();
   private static final net.minecraftforge.client.settings.KeyBindingMap HASH = new net.minecraftforge.client.settings.KeyBindingMap();
   private static final Set<String> KEYBIND_SET = Sets.newHashSet();
   private static final Map<String, Integer> CATEGORY_ORDER = Util.make(Maps.newHashMap(), (p_205215_0_) -> {
      p_205215_0_.put("key.categories.movement", 1);
      p_205215_0_.put("key.categories.gameplay", 2);
      p_205215_0_.put("key.categories.inventory", 3);
      p_205215_0_.put("key.categories.creative", 4);
      p_205215_0_.put("key.categories.multiplayer", 5);
      p_205215_0_.put("key.categories.ui", 6);
      p_205215_0_.put("key.categories.misc", 7);
   });
   private final String keyDescription;
   private final InputMappings.Input keyCodeDefault;
   private final String keyCategory;
   private InputMappings.Input keyCode;
   private boolean pressed;
   private int pressTime;

   public static void onTick(InputMappings.Input key) {
      KeyBinding keybinding = HASH.lookupActive(key);
      if (keybinding != null) {
         ++keybinding.pressTime;
      }

   }

   public static void setKeyBindState(InputMappings.Input key, boolean held) {
      for (KeyBinding keybinding : HASH.lookupAll(key))
      if (keybinding != null) {
         keybinding.setPressed(held);
      }

   }

   /**
    * Completely recalculates whether any keybinds are held, from scratch.
    */
   public static void updateKeyBindState() {
      for(KeyBinding keybinding : KEYBIND_ARRAY.values()) {
         if (keybinding.keyCode.getType() == InputMappings.Type.KEYSYM && keybinding.keyCode.getKeyCode() != InputMappings.INPUT_INVALID.getKeyCode()) {
            keybinding.setPressed(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keybinding.keyCode.getKeyCode()));
         }
      }

   }

   public static void unPressAllKeys() {
      for(KeyBinding keybinding : KEYBIND_ARRAY.values()) {
         keybinding.unpressKey();
      }

   }

   public static void resetKeyBindingArrayAndHash() {
      HASH.clearMap();

      for(KeyBinding keybinding : KEYBIND_ARRAY.values()) {
         HASH.addKey(keybinding.keyCode, keybinding);
      }

   }

   public KeyBinding(String description, int keyCode, String category) {
      this(description, InputMappings.Type.KEYSYM, keyCode, category);
   }

   public KeyBinding(String description, InputMappings.Type type, int code, String category) {
      this.keyDescription = description;
      this.keyCode = type.getOrMakeInput(code);
      this.keyCodeDefault = this.keyCode;
      this.keyCategory = category;
      KEYBIND_ARRAY.put(description, this);
      HASH.addKey(this.keyCode, this);
      KEYBIND_SET.add(category);
   }

   /**
    * Returns true if the key is pressed (used for continuous querying). Should be used in tickers.
    */
   public boolean isKeyDown() {
      return this.pressed && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
   }

   public String getKeyCategory() {
      return this.keyCategory;
   }

   /**
    * Returns true on the initial key press. For continuous querying use {@link isKeyDown()}. Should be used in key
    * events.
    */
   public boolean isPressed() {
      if (this.pressTime == 0) {
         return false;
      } else {
         --this.pressTime;
         return true;
      }
   }

   private void unpressKey() {
      this.pressTime = 0;
      this.setPressed(false);
   }

   public String getKeyDescription() {
      return this.keyDescription;
   }

   public InputMappings.Input getDefault() {
      return this.keyCodeDefault;
   }

   /**
    * Binds a new KeyCode to this
    */
   public void bind(InputMappings.Input key) {
      this.keyCode = key;
   }

   public int compareTo(KeyBinding p_compareTo_1_) {
      if (this.keyCategory.equals(p_compareTo_1_.keyCategory)) return I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.keyDescription));
      Integer tCat = CATEGORY_ORDER.get(this.keyCategory);
      Integer oCat = CATEGORY_ORDER.get(p_compareTo_1_.keyCategory);
      if (tCat == null && oCat != null) return 1;
      if (tCat != null && oCat == null) return -1;
      if (tCat == null && oCat == null) return I18n.format(this.keyCategory).compareTo(I18n.format(p_compareTo_1_.keyCategory));
      return  tCat.compareTo(oCat);
   }

   /**
    * Returns a supplier which gets a keybind's current binding (eg, <code>key.forward</code> returns <samp>W</samp> by
    * default), or the keybind's name if no such keybind exists (eg, <code>key.invalid</code> returns
    * <samp>key.invalid</samp>)
    */
   public static Supplier<String> getDisplayString(String key) {
      KeyBinding keybinding = KEYBIND_ARRAY.get(key);
      return keybinding == null ? () -> {
         return key;
      } : keybinding::getLocalizedName;
   }

   /**
    * Returns true if the supplied KeyBinding conflicts with this
    */
   public boolean conflicts(KeyBinding binding) {
      if (getKeyConflictContext().conflicts(binding.getKeyConflictContext()) || binding.getKeyConflictContext().conflicts(getKeyConflictContext())) {
         net.minecraftforge.client.settings.KeyModifier keyModifier = getKeyModifier();
         net.minecraftforge.client.settings.KeyModifier otherKeyModifier = binding.getKeyModifier();
         if (keyModifier.matches(binding.getKey()) || otherKeyModifier.matches(getKey())) {
            return true;
         } else if (getKey().equals(binding.getKey())) {
            // IN_GAME key contexts have a conflict when at least one modifier is NONE.
            // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
            // GUI and other key contexts do not have this limitation.
            return keyModifier == otherKeyModifier ||
               (getKeyConflictContext().conflicts(net.minecraftforge.client.settings.KeyConflictContext.IN_GAME) &&
               (keyModifier == net.minecraftforge.client.settings.KeyModifier.NONE || otherKeyModifier == net.minecraftforge.client.settings.KeyModifier.NONE));
         }
      }
      return this.keyCode.equals(binding.keyCode);
   }

   public boolean isInvalid() {
      return this.keyCode.equals(InputMappings.INPUT_INVALID);
   }

   public boolean matchesKey(int keysym, int scancode) {
      if (keysym == InputMappings.INPUT_INVALID.getKeyCode()) {
         return this.keyCode.getType() == InputMappings.Type.SCANCODE && this.keyCode.getKeyCode() == scancode;
      } else {
         return this.keyCode.getType() == InputMappings.Type.KEYSYM && this.keyCode.getKeyCode() == keysym;
      }
   }

   /**
    * Returns true if the KeyBinding is set to a mouse key and the key matches
    */
   public boolean matchesMouseKey(int key) {
      return this.keyCode.getType() == InputMappings.Type.MOUSE && this.keyCode.getKeyCode() == key;
   }

   /**
    * Returns the localized name of the key and key modifier combo set for this KeyBinding
    */
   public String getLocalizedName() {
      return getKeyModifier().getLocalizedComboName(this.keyCode, () -> {
      String s = this.keyCode.getTranslationKey();
      int i = this.keyCode.getKeyCode();
      String s1 = null;
      switch(this.keyCode.getType()) {
      case KEYSYM:
         s1 = InputMappings.getKeynameFromKeycode(i);
         break;
      case SCANCODE:
         s1 = InputMappings.getKeyNameFromScanCode(i);
         break;
      case MOUSE:
         String s2 = I18n.format(s);
         s1 = Objects.equals(s2, s) ? I18n.format(InputMappings.Type.MOUSE.getName(), i + 1) : s2;
      }

      return s1 == null ? I18n.format(s) : s1;
      });
   }

   /**
    * Returns true if the keybinding is using the default key and key modifier
    */
   public boolean isDefault() {
      return this.keyCode.equals(this.keyCodeDefault) && getKeyModifier() == getKeyModifierDefault();
   }

   public String getTranslationKey() {
      return this.keyCode.getTranslationKey();
   }

   /****************** Forge Start *****************************/
   private net.minecraftforge.client.settings.KeyModifier keyModifierDefault = net.minecraftforge.client.settings.KeyModifier.NONE;
   private net.minecraftforge.client.settings.KeyModifier keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
   private net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext = net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, final InputMappings.Type inputType, final int keyCode, String category) {
      this(description, keyConflictContext, inputType.getOrMakeInput(keyCode), category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, InputMappings.Input keyCode, String category) {
      this(description, keyConflictContext, net.minecraftforge.client.settings.KeyModifier.NONE, keyCode, category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, final InputMappings.Type inputType, final int keyCode, String category) {
      this(description, keyConflictContext, keyModifier, inputType.getOrMakeInput(keyCode), category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, InputMappings.Input keyCode, String category) {
       this.keyDescription = description;
       this.keyCode = keyCode;
       this.keyCodeDefault = keyCode;
       this.keyCategory = category;
       this.keyConflictContext = keyConflictContext;
       this.keyModifier = keyModifier;
       this.keyModifierDefault = keyModifier;
       if (this.keyModifier.matches(keyCode))
          this.keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
       KEYBIND_ARRAY.put(description, this);
       HASH.addKey(keyCode, this);
       KEYBIND_SET.add(category);
   }

   @Override
   public InputMappings.Input getKey() {
      return this.keyCode;
   }

   @Override
   public void setKeyConflictContext(net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext) {
      this.keyConflictContext = keyConflictContext;
   }

   @Override
   public net.minecraftforge.client.settings.IKeyConflictContext getKeyConflictContext() {
      return keyConflictContext;
   }

   @Override
   public net.minecraftforge.client.settings.KeyModifier getKeyModifierDefault() {
      return keyModifierDefault;
   }

   @Override
   public net.minecraftforge.client.settings.KeyModifier getKeyModifier() {
      return keyModifier;
   }

   @Override
   public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, InputMappings.Input keyCode) {
      this.keyCode = keyCode;
      if (keyModifier.matches(keyCode))
         keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
      HASH.removeKey(this);
      this.keyModifier = keyModifier;
      HASH.addKey(keyCode, this);
   }
   /****************** Forge End *****************************/

   public void setPressed(boolean valueIn) {
      this.pressed = valueIn;
   }
}