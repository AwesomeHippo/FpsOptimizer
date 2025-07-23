package com.awesomehippo.fpsoptimizer;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue DEFAULT_FPS;
    public static final ModConfigSpec.IntValue IDLE_FPS;
    public static final ModConfigSpec.IntValue AFK_TIMEOUT;
    public static final ModConfigSpec.BooleanValue SHOW_IDLE_MESSAGE;
    public static final ModConfigSpec.ConfigValue<String> IDLE_MESSAGE;
    public static final ModConfigSpec.BooleanValue DARK_BACKGROUND;

    // config settings handled by neofroge!
    static {
        DEFAULT_FPS = BUILDER.translation("fpsoptimizer.config.default_fps").defineInRange("default_fps", 60, 10, 260);
        IDLE_FPS = BUILDER.translation("fpsoptimizer.config.idle_fps").defineInRange("idle_fps", 15, 10, 260);
        AFK_TIMEOUT = BUILDER.translation("fpsoptimizer.config.afk_timeout").defineInRange("afk_timeout", 12, 1, 3600);
        SHOW_IDLE_MESSAGE = BUILDER.translation("fpsoptimizer.config.show_idle_message").define("show_idle_message", true);
        IDLE_MESSAGE = BUILDER.translation("fpsoptimizer.config.idle_message").define("idle_message", "Detected inactivity: FPS reduced to %s");
        DARK_BACKGROUND = BUILDER.translation("fpsoptimizer.config.dark_background").define("dark_background", true);
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
