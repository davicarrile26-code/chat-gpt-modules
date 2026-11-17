package br.com.firedclient.compat;

import net.minecraftforge.fml.common.Loader;

/** Detects optional OptiFine installation without bundling or depending on its jar. */
public final class OptiFineCompatibility {
    private OptiFineCompatibility() {}

    public static boolean isPresent() {
        try {
            if (Loader.isModLoaded("optifine") || Loader.isModLoaded("OptiFine")) return true;
        } catch (Throwable ignored) {
            // Loader may not be available during very early startup.
        }
        try {
            Class.forName("Config", false, OptiFineCompatibility.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static String getStatus() {
        return isPresent() ? "OptiFine detectado" : "OptiFine nao detectado";
    }
}
