package com.awesomehippo.fpsoptimizer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;

    private IntSlider defaultFpsSlider;
    private IntSlider idleFpsSlider;
    private IntSlider afkTimeoutSlider;
    private EditBox idleMessageBox;
    private Button showIdleButton;
    private Button darkBackgroundButton;
    private Button doneButton;
    private Button cancelButton;

    public ConfigScreen(Screen parentScreen) {
        super(Component.translatable("fpsoptimizer.config.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 40;
        int spacing = 24;
        int labelWidth = 120;
        int controlWidth = 200;
        int gap = 20;
        int totalWidth = labelWidth + gap + controlWidth;
        int leftMargin = centerX - totalWidth / 2;
        int controlX = leftMargin + labelWidth + gap;

        defaultFpsSlider = new IntSlider(controlX, startY, controlWidth, 20, Config.DEFAULT_FPS.get(), 10, 260, "fpsoptimizer.config.default_fps");
        defaultFpsSlider.setTooltip(Tooltip.create(Component.translatable("fpsoptimizer.config.default_fps")));
        this.addRenderableWidget(defaultFpsSlider);

        startY += spacing;

        idleFpsSlider = new IntSlider(controlX, startY, controlWidth, 20, Config.IDLE_FPS.get(), 10, 260, "fpsoptimizer.config.idle_fps");
        idleFpsSlider.setTooltip(Tooltip.create(Component.translatable("fpsoptimizer.config.idle_fps")));
        this.addRenderableWidget(idleFpsSlider);

        startY += spacing;

        // afk timeout as a slider unlike neoforge since it's harder to handle here.
        afkTimeoutSlider = new IntSlider(controlX, startY, controlWidth, 20, Config.AFK_TIMEOUT.get(), 1, 3600, "fpsoptimizer.config.afk_timeout");
        afkTimeoutSlider.setTooltip(Tooltip.create(Component.translatable("fpsoptimizer.config.afk_timeout")));
        this.addRenderableWidget(afkTimeoutSlider);

        startY += spacing;

        String showIdleText = Config.SHOW_IDLE_MESSAGE.get() ? "ON" : "OFF";
        showIdleButton = Button.builder(Component.literal(showIdleText), this::toggleShowIdle).bounds(controlX, startY, controlWidth, 20).build();
        showIdleButton.setTooltip(Tooltip.create(Component.translatable("fpsoptimizer.config.show_idle_message")));
        this.addRenderableWidget(showIdleButton);

        startY += spacing;

        idleMessageBox = new EditBox(this.font, controlX, startY, controlWidth, 20, Component.literal(""));
        idleMessageBox.setMaxLength(256); // 256 max characters
        idleMessageBox.setValue(Config.IDLE_MESSAGE.get());
        idleMessageBox.setTooltip(Tooltip.create(Component.translatable("fpsoptimizer.config.idle_message")));
        this.addRenderableWidget(idleMessageBox);

        startY += spacing;

        String darkBgText = Config.DARK_BACKGROUND.get() ? "ON" : "OFF";
        darkBackgroundButton = Button.builder(Component.literal(darkBgText), this::toggleDarkBackground).bounds(controlX, startY, controlWidth, 20).build();
        darkBackgroundButton.setTooltip(Tooltip.create(Component.translatable("fpsoptimizer.config.dark_background")));
        this.addRenderableWidget(darkBackgroundButton);

        // done / cancel buttons (at bottom)
        int buttonY = this.height - 40;
        int buttonWidth = 100;
        int buttonGap = 20;
        int buttonsTotal = buttonWidth * 2 + buttonGap;
        int buttonsLeft = centerX - buttonsTotal / 2;

        cancelButton = Button.builder(Component.literal("Cancel"), this::cancelConfig).bounds(buttonsLeft, buttonY, buttonWidth, 20).build();
        this.addRenderableWidget(cancelButton);

        doneButton = Button.builder(Component.literal("Done"), this::saveConfig).bounds(buttonsLeft + buttonWidth + buttonGap, buttonY, buttonWidth, 20).build();
        this.addRenderableWidget(doneButton);
    }


    // toggles utils
    private void toggleShowIdle(Button button) {
        boolean current = Config.SHOW_IDLE_MESSAGE.get();
        Config.SHOW_IDLE_MESSAGE.set(!current);
        button.setMessage(Component.literal(!current ? "ON" : "OFF"));
    }
    private void toggleDarkBackground(Button button) {
        boolean current = Config.DARK_BACKGROUND.get();
        Config.DARK_BACKGROUND.set(!current);
        button.setMessage(Component.literal(!current ? "ON" : "OFF"));
    }

    private void saveConfig(Button button) {
        Config.DEFAULT_FPS.set(defaultFpsSlider.getIntValue());
        Config.IDLE_FPS.set(idleFpsSlider.getIntValue());
        Config.AFK_TIMEOUT.set(afkTimeoutSlider.getIntValue());
        Config.IDLE_MESSAGE.set(idleMessageBox.getValue());

        Config.SPEC.save();

        if (this.minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }

    private void cancelConfig(Button button) {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        // title
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        int centerX = this.width / 2;
        int startY = 40;
        int spacing = 24;
        int labelWidth = 120;
        int gap = 20;
        int totalWidth = labelWidth + gap + 200;
        int labelX = centerX - totalWidth / 2;

        // labels (translatable)
        guiGraphics.drawString(this.font, Component.translatable("fpsoptimizer.config.default_fps"), labelX, startY + 6, 0xFFFFFF);

        startY += spacing;
        guiGraphics.drawString(this.font, Component.translatable("fpsoptimizer.config.idle_fps"), labelX, startY + 6, 0xFFFFFF);

        startY += spacing;
        guiGraphics.drawString(this.font, Component.translatable("fpsoptimizer.config.afk_timeout"), labelX, startY + 6, 0xFFFFFF);

        startY += spacing;
        guiGraphics.drawString(this.font, Component.translatable("fpsoptimizer.config.show_idle_message"), labelX, startY + 6, 0xFFFFFF);

        startY += spacing;
        guiGraphics.drawString(this.font, Component.translatable("fpsoptimizer.config.idle_message"), labelX, startY + 6, 0xFFFFFF);

        startY += spacing;
        guiGraphics.drawString(this.font, Component.translatable("fpsoptimizer.config.dark_background"), labelX, startY + 6, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // custom slider class..
    private static class IntSlider extends AbstractSliderButton {
        private final int minValue;
        private final int maxValue;
        private final String translationKey;

        public IntSlider(int x, int y, int width, int height, int currentValue, int minValue, int maxValue, String translationKey) {
            super(x, y, width, height, Component.literal(""), (double)(currentValue - minValue) / (maxValue - minValue));
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.translationKey = translationKey;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable(translationKey).append(": " + getIntValue()));
        }

        @Override
        protected void applyValue() {

        }

        public int getIntValue() {
            return (int) Mth.lerp(this.value, this.minValue, this.maxValue);
        }
    }
}