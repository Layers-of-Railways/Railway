package net.minecraft.client;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ChatOptionsScreen;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyboardListener {
   private final Minecraft mc;
   private boolean repeatEventsEnabled;
   private final ClipboardHelper clipboardHelper = new ClipboardHelper();
   private long debugCrashKeyPressTime = -1L;
   private long lastDebugCrashWarning = -1L;
   private long debugCrashWarningsSent = -1L;
   private boolean actionKeyF3;

   public KeyboardListener(Minecraft mcIn) {
      this.mc = mcIn;
   }

   private void printDebugMessage(String message, Object... args) {
      this.mc.ingameGUI.getChatGUI().printChatMessage((new StringTextComponent("")).appendSibling((new TranslationTextComponent("debug.prefix")).applyTextStyles(new TextFormatting[]{TextFormatting.YELLOW, TextFormatting.BOLD})).appendText(" ").appendSibling(new TranslationTextComponent(message, args)));
   }

   private void printDebugWarning(String message, Object... args) {
      this.mc.ingameGUI.getChatGUI().printChatMessage((new StringTextComponent("")).appendSibling((new TranslationTextComponent("debug.prefix")).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.BOLD})).appendText(" ").appendSibling(new TranslationTextComponent(message, args)));
   }

   private boolean processKeyF3(int key) {
      if (this.debugCrashKeyPressTime > 0L && this.debugCrashKeyPressTime < Util.milliTime() - 100L) {
         return true;
      } else {
         switch(key) {
         case 65:
            this.mc.worldRenderer.loadRenderers();
            this.printDebugMessage("debug.reload_chunks.message");
            return true;
         case 66:
            boolean flag = !this.mc.getRenderManager().isDebugBoundingBox();
            this.mc.getRenderManager().setDebugBoundingBox(flag);
            this.printDebugMessage(flag ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            return true;
         case 67:
            if (this.mc.player.hasReducedDebug()) {
               return false;
            }

            this.printDebugMessage("debug.copy_location.message");
            this.setClipboardString(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.getKey(this.mc.player.world.dimension.getType()), this.mc.player.getPosX(), this.mc.player.getPosY(), this.mc.player.getPosZ(), this.mc.player.rotationYaw, this.mc.player.rotationPitch));
            return true;
         case 68:
            if (this.mc.ingameGUI != null) {
               this.mc.ingameGUI.getChatGUI().clearChatMessages(false);
            }

            return true;
         case 69:
         case 74:
         case 75:
         case 76:
         case 77:
         case 79:
         case 82:
         case 83:
         default:
            return false;
         case 70:
            AbstractOption.RENDER_DISTANCE.set(this.mc.gameSettings, MathHelper.clamp((double)(this.mc.gameSettings.renderDistanceChunks + (Screen.hasShiftDown() ? -1 : 1)), AbstractOption.RENDER_DISTANCE.getMinValue(), AbstractOption.RENDER_DISTANCE.getMaxValue()));
            this.printDebugMessage("debug.cycle_renderdistance.message", this.mc.gameSettings.renderDistanceChunks);
            return true;
         case 71:
            boolean flag1 = this.mc.debugRenderer.toggleChunkBorders();
            this.printDebugMessage(flag1 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return true;
         case 72:
            this.mc.gameSettings.advancedItemTooltips = !this.mc.gameSettings.advancedItemTooltips;
            this.printDebugMessage(this.mc.gameSettings.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            this.mc.gameSettings.saveOptions();
            return true;
         case 73:
            if (!this.mc.player.hasReducedDebug()) {
               this.copyHoveredObject(this.mc.player.hasPermissionLevel(2), !Screen.hasShiftDown());
            }

            return true;
         case 78:
            if (!this.mc.player.hasPermissionLevel(2)) {
               this.printDebugMessage("debug.creative_spectator.error");
            } else if (this.mc.player.isCreative()) {
               this.mc.player.sendChatMessage("/gamemode spectator");
            } else {
               this.mc.player.sendChatMessage("/gamemode creative");
            }

            return true;
         case 80:
            this.mc.gameSettings.pauseOnLostFocus = !this.mc.gameSettings.pauseOnLostFocus;
            this.mc.gameSettings.saveOptions();
            this.printDebugMessage(this.mc.gameSettings.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
            return true;
         case 81:
            this.printDebugMessage("debug.help.message");
            NewChatGui newchatgui = this.mc.ingameGUI.getChatGUI();
            newchatgui.printChatMessage(new TranslationTextComponent("debug.reload_chunks.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.show_hitboxes.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.copy_location.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.clear_chat.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.cycle_renderdistance.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.chunk_boundaries.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.advanced_tooltips.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.inspect.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.creative_spectator.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.pause_focus.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.help.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.reload_resourcepacks.help"));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.pause.help"));
            return true;
         case 84:
            this.printDebugMessage("debug.reload_resourcepacks.message");
            this.mc.reloadResources();
            return true;
         }
      }
   }

   private void copyHoveredObject(boolean privileged, boolean askServer) {
      RayTraceResult raytraceresult = this.mc.objectMouseOver;
      if (raytraceresult != null) {
         switch(raytraceresult.getType()) {
         case BLOCK:
            BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
            BlockState blockstate = this.mc.player.world.getBlockState(blockpos);
            if (privileged) {
               if (askServer) {
                  this.mc.player.connection.getNBTQueryManager().queryTileEntity(blockpos, (p_211561_3_) -> {
                     this.setBlockClipboardString(blockstate, blockpos, p_211561_3_);
                     this.printDebugMessage("debug.inspect.server.block");
                  });
               } else {
                  TileEntity tileentity = this.mc.player.world.getTileEntity(blockpos);
                  CompoundNBT compoundnbt1 = tileentity != null ? tileentity.write(new CompoundNBT()) : null;
                  this.setBlockClipboardString(blockstate, blockpos, compoundnbt1);
                  this.printDebugMessage("debug.inspect.client.block");
               }
            } else {
               this.setBlockClipboardString(blockstate, blockpos, (CompoundNBT)null);
               this.printDebugMessage("debug.inspect.client.block");
            }
            break;
         case ENTITY:
            Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
            ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(entity.getType());
            if (privileged) {
               if (askServer) {
                  this.mc.player.connection.getNBTQueryManager().queryEntity(entity.getEntityId(), (p_227999_3_) -> {
                     this.setEntityClipboardString(resourcelocation, entity.getPositionVec(), p_227999_3_);
                     this.printDebugMessage("debug.inspect.server.entity");
                  });
               } else {
                  CompoundNBT compoundnbt = entity.writeWithoutTypeId(new CompoundNBT());
                  this.setEntityClipboardString(resourcelocation, entity.getPositionVec(), compoundnbt);
                  this.printDebugMessage("debug.inspect.client.entity");
               }
            } else {
               this.setEntityClipboardString(resourcelocation, entity.getPositionVec(), (CompoundNBT)null);
               this.printDebugMessage("debug.inspect.client.entity");
            }
         }

      }
   }

   private void setBlockClipboardString(BlockState state, BlockPos pos, @Nullable CompoundNBT compound) {
      if (compound != null) {
         compound.remove("x");
         compound.remove("y");
         compound.remove("z");
         compound.remove("id");
      }

      StringBuilder stringbuilder = new StringBuilder(BlockStateParser.toString(state));
      if (compound != null) {
         stringbuilder.append((Object)compound);
      }

      String s = String.format(Locale.ROOT, "/setblock %d %d %d %s", pos.getX(), pos.getY(), pos.getZ(), stringbuilder);
      this.setClipboardString(s);
   }

   private void setEntityClipboardString(ResourceLocation entityIdIn, Vec3d pos, @Nullable CompoundNBT compound) {
      String s;
      if (compound != null) {
         compound.remove("UUIDMost");
         compound.remove("UUIDLeast");
         compound.remove("Pos");
         compound.remove("Dimension");
         String s1 = compound.toFormattedComponent().getString();
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", entityIdIn.toString(), pos.x, pos.y, pos.z, s1);
      } else {
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", entityIdIn.toString(), pos.x, pos.y, pos.z);
      }

      this.setClipboardString(s);
   }

   public void onKeyEvent(long windowPointer, int key, int scanCode, int action, int modifiers) {
      if (windowPointer == this.mc.getMainWindow().getHandle()) {
         if (this.debugCrashKeyPressTime > 0L) {
            if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 67) || !InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 292)) {
               this.debugCrashKeyPressTime = -1L;
            }
         } else if (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 67) && InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 292)) {
            this.actionKeyF3 = true;
            this.debugCrashKeyPressTime = Util.milliTime();
            this.lastDebugCrashWarning = Util.milliTime();
            this.debugCrashWarningsSent = 0L;
         }

         INestedGuiEventHandler inestedguieventhandler = this.mc.currentScreen;

         if ((!(this.mc.currentScreen instanceof ControlsScreen) || ((ControlsScreen)inestedguieventhandler).time <= Util.milliTime() - 20L)) {
            if (action == 1) {
            if (this.mc.gameSettings.keyBindFullscreen.matchesKey(key, scanCode)) {
               this.mc.getMainWindow().toggleFullscreen();
               this.mc.gameSettings.fullscreen = this.mc.getMainWindow().isFullscreen();
               return;
            }

            if (this.mc.gameSettings.keyBindScreenshot.matchesKey(key, scanCode)) {
               if (Screen.hasControlDown()) {
                  ;
               }

               ScreenShotHelper.saveScreenshot(this.mc.gameDir, this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), this.mc.getFramebuffer(), (p_212449_1_) -> {
                  this.mc.execute(() -> {
                     this.mc.ingameGUI.getChatGUI().printChatMessage(p_212449_1_);
                  });
               });
               return;
            }
            } else if (action == 0 /*GLFW_RELEASE*/ && this.mc.currentScreen instanceof ControlsScreen)
               ((ControlsScreen)this.mc.currentScreen).buttonId = null; //Forge: Unset pure modifiers.
         }

         boolean flag = inestedguieventhandler == null || !(inestedguieventhandler.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)inestedguieventhandler.getFocused()).canWrite();
         if (action != 0 && key == 66 && Screen.hasControlDown() && flag) {
            AbstractOption.NARRATOR.setValueIndex(this.mc.gameSettings, 1);
            if (inestedguieventhandler instanceof ChatOptionsScreen) {
               ((ChatOptionsScreen)inestedguieventhandler).updateNarratorButton();
            }

            if (inestedguieventhandler instanceof AccessibilityScreen) {
               ((AccessibilityScreen)inestedguieventhandler).func_212985_a();
            }
         }

         if (inestedguieventhandler != null) {
            boolean[] aboolean = new boolean[]{false};
            Screen.wrapScreenError(() -> {
               if (action != 1 && (action != 2 || !this.repeatEventsEnabled)) {
                  if (action == 0) {
                     aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyReleasedPre(this.mc.currentScreen, key, scanCode, modifiers);
                     if (!aboolean[0]) aboolean[0] = inestedguieventhandler.keyReleased(key, scanCode, modifiers);
                     if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyReleasedPost(this.mc.currentScreen, key, scanCode, modifiers);
                  }
               } else {
                  aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyPressedPre(this.mc.currentScreen, key, scanCode, modifiers);
                  if (!aboolean[0]) aboolean[0] = inestedguieventhandler.keyPressed(key, scanCode, modifiers);
                  if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyPressedPost(this.mc.currentScreen, key, scanCode, modifiers);
               }

            }, "keyPressed event handler", inestedguieventhandler.getClass().getCanonicalName());
            if (aboolean[0]) {
               return;
            }
         }

         if (this.mc.currentScreen == null || this.mc.currentScreen.passEvents) {
            InputMappings.Input inputmappings$input = InputMappings.getInputByCode(key, scanCode);
            if (action == 0) {
               KeyBinding.setKeyBindState(inputmappings$input, false);
               if (key == 292) {
                  if (this.actionKeyF3) {
                     this.actionKeyF3 = false;
                  } else {
                     this.mc.gameSettings.showDebugInfo = !this.mc.gameSettings.showDebugInfo;
                     this.mc.gameSettings.showDebugProfilerChart = this.mc.gameSettings.showDebugInfo && Screen.hasShiftDown();
                     this.mc.gameSettings.showLagometer = this.mc.gameSettings.showDebugInfo && Screen.hasAltDown();
                  }
               }
            } else {
               if (key == 293 && this.mc.gameRenderer != null) {
                  this.mc.gameRenderer.switchUseShader();
               }

               boolean flag1 = false;
               if (this.mc.currentScreen == null) {
                  if (key == 256) {
                     boolean flag2 = InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 292);
                     this.mc.displayInGameMenu(flag2);
                  }

                  flag1 = InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 292) && this.processKeyF3(key);
                  this.actionKeyF3 |= flag1;
                  if (key == 290) {
                     this.mc.gameSettings.hideGUI = !this.mc.gameSettings.hideGUI;
                  }
               }

               if (flag1) {
                  KeyBinding.setKeyBindState(inputmappings$input, false);
               } else {
                  KeyBinding.setKeyBindState(inputmappings$input, true);
                  KeyBinding.onTick(inputmappings$input);
               }

               if (this.mc.gameSettings.showDebugProfilerChart) {
                  if (key == 48) {
                     this.mc.updateDebugProfilerName(0);
                  }

                  for(int i = 0; i < 9; ++i) {
                     if (key == 49 + i) {
                        this.mc.updateDebugProfilerName(i + 1);
                     }
                  }
               }
            }
         }
         net.minecraftforge.client.ForgeHooksClient.fireKeyInput(key, scanCode, action, modifiers);
      }
   }

   private void onCharEvent(long windowPointer, int codePoint, int modifiers) {
      if (windowPointer == this.mc.getMainWindow().getHandle()) {
         IGuiEventListener iguieventlistener = this.mc.currentScreen;
         if (iguieventlistener != null && this.mc.getLoadingGui() == null) {
            if (Character.charCount(codePoint) == 1) {
               Screen.wrapScreenError(() -> {
                  if (net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPre(this.mc.currentScreen, (char)codePoint, modifiers)) return;
                  if (iguieventlistener.charTyped((char)codePoint, modifiers)) return;
                  net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPost(this.mc.currentScreen, (char)codePoint, modifiers);
               }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
            } else {
               for(char c0 : Character.toChars(codePoint)) {
                  Screen.wrapScreenError(() -> {
                     if (net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPre(this.mc.currentScreen, c0, modifiers)) return;
                     if (iguieventlistener.charTyped(c0, modifiers)) return;
                     net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPost(this.mc.currentScreen, c0, modifiers);
                  }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
               }
            }

         }
      }
   }

   public void enableRepeatEvents(boolean p_197967_1_) {
      this.repeatEventsEnabled = p_197967_1_;
   }

   public void setupCallbacks(long window) {
      InputMappings.setKeyCallbacks(window, (p_228001_1_, p_228001_3_, p_228001_4_, p_228001_5_, p_228001_6_) -> {
         this.mc.execute(() -> {
            this.onKeyEvent(p_228001_1_, p_228001_3_, p_228001_4_, p_228001_5_, p_228001_6_);
         });
      }, (p_228000_1_, p_228000_3_, p_228000_4_) -> {
         this.mc.execute(() -> {
            this.onCharEvent(p_228000_1_, p_228000_3_, p_228000_4_);
         });
      });
   }

   public String getClipboardString() {
      return this.clipboardHelper.getClipboardString(this.mc.getMainWindow().getHandle(), (p_227998_1_, p_227998_2_) -> {
         if (p_227998_1_ != 65545) {
            this.mc.getMainWindow().logGlError(p_227998_1_, p_227998_2_);
         }

      });
   }

   public void setClipboardString(String string) {
      this.clipboardHelper.setClipboardString(this.mc.getMainWindow().getHandle(), string);
   }

   public void tick() {
      if (this.debugCrashKeyPressTime > 0L) {
         long i = Util.milliTime();
         long j = 10000L - (i - this.debugCrashKeyPressTime);
         long k = i - this.lastDebugCrashWarning;
         if (j < 0L) {
            if (Screen.hasControlDown()) {
               NativeUtil.crash();
            }

            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
         }

         if (k >= 1000L) {
            if (this.debugCrashWarningsSent == 0L) {
               this.printDebugMessage("debug.crash.message");
            } else {
               this.printDebugWarning("debug.crash.warning", MathHelper.ceil((float)j / 1000.0F));
            }

            this.lastDebugCrashWarning = i;
            ++this.debugCrashWarningsSent;
         }
      }

   }
}