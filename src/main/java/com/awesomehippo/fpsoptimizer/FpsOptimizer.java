package com.awesomehippo.fpsoptimizer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(FpsOptimizer.MODID)
public class FpsOptimizer {
    public static final String MODID = "fpsoptimizer";
    private long lastActivityTime = System.currentTimeMillis();
    private boolean isIdle = false;
    long idleTime = 0;

    public FpsOptimizer() {
        // register mod configuration
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        // register the screen config handler..
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ConfigScreen(screen)));

        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // reset the timer on join, to prevent the player being directly considered as afk
        if (event.getEntity() == Minecraft.getInstance().player) {
            lastActivityTime = System.currentTimeMillis();
            isIdle = false;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        // only when moving (camera movement = still idle)
        boolean isPlayerActive = mc.options.keyUp.isDown() || mc.options.keyDown.isDown() || mc.options.keyLeft.isDown() || mc.options.keyRight.isDown() || mc.options.keyJump.isDown();

        if (isPlayerActive) {
            if (isIdle) {
                mc.options.framerateLimit().set(Config.DEFAULT_FPS.get());
                isIdle = false;
            }
            lastActivityTime = System.currentTimeMillis();
        }

        idleTime = System.currentTimeMillis() - lastActivityTime;

        // otherwise we drop the FPS as they've been idle
        if (idleTime > Config.AFK_TIMEOUT.get() * 1000 && !isPlayerActive && !isIdle) {
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