package com.railwayteam.railways;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRailwaysScreen <S extends Container> extends ContainerScreen<S> {
  protected List<Widget> widgets;

  public AbstractRailwaysScreen(S container, PlayerInventory inv, ITextComponent title) {
    super (container, inv, title);
    widgets = new ArrayList<>();
  }

  protected void setWindowSize (int width, int height) {
    this.imageWidth = width;
    this.imageHeight = height;
  }

  @Override
  public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(stack);
    renderWindow(stack, mouseX, mouseY, partialTicks);

    for (Widget w : widgets) {
      w.render(stack, mouseX, mouseY, partialTicks);
    }
    super.render(stack, mouseX, mouseY, partialTicks);

    RenderSystem.enableAlphaTest();
    RenderSystem.enableBlend();
    RenderSystem.disableRescaleNormal();
    RenderSystem.disableLighting();
    RenderSystem.disableDepthTest();
    renderWindowForeground(stack, mouseX, mouseY, partialTicks);
    for (Widget w : widgets) {
      w.renderToolTip(stack, mouseX, mouseY);
    }
  }

  @Override
  public boolean mouseClicked (double x, double y, int button) {
    boolean result = false;
    for (Widget w : widgets) {
      if (w.mouseClicked(x,y,button)) {
        result = true;
      }
    }
    return result || super.mouseClicked(x,y,button);
  }

  @Override
  public boolean keyPressed (int code, int p_keyPressed_2_, int p_keyPressed_3_) {
    for (Widget w : widgets) {
      if (w.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_)) {
        return true;
      }
    }
    return super.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_);
  }

  @Override
  public boolean charTyped (char c, int code) {
    for (Widget w : widgets) {
      if (w.charTyped(c, code)) {
        return true;
      }
    }
    return super.charTyped(c, code);
  }

  @Override
  public boolean mouseScrolled (double mouseX, double mouseY, double delta) {
    for (Widget w : widgets) {
      if (w.mouseScrolled(mouseX, mouseY, delta)) {
        return true;
      }
    }
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseReleased (double x, double y, int button) {
    boolean result = false;
    for (Widget w : widgets) {
      if (w.mouseReleased(x, y, button)) {
        result = true;
      }
    }
    return result | super.mouseReleased(x,y,button);
  }

  @Override
  public boolean shouldCloseOnEsc () {
    return true;
  }

  @Override
  public boolean isPauseScreen () {
    return false;
  }

  protected abstract void renderWindow(MatrixStack stack, int mouseX, int mouseY, float PartialTicks);

  protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
  }

  protected void renderWindowForeground(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
    renderTooltip(stack, mouseX, mouseY);
    for (Widget w : widgets) {
      if (!w.isHovered()) {
        continue;
      }
      // check for special widgetation here
    }
  }


}
