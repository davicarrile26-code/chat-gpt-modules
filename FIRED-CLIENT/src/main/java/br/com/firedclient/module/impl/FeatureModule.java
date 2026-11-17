package br.com.firedclient.module.impl;

import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.HudModule;
import br.com.firedclient.module.TickModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Real, client-side quality-of-life features supported by vanilla 1.8.9. */
public class FeatureModule extends ClientModule implements HudModule, TickModule {
    private final String category;
    private float previousGamma;
    private boolean changedGamma;

    public FeatureModule(String name, String category) {
        super(name);
        this.category = category;
    }

    public String getCategory() { return category; }

    @Override
    protected void onEnable() {
        if ("Fullbright".equals(getName())) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null) {
                previousGamma = mc.gameSettings.gammaSetting;
                changedGamma = true;
                mc.gameSettings.gammaSetting = 100.0F;
            }
        }
    }

    @Override
    protected void onDisable() {
        if ("Fullbright".equals(getName()) && changedGamma) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null) mc.gameSettings.gammaSetting = previousGamma;
            changedGamma = false;
        }
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) return;
        if ("Fullbright".equals(getName())) {
            mc.gameSettings.gammaSetting = 100.0F;
        } else if ("Toggle Sprint".equals(getName())) {
            EntityPlayerSP player = mc.thePlayer;
            boolean movingForward = mc.gameSettings.keyBindForward.isKeyDown();
            boolean canSprint = player.getFoodStats().getFoodLevel() > 6
                    && !player.isSneaking() && !player.isCollidedHorizontally;
            if (movingForward && canSprint) player.setSprinting(true);
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.theWorld == null) return;
        String value = getHudValue(mc);
        if (value == null || value.length() == 0) return;
        int y = getHudY();
        mc.fontRendererObj.drawStringWithShadow(value, 4, y, 0xFFFFA04A);
    }

    private int getHudY() {
        String name = getName();
        if ("Coordinates".equals(name)) return 16;
        if ("Ping".equals(name)) return 28;
        if ("Clock".equals(name)) return 40;
        if ("Server Address".equals(name)) return 52;
        if ("Memory".equals(name)) return 64;
        if ("Saturation".equals(name)) return 76;
        if ("Armor Status".equals(name)) return 88;
        return 100;
    }

    private String getHudValue(Minecraft mc) {
        String name = getName();
        if ("Coordinates".equals(name)) {
            return String.format("XYZ: %d, %d, %d", (int) mc.thePlayer.posX,
                    (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
        }
        if ("Ping".equals(name)) {
            if (mc.getNetHandler() == null) return "Ping: --";
            net.minecraft.client.network.NetworkPlayerInfo info =
                    mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            return "Ping: " + (info == null ? "--" : info.getResponseTime() + " ms");
        }
        if ("Clock".equals(name)) {
            return "Hora: " + new SimpleDateFormat("HH:mm").format(new Date());
        }
        if ("Server Address".equals(name)) {
            return mc.getCurrentServerData() == null ? "Servidor: Singleplayer"
                    : "Servidor: " + mc.getCurrentServerData().serverIP;
        }
        if ("Memory".equals(name)) {
            Runtime runtime = Runtime.getRuntime();
            long used = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
            return "Memoria: " + used + "/" + (runtime.maxMemory() / 1048576L) + " MB";
        }
        if ("Saturation".equals(name)) {
            return "Saturacao: " + mc.thePlayer.getFoodStats().getSaturationLevel();
        }
        if ("Armor Status".equals(name)) {
            StringBuilder result = new StringBuilder("Armadura: ");
            for (int i = 3; i >= 0; i--) {
                ItemStack stack = mc.thePlayer.inventory.armorInventory[i];
                result.append(stack == null ? "-" : stack.getDisplayName());
                if (i > 0) result.append(" | ");
            }
            return result.toString();
        }
        if ("Potion Effects".equals(name)) {
            return "Efeitos: " + mc.thePlayer.getActivePotionEffects().size();
        }
        return null;
    }
}
