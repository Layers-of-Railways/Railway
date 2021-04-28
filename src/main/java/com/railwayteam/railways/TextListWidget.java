package com.railwayteam.railways;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TextListWidget extends Widget {
  private Button add;
  private LinkedHashMap<Button, TextFieldWidget> active;
  private LinkedHashMap<Button, TextFieldWidget> graveyard;

  private int spacing = 20;
  private int buttonWidth = 60;
  private int textWidth   = 100;
  private int fieldHeight = 20;

  private boolean drawAdd = true;
  private boolean drawDel = true;

  public TextListWidget(int xIn, int yIn, int widthIn, int heightIn) {
    super(xIn, yIn, widthIn, heightIn, new StringTextComponent("text_list"));
    active     = new LinkedHashMap<Button, TextFieldWidget>();
    graveyard  = new LinkedHashMap<Button, TextFieldWidget>();
    add        = new Button(xIn, yIn, buttonWidth, fieldHeight, new StringTextComponent("Add"), button->addItem(""));
  }

  public void addItem (String startingText) {
    Button removeButton = new Button (this.x, this.y + active.size()*spacing, buttonWidth, fieldHeight, new StringTextComponent("Remove"), this::removeItem);
    TextFieldWidget textEntry = new TextFieldWidget(
      Minecraft.getInstance().fontRenderer, this.x+buttonWidth, this.y + active.size()*spacing, textWidth, fieldHeight, new StringTextComponent("field")
    );
    textEntry.setText(startingText);
    active.put(removeButton, textEntry);
    add.y += spacing;
  }

  private void removeItem (Button fired) {
    if (!active.containsKey(fired)) return;
    graveyard.put(fired, active.get(fired));
  }

  // used to prevent concurrent modification issues
  private void deferredRemoval () {
    for (Widget w : graveyard.keySet()) {
      active.remove(w);
      add.y -= spacing;
    }
    graveyard.clear();
  }

  public void setSpacing (int value) {
    spacing = (value > 0 ? value : 10);
  }
  public void setHeight (int value) {
    fieldHeight = (value > 0 ? value : 20);
  }
  public void setButtonWidth (int value) {
    buttonWidth = (value > 0 ? value : 80);
  }
  public void setTextWidth (int value) {
    textWidth = (value > 0 ? value : 160);
  }
  public void setEditable (boolean canEdit) {
    drawAdd = canEdit;
    drawDel = canEdit;
  }
  public void setAddable (boolean canAdd) { drawAdd = canAdd; }
  public void setClearable (boolean canDel) { drawDel = canDel; }

  protected ArrayList<String> getActiveValues () {
    deferredRemoval();
    ArrayList<String> ret = new ArrayList<String>();
    for (TextFieldWidget entry : active.values()) {
      ret.add(entry.getText());
    }
    return ret;
  }

  @Override
  public void render (MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
    int yFixed = this.y;
    for (Button b : active.keySet()) {
      b.y = yFixed;
      active.get(b).y = yFixed;
      if (drawDel) b.render(ms, mouseX, mouseY, partialTicks);
      active.get(b).render(ms, mouseX, mouseY, partialTicks);
      yFixed += spacing;
    }
    add.y = yFixed;
    if (drawAdd) add.render(ms, mouseX, mouseY, partialTicks);
    deferredRemoval();
  }

  @Override
  public boolean mouseClicked (double x, double y, int button) {
    boolean result = false;
    for (Button b : active.keySet()) {
      if (b.mouseClicked(x, y, button)) result = true;
      if (active.get(b).mouseClicked(x, y, button)) result = true;
    }
    if (add.mouseClicked(x, y, button)) result = true;
    return result || super.mouseClicked(x,y,button);
  }

  @Override
  public boolean keyPressed (int code, int p_keyPressed_2_, int p_keyPressed_3_) {
    for (Button b : active.keySet()) {
      if (b.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_)) return true;
      if (active.get(b).keyPressed(code, p_keyPressed_2_, p_keyPressed_3_)) return true;
    }
    if (add.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_)) return true;

    return super.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_);
  }

  @Override
  public boolean charTyped (char c, int code) {
    for (Button b : active.keySet()) {
      if (b.charTyped(c, code)) return true;
      if (active.get(b).charTyped(c, code)) return true;
    }
    if (add.charTyped(c, code)) return true;
    return super.charTyped(c, code);
  }

  @Override
  public boolean mouseScrolled (double mouseX, double mouseY, double delta) {
    for (Button b : active.keySet()) {
      if (b.mouseScrolled(mouseX, mouseY, delta)) return true;
      if (active.get(b).mouseScrolled(mouseX, mouseY, delta)) return true;
    }
    if (add.mouseScrolled(mouseX, mouseY, delta)) return true;
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseReleased (double x, double y, int button) {
    boolean result = false;
    for (Button b : active.keySet()) {
      if (b.mouseReleased(x, y, button)) result = true;
      if (active.get(b).mouseReleased(x, y, button)) result = true;
    }
    if (add.mouseReleased(x, y, button)) result = true;
    return result || super.mouseReleased(x,y,button);
  }


}
