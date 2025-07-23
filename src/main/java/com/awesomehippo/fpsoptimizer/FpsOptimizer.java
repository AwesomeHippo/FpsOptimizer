package com.awesomehippo.fpsoptimizer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(FpsOptimizer.MODID)
public class FpsOptimizer {
    public static final String MODID = "fpsoptimizer";
    private long lastActivityTime = System.currentTimeMillis();
    private boolean isIdle = false;
    long idleTime = 0;

    public FpsOptimizer(ModContainer container) {
        NeoForge.EVENT_BUS.register(this);
        // register mod config (nicely handled by neoforge)
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    @SubscribeEvent
    public void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        // reset the timer on join, to prevent the player being directly considered as afk
        lastActivityTime = System.currentTimeMillis();
        isIdle = false;
    }


    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || event.getEntity() != mc.player) {
            return;
        }

        // only when moving (camera movement = still idle)
        boolean isPlayeerActive = mc.options.keyUp.isDown() || mc.options.keyDown.isDown() || mc.options.keyLeft.isDown() || mc.options.keyRight.isDown() || mc.options.keyJump.isDown();

        // so we reset if they're moving
        if (isPlayeerActive) {
            if (isIdle) {
                mc.options.framerateLimit().set(Config.DEFAULT_FPS.get());
                isIdle = false;
            }
            lastActivityTime = System.currentTimeMillis();
        }

        idleTime = System.currentTimeMillis() - lastActivityTime;

        // otherwise we drop the FPS as they've been idle
        if (idleTime > Config.AFK_TIMEOUT.get() * 1000 && !isPlayeerActive && !isIdle) {
            mc.options.framerateLimit().set(Config.IDLE_FPS.get());
            isIdle = true;
        }
    }

    @SubscribeEvent
    public void renderHUD(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        if (isIdle && Config.SHOW_IDLE_MESSAGE.get()) {
            String idleDisplay = Config.IDLE_MESSAGE.get().formatted(Config.IDLE_FPS.get());

            // dark background
            if (Config.DARK_BACKGROUND.get()) {
                renderDarkBackground(event.getGuiGraphics());
            }

            GuiGraphics guiGraphics = event.getGuiGraphics();

            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();

            int x = (screenWidth - mc.font.width(idleDisplay)) / 2;
            int y = screenHeight / 2 - 20;

            // custom idle message
            guiGraphics.drawString(mc.font, Component.literal(idleDisplay), x, y, 0xFFFFFF, true);
        }
    }

    private void renderDarkBackground(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        guiGraphics.fill(0, 0, screenWidth, screenHeight, 0x80000000);
    }
}
