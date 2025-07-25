package com.awesomehippo.fpsoptimizer;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue DEFAULT_FPS;
    public static final ForgeConfigSpec.IntValue IDLE_FPS;
    public static final ForgeConfigSpec.IntValue AFK_TIMEOUT;
    public static final ForgeConfigSpec.BooleanValue SHOW_IDLE_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<String> IDLE_MESSAGE;
    public static final ForgeConfigSpec.BooleanValue DARK_BACKGROUND;

    // config settings
    static {
        DEFAULT_FPS = BUILDER.translation("fpsoptimizer.config.default_fps").defineInRange("default_fps", 60, 10, 260);
        IDLE_FPS = BUILDER.translation("fpsoptimizer.config.idle_fps").defineInRange("idle_fps", 15, 10, 260);
        AFK_TIMEOUT = BUILDER.translation("fpsoptimizer.config.afk_timeout").defineInRange("afk_timeout", 12, 1, 3600);
        SHOW_IDLE_MESSAGE = BUILDER.translation("fpsoptimizer.config.show_idle_message").define("show_idle_message", true);
        IDLE_MESSAGE = BUILDER.translation("fpsoptimizer.config.idle_message").define("idle_message", "fpsoptimizer.config.default_idle_message");
        DARK_BACKGROUND = BUILDER.translation("fpsoptimizer.config.dark_background").define("dark_background", true);
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}