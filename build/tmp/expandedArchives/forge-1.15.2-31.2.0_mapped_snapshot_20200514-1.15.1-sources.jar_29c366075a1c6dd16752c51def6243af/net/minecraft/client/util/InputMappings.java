package net.minecraft.client.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

@OnlyIn(Dist.CLIENT)
public class InputMappings {
   @Nullable
   private static final MethodHandle GLFW_RAW_MOUSE_SUPPORTED;
   private static final int GLFW_RAW_MOUSE;
   public static final InputMappings.Input INPUT_INVALID;

   public static InputMappings.Input getInputByCode(int keyCode, int scanCode) {
      return keyCode == -1 ? InputMappings.Type.SCANCODE.getOrMakeInput(scanCode) : InputMappings.Type.KEYSYM.getOrMakeInput(keyCode);
   }

   public static InputMappings.Input getInputByName(String name) {
      if (InputMappings.Input.REGISTRY.containsKey(name)) {
         return InputMappings.Input.REGISTRY.get(name);
      } else {
         for(InputMappings.Type inputmappings$type : InputMappings.Type.values()) {
            if (name.startsWith(inputmappings$type.name)) {
               String s = name.substring(inputmappings$type.name.length() + 1);
               return inputmappings$type.getOrMakeInput(Integer.parseInt(s));
            }
         }

         throw new IllegalArgumentException("Unknown key name: " + name);
      }
   }

   public static boolean isKeyDown(long p_216506_0_, int p_216506_2_) {
      return GLFW.glfwGetKey(p_216506_0_, p_216506_2_) == 1;
   }

   public static void setKeyCallbacks(long p_216505_0_, GLFWKeyCallbackI p_216505_2_, GLFWCharModsCallbackI p_216505_3_) {
      GLFW.glfwSetKeyCallback(p_216505_0_, p_216505_2_);
      GLFW.glfwSetCharModsCallback(p_216505_0_, p_216505_3_);
   }

   public static void setMouseCallbacks(long p_216503_0_, GLFWCursorPosCallbackI p_216503_2_, GLFWMouseButtonCallbackI p_216503_3_, GLFWScrollCallbackI p_216503_4_) {
      GLFW.glfwSetCursorPosCallback(p_216503_0_, p_216503_2_);
      GLFW.glfwSetMouseButtonCallback(p_216503_0_, p_216503_3_);
      GLFW.glfwSetScrollCallback(p_216503_0_, p_216503_4_);
   }

   public static void setCursorPosAndMode(long p_216504_0_, int p_216504_2_, double p_216504_3_, double p_216504_5_) {
      GLFW.glfwSetCursorPos(p_216504_0_, p_216504_3_, p_216504_5_);
      GLFW.glfwSetInputMode(p_216504_0_, 208897, p_216504_2_);
   }

   public static boolean func_224790_a() {
      try {
         return GLFW_RAW_MOUSE_SUPPORTED != null && (boolean)GLFW_RAW_MOUSE_SUPPORTED.invokeExact();
      } catch (Throwable throwable) {
         throw new RuntimeException(throwable);
      }
   }

   public static void setRawMouseInput(long p_224791_0_, boolean p_224791_2_) {
      if (func_224790_a()) {
         GLFW.glfwSetInputMode(p_224791_0_, GLFW_RAW_MOUSE, p_224791_2_ ? 1 : 0);
      }

   }

   @Nullable
   public static String getKeynameFromKeycode(int p_216507_0_) {
      return GLFW.glfwGetKeyName(p_216507_0_, -1);
   }

   @Nullable
   public static String getKeyNameFromScanCode(int p_216502_0_) {
      return GLFW.glfwGetKeyName(-1, p_216502_0_);
   }

   static {
      Lookup lookup = MethodHandles.lookup();
      MethodType methodtype = MethodType.methodType(Boolean.TYPE);
      MethodHandle methodhandle = null;
      int i = 0;

      try {
         methodhandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodtype);
         MethodHandle methodhandle1 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", Integer.TYPE);
         i = (int)methodhandle1.invokeExact();
      } catch (NoSuchFieldException | NoSuchMethodException var5) {
         ;
      } catch (Throwable throwable) {
         throw new RuntimeException(throwable);
      }

