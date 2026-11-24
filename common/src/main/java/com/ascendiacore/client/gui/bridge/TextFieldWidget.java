package com.ascendiacore.client.gui.bridge;

import com.ascendiacore.gui.base.geometry.Rect;
import com.ascendiacore.gui.widget.textfield.TextField;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;

public final class TextFieldWidget extends EditBox {
    private final TextField field;
    private final Font font;
    private final ComponentTooltip tooltip;
    private int maxCharacterCount;
    private boolean isEditable = true;

    public TextFieldWidget(TextField field) {
        this(field, Minecraft.getInstance().font);
    }

    private TextFieldWidget(TextField field, Font font) {
        super(font, field.getTextLeft(), field.getTextTop(), field.getTextRoomPixels(), field.getTextHeightPixels(), field.getNarration());
        this.field = field;
        this.font = font;
        this.tooltip = new ComponentTooltip(this, field);
        TextFieldStyle style = field.getStyle();
        setBordered(false);
        setTextColor(style.textColor());
        setTextColorUneditable(style.disabledTextColor());
        setTextShadow(style.hasTextShadow());
        followField();
        setResponder(field::edited);
    }

    @Override
    public boolean isActive() {
        return field.isInteractive() && field.isVisible() && field.isEnabled();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return field.isTakingPointerAt(mouseX, mouseY);
    }

    @Override
    public boolean isHovered() {
        return field.isHovered();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        field.setFocused(isFocused());
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return field.tryScrollWheel(GameInput.toWheelInput(x, y, scrollY));
    }

    @Override
    public void insertText(String input) {
        String kept = field.filterAccepted(input);
        if (!kept.isEmpty() || input.isEmpty()) {
            super.insertText(kept);
        }
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        followField();
        if (!field.isVisible()) {
            return;
        }
        boolean isPointerShown = graphics.containsPointInScissor(mouseX, mouseY);
        field.draw(new GameCanvas(graphics, font),
                isPointerShown ? mouseX : ComponentWidget.OUTSIDE, isPointerShown ? mouseY : ComponentWidget.OUTSIDE);
        extractTextInWindow(graphics, mouseX, mouseY, partialTick);
    }

    private void extractTextInWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Rect window = field.getWindow();
        if (window == null) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
            return;
        }
        graphics.enableScissor(window.x(), window.y(), window.getRight(), window.getBottom());
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();
    }

    @Override
    protected void extractTooltipForNextRenderPass(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (field.isVisible()) {
            if (isHovered() || isFocused()) {
                tooltip.follow(mouseX, mouseY);
            }
            super.extractTooltipForNextRenderPass(graphics, mouseX, mouseY);
        }
    }

    void followComponent() {
        int widthBeforePixels = getWidth();
        setRectangle(field.getTextRoomPixels(), field.getTextHeightPixels(), field.getTextLeft(), field.getTextTop());
        if (getWidth() != widthBeforePixels) {
            scrollToCursor();
        }
    }

    private void scrollToCursor() {
        moveCursorTo(getCursorPosition(), true);
    }

    private void followField() {
        followComponent();
        if (maxCharacterCount != field.getMaxCharacterCount()) {
            maxCharacterCount = field.getMaxCharacterCount();
            setMaxLength(maxCharacterCount);
        }
        if (!getValue().equals(field.getValue())) {
            setValue(field.getValue());
        }
        boolean isEnabled = field.isEnabled();
        if (isEditable != isEnabled) {
            isEditable = isEnabled;
            setEditable(isEnabled);
        }
    }
}
