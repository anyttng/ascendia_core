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
    private int maxLength;
    private boolean editable = true;

    public TextFieldWidget(TextField field) {
        this(field, Minecraft.getInstance().font);
    }

    private TextFieldWidget(TextField field, Font font) {
        super(font, field.textLeft(), field.textTop(), field.textRoom(), field.textHeight(), field.narration());
        this.field = field;
        this.font = font;
        this.tooltip = new ComponentTooltip(this, field);
        TextFieldStyle style = field.style();
        setBordered(false);
        setTextColor(style.textColor());
        setTextColorUneditable(style.disabledTextColor());
        setTextShadow(style.textShadow());
        followField();
        setResponder(field::edited);
    }

    @Override
    public boolean isActive() {
        return field.interactive() && field.visible() && field.enabled();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return field.takesPointerAt(mouseX, mouseY);
    }

    @Override
    public boolean isHovered() {
        return field.hovered();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        field.setFocused(isFocused());
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return field.wheelScrolled(GameInput.wheel(x, y, scrollY));
    }

    @Override
    public void insertText(String input) {
        String kept = field.accepted(input);
        if (!kept.isEmpty() || input.isEmpty()) {
            super.insertText(kept);
        }
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        followField();
        if (!field.visible()) {
            return;
        }
        boolean pointerShown = graphics.containsPointInScissor(mouseX, mouseY);
        field.draw(new GameCanvas(graphics, font),
                pointerShown ? mouseX : ComponentWidget.OUTSIDE, pointerShown ? mouseY : ComponentWidget.OUTSIDE);
        extractTextInWindow(graphics, mouseX, mouseY, partialTick);
    }

    private void extractTextInWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Rect window = field.window();
        if (window == null) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
            return;
        }
        graphics.enableScissor(window.x(), window.y(), window.right(), window.bottom());
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();
    }

    @Override
    protected void extractTooltipForNextRenderPass(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (field.visible()) {
            if (isHovered() || isFocused()) {
                tooltip.follow(mouseX, mouseY);
            }
            super.extractTooltipForNextRenderPass(graphics, mouseX, mouseY);
        }
    }

    void followComponent() {
        int room = getWidth();
        setRectangle(field.textRoom(), field.textHeight(), field.textLeft(), field.textTop());
        if (getWidth() != room) {
            scrollToCursor();
        }
    }

    private void scrollToCursor() {
        moveCursorTo(getCursorPosition(), true);
    }

    private void followField() {
        followComponent();
        if (maxLength != field.maxLength()) {
            maxLength = field.maxLength();
            setMaxLength(maxLength);
        }
        if (!getValue().equals(field.value())) {
            setValue(field.value());
        }
        boolean enabled = field.enabled();
        if (editable != enabled) {
            editable = enabled;
            setEditable(enabled);
        }
    }
}