      GLFW_RAW_MOUSE_SUPPORTED = methodhandle;
      GLFW_RAW_MOUSE = i;
      INPUT_INVALID = InputMappings.Type.KEYSYM.getOrMakeInput(-1);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Input {
      private final String name;
      private final InputMappings.Type type;
      private final int keyCode;
      private static final Map<String, InputMappings.Input> REGISTRY = Maps.newHashMap();

      private Input(String nameIn, InputMappings.Type typeIn, int keyCodeIn) {
         this.name = nameIn;
         this.type = typeIn;
         this.keyCode = keyCodeIn;
         REGISTRY.put(nameIn, this);
      }

      public InputMappings.Type getType() {
         return this.type;
      }

      public int getKeyCode() {
         return this.keyCode;
      }

      public String getTranslationKey() {
         return this.name;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            InputMappings.Input inputmappings$input = (InputMappings.Input)p_equals_1_;
            return this.keyCode == inputmappings$input.keyCode && this.type == inputmappings$input.type;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.type, this.keyCode);
      }

      public String toString() {
         return this.name;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      KEYSYM("key.keyboard"),
      SCANCODE("scancode"),
      MOUSE("key.mouse");

      private static final String[] MOUSE_BUTTONS = new String[]{"left", "middle", "right"};
      private final Int2ObjectMap<InputMappings.Input> inputs = new Int2ObjectOpenHashMap<>();
      private final String name;

      private static void registerInput(InputMappings.Type type, String nameIn, int keyCode) {
         InputMappings.Input inputmappings$input = new InputMappings.Input(nameIn, type, keyCode);
         type.inputs.put(keyCode, inputmappings$input);
      }

      private Type(String nameIn) {
         this.name = nameIn;
      }

      public InputMappings.Input getOrMakeInput(int keyCode) {
         if (this.inputs.containsKey(keyCode)) {
            return this.inputs.get(keyCode);
         } else {
            String s;
            if (this == MOUSE) {
               if (keyCode <= 2) {
                  s = "." + MOUSE_BUTTONS[keyCode];
               } else {
                  s = "." + (keyCode + 1);
               }
            } else {
               s = "." + keyCode;
            }

            InputMappings.Input inputmappings$input = new InputMappings.Input(this.name + s, this, keyCode);
            this.inputs.put(keyCode, inputmappings$input);
            return inputmappings$input;
         }
      }

      public String getName() {
         return this.name;
      }

      static {
         registerInput(KEYSYM, "key.keyboard.unknown", -1);
         registerInput(MOUSE, "key.mouse.left", 0);
         registerInput(MOUSE, "key.mouse.right", 1);
         registerInput(MOUSE, "key.mouse.middle", 2);
         registerInput(MOUSE, "key.mouse.4", 3);
         registerInput(MOUSE, "key.mouse.5", 4);
         registerInput(MOUSE, "key.mouse.6", 5);
         registerInput(MOUSE, "key.mouse.7", 6);
         registerInput(MOUSE, "key.mouse.8", 7);
         registerInput(KEYSYM, "key.keyboard.0", 48);
         registerInput(KEYSYM, "key.keyboard.1", 49);
         registerInput(KEYSYM, "key.keyboard.2", 50);
         registerInput(KEYSYM, "key.keyboard.3", 51);
         registerInput(KEYSYM, "key.keyboard.4", 52);
         registerInput(KEYSYM, "key.keyboard.5", 53);
         registerInput(KEYSYM, "key.keyboard.6", 54);
         registerInput(KEYSYM, "key.keyboard.7", 55);
         registerInput(KEYSYM, "key.keyboard.8", 56);
         registerInput(KEYSYM, "key.keyboard.9", 57);
         registerInput(KEYSYM, "key.keyboard.a", 65);
         registerInput(KEYSYM, "key.keyboard.b", 66);
         registerInput(KEYSYM, "key.keyboard.c", 67);
         registerInput(KEYSYM, "key.keyboard.d", 68);
         registerInput(KEYSYM, "key.keyboard.e", 69);
         registerInput(KEYSYM, "key.keyboard.f", 70);
         registerInput(KEYSYM, "key.keyboard.g", 71);
         registerInput(KEYSYM, "key.keyboard.h", 72);
         registerInput(KEYSYM, "key.keyboard.i", 73);
         registerInput(KEYSYM, "key.keyboard.j", 74);
         registerInput(KEYSYM, "key.keyboard.k", 75);
         registerInput(KEYSYM, "key.keyboard.l", 76);
         registerInput(KEYSYM, "key.keyboard.m", 77);
         registerInput(KEYSYM, "key.keyboard.n", 78);
         registerInput(KEYSYM, "key.keyboard.o", 79);
         registerInput(KEYSYM, "key.keyboard.p", 80);
         registerInput(KEYSYM, "key.keyboard.q", 81);
         registerInput(KEYSYM, "key.keyboard.r", 82);
         registerInput(KEYSYM, "key.keyboard.s", 83);
         registerInput(KEYSYM, "key.keyboard.t", 84);
         registerInput(KEYSYM, "key.keyboard.u", 85);
         registerInput(KEYSYM, "key.keyboard.v", 86);
         registerInput(KEYSYM, "key.keyboard.w", 87);
         registerInput(KEYSYM, "key.keyboard.x", 88);
         registerInput(KEYSYM, "key.keyboard.y", 89);
         registerInput(KEYSYM, "key.keyboard.z", 90);
         registerInput(KEYSYM, "key.keyboard.f1", 290);
         registerInput(KEYSYM, "key.keyboard.f2", 291);
         registerInput(KEYSYM, "key.keyboard.f3", 292);
         registerInput(KEYSYM, "key.keyboard.f4", 293);
         registerInput(KEYSYM, "key.keyboard.f5", 294);
         registerInput(KEYSYM, "key.keyboard.f6", 295);
         registerInput(KEYSYM, "key.keyboard.f7", 296);
         registerInput(KEYSYM, "key.keyboard.f8", 297);
         registerInput(KEYSYM, "key.keyboard.f9", 298);
         registerInput(KEYSYM, "key.keyboard.f10", 299);
         registerInput(KEYSYM, "key.keyboard.f11", 300);
         registerInput(KEYSYM, "key.keyboard.f12", 301);
         registerInput(KEYSYM, "key.keyboard.f13", 302);
         registerInput(KEYSYM, "key.keyboard.f14", 303);
         registerInput(KEYSYM, "key.keyboard.f15", 304);
         registerInput(KEYSYM, "key.keyboard.f16", 305);
         registerInput(KEYSYM, "key.keyboard.f17", 306);
         registerInput(KEYSYM, "key.keyboard.f18", 307);
         registerInput(KEYSYM, "key.keyboard.f19", 308);
         registerInput(KEYSYM, "key.keyboard.f20", 309);
         registerInput(KEYSYM, "key.keyboard.f21", 310);
         registerInput(KEYSYM, "key.keyboard.f22", 311);
         registerInput(KEYSYM, "key.keyboard.f23", 312);
         registerInput(KEYSYM, "key.keyboard.f24", 313);
         registerInput(KEYSYM, "key.keyboard.f25", 314);
         registerInput(KEYSYM, "key.keyboard.num.lock", 282);
         registerInput(KEYSYM, "key.keyboard.keypad.0", 320);
         registerInput(KEYSYM, "key.keyboard.keypad.1", 321);
         registerInput(KEYSYM, "key.keyboard.keypad.2", 322);
         registerInput(KEYSYM, "key.keyboard.keypad.3", 323);
         registerInput(KEYSYM, "key.keyboard.keypad.4", 324);
         registerInput(KEYSYM, "key.keyboard.keypad.5", 325);
         registerInput(KEYSYM, "key.keyboard.keypad.6", 326);
         registerInput(KEYSYM, "key.keyboard.keypad.7", 327);
         registerInput(KEYSYM, "key.keyboard.keypad.8", 328);
         registerInput(KEYSYM, "key.keyboard.keypad.9", 329);
         registerInput(KEYSYM, "key.keyboard.keypad.add", 334);
         registerInput(KEYSYM, "key.keyboard.keypad.decimal", 330);
         registerInput(KEYSYM, "key.keyboard.keypad.enter", 335);
         registerInput(KEYSYM, "key.keyboard.keypad.equal", 336);
         registerInput(KEYSYM, "key.keyboard.keypad.multiply", 332);
         registerInput(KEYSYM, "key.keyboard.keypad.divide", 331);
         registerInput(KEYSYM, "key.keyboard.keypad.subtract", 333);
         registerInput(KEYSYM, "key.keyboard.down", 264);
         registerInput(KEYSYM, "key.keyboard.left", 263);
         registerInput(KEYSYM, "key.keyboard.right", 262);
         registerInput(KEYSYM, "key.keyboard.up", 265);
         registerInput(KEYSYM, "key.keyboard.apostrophe", 39);
         registerInput(KEYSYM, "key.keyboard.backslash", 92);
         registerInput(KEYSYM, "key.keyboard.comma", 44);
         registerInput(KEYSYM, "key.keyboard.equal", 61);
         registerInput(KEYSYM, "key.keyboard.grave.accent", 96);
         registerInput(KEYSYM, "key.keyboard.left.bracket", 91);
         registerInput(KEYSYM, "key.keyboard.minus", 45);
         registerInput(KEYSYM, "key.keyboard.period", 46);
         registerInput(KEYSYM, "key.keyboard.right.bracket", 93);
         registerInput(KEYSYM, "key.keyboard.semicolon", 59);
         registerInput(KEYSYM, "key.keyboard.slash", 47);
         registerInput(KEYSYM, "key.keyboard.space", 32);
         registerInput(KEYSYM, "key.keyboard.tab", 258);
         registerInput(KEYSYM, "key.keyboard.left.alt", 342);
         registerInput(KEYSYM, "key.keyboard.left.control", 341);
         registerInput(KEYSYM, "key.keyboard.left.shift", 340);
         registerInput(KEYSYM, "key.keyboard.left.win", 343);
         registerInput(KEYSYM, "key.keyboard.right.alt", 346);
         registerInput(KEYSYM, "key.keyboard.right.control", 345);
         registerInput(KEYSYM, "key.keyboard.right.shift", 344);
         registerInput(KEYSYM, "key.keyboard.right.win", 347);
         registerInput(KEYSYM, "key.keyboard.enter", 257);
         registerInput(KEYSYM, "key.keyboard.escape", 256);
         registerInput(KEYSYM, "key.keyboard.backspace", 259);
         registerInput(KEYSYM, "key.keyboard.delete", 261);
         registerInput(KEYSYM, "key.keyboard.end", 269);
         registerInput(KEYSYM, "key.keyboard.home", 268);
         registerInput(KEYSYM, "key.keyboard.insert", 260);
         registerInput(KEYSYM, "key.keyboard.page.down", 267);
         registerInput(KEYSYM, "key.keyboard.page.up", 266);
         registerInput(KEYSYM, "key.keyboard.caps.lock", 280);
         registerInput(KEYSYM, "key.keyboard.pause", 284);
         registerInput(KEYSYM, "key.keyboard.scroll.lock", 281);
         registerInput(KEYSYM, "key.keyboard.menu", 348);
         registerInput(KEYSYM, "key.keyboard.print.screen", 283);
         registerInput(KEYSYM, "key.keyboard.world.1", 161);
         registerInput(KEYSYM, "key.keyboard.world.2", 162);
      }
   }
}