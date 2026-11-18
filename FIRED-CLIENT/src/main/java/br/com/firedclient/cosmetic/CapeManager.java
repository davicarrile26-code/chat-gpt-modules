package br.com.firedclient.cosmetic;

public final class CapeManager {
    private static String selectedCape = "fired_flame";
    private CapeManager() {}
    public static String getSelectedCape() { return selectedCape; }
    public static void selectCape(String capeId) {
        if ("fired_flame".equals(capeId) || "royal_ember".equals(capeId)
                || "inferno_stripes".equals(capeId)) selectedCape = capeId;
    }
    public static String getTexturePath() { return "firedclient:capes/" + selectedCape + ".png"; }
}
