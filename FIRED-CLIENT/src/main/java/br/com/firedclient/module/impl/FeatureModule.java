package br.com.firedclient.module.impl;

import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.HudModule;
import br.com.firedclient.module.TickModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Small client-side HUD and quality-of-life modules for Minecraft 1.8.9. */
public class FeatureModule extends ClientModule implements HudModule, TickModule {
    private final String category;
    private float previousGamma;
    private boolean changedGamma;
    private long stopwatchStarted;

    public FeatureModule(String name, String category) {
        super(name);
        this.category = category;
    }

    public String getCategory() { return category; }

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getMinecraft();
        if ("Fullbright".equals(getName()) && mc != null) {
            previousGamma = mc.gameSettings.gammaSetting;
            changedGamma = true;
            mc.gameSettings.gammaSetting = 100.0F;
        }
        if ("Stopwatch".equals(getName())) stopwatchStarted = System.currentTimeMillis();
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
        if ("Keystrokes".equals(getName())) {
            renderKeystrokes(mc);
            return;
        }
        String value = getHudValue(mc);
        if (value != null && value.length() > 0)
            mc.fontRendererObj.drawStringWithShadow(value, 4, getHudY(), 0xFFFF4D4D);
    }

    private int getHudY() {
        String n = getName();
        if ("Coordinates".equals(n)) return 16;
        if ("Ping".equals(n)) return 28;
        if ("Clock".equals(n)) return 40;
        if ("Server Address".equals(n)) return 52;
        if ("Memory".equals(n)) return 64;
        if ("Saturation".equals(n)) return 76;
        if ("Armor Status".equals(n)) return 88;
        if ("Potion Effects".equals(n)) return 100;
        if ("Keystrokes".equals(n)) return 112;
        if ("Stopwatch".equals(n)) return 124;
        if ("Item Counter".equals(n)) return 136;
        if ("Direction".equals(n)) return 148;
        if ("Biome".equals(n)) return 160;
        if ("Speed".equals(n)) return 172;
        if ("Durability".equals(n)) return 184;
        if ("Entity Counter".equals(n)) return 196;
        if ("Player Counter".equals(n)) return 208;
        if ("Health".equals(n)) return 220;
        if ("Food Level".equals(n)) return 232;
        if ("World Time".equals(n)) return 244;
        if ("Weather".equals(n)) return 256;
        if ("Light Level".equals(n)) return 268;
        return 280;
    }

    private String getHudValue(Minecraft mc) {
        String n = getName();
        if ("Coordinates".equals(n))
            return String.format("XYZ: %d, %d, %d", (int) mc.thePlayer.posX,
                    (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
        if ("Ping".equals(n)) {
            if (mc.getNetHandler() == null) return "Ping: --";
            net.minecraft.client.network.NetworkPlayerInfo info =
                    mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            return "Ping: " + (info == null ? "--" : info.getResponseTime() + " ms");
        }
        if ("Clock".equals(n)) return "Hora: " + new SimpleDateFormat("HH:mm").format(new Date());
        if ("Server Address".equals(n))
            return mc.getCurrentServerData() == null ? "Servidor: Singleplayer"
                    : "Servidor: " + mc.getCurrentServerData().serverIP;
        if ("Memory".equals(n)) {
            Runtime r = Runtime.getRuntime();
            return "Memoria: " + ((r.totalMemory() - r.freeMemory()) / 1048576L)
                    + "/" + (r.maxMemory() / 1048576L) + " MB";
        }
        if ("Saturation".equals(n))
            return "Saturacao: " + mc.thePlayer.getFoodStats().getSaturationLevel();
        if ("Armor Status".equals(n)) {
            StringBuilder b = new StringBuilder("Armadura: ");
            for (int i = 3; i >= 0; i--) {
                ItemStack stack = mc.thePlayer.inventory.armorInventory[i];
                b.append(stack == null ? "-" : stack.getDisplayName());
                if (i > 0) b.append(" | ");
            }
            return b.toString();
        }
        if ("Potion Effects".equals(n))
            return "Efeitos: " + mc.thePlayer.getActivePotionEffects().size();
        if ("Keystrokes".equals(n)) return null;
        if ("Stopwatch".equals(n)) {
            long seconds = Math.max(0L, (System.currentTimeMillis() - stopwatchStarted) / 1000L);
            return String.format("Cronometro: %02d:%02d", seconds / 60L, seconds % 60L);
        }
        if ("Item Counter".equals(n)) {
            ItemStack held = mc.thePlayer.getHeldItem();
            return "Item: " + (held == null ? "Nenhum" : held.getDisplayName() + " x" + held.stackSize);
        }
        if ("Direction".equals(n)) {
            float yaw = (mc.thePlayer.rotationYaw % 360.0F + 360.0F) % 360.0F;
            String[] dirs = {"Sul", "Oeste", "Norte", "Leste"};
            return "Direcao: " + dirs[Math.round(yaw / 90.0F) % 4];
        }
        if ("Biome".equals(n)) {
            BlockPos p = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            return "Bioma: " + mc.theWorld.getBiomeGenForCoords(p).biomeName;
        }
        if ("Speed".equals(n)) {
            double speed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX
                    + mc.thePlayer.motionZ * mc.thePlayer.motionZ) * 20.0D;
            return String.format("Velocidade: %.2f m/s", speed);
        }
        if ("Durability".equals(n)) {
            ItemStack held = mc.thePlayer.getHeldItem();
            if (held == null || !held.isItemStackDamageable()) return "Durabilidade: --";
            return "Durabilidade: " + (held.getMaxDamage() - held.getItemDamage())
                    + "/" + held.getMaxDamage();
        }
        if ("Entity Counter".equals(n))
            return "Entidades: " + mc.theWorld.loadedEntityList.size();
        if ("Player Counter".equals(n))
            return "Jogadores: " + mc.theWorld.playerEntities.size();
        if ("Health".equals(n))
            return String.format("Vida: %.1f/%.1f", mc.thePlayer.getHealth(), mc.thePlayer.getMaxHealth());
        if ("Food Level".equals(n))
            return "Fome: " + mc.thePlayer.getFoodStats().getFoodLevel() + "/20";
        if ("World Time".equals(n)) {
            long time = mc.theWorld.getWorldTime() % 24000L;
            long hours = (time / 1000L + 6L) % 24L;
            long minutes = (time % 1000L) * 60L / 1000L;
            return String.format("Horario: %02d:%02d", hours, minutes);
        }
        if ("Weather".equals(n))
            return "Clima: " + (mc.theWorld.isRaining() ? "Chovendo" : "Limpo");
        if ("Light Level".equals(n)) {
            BlockPos p = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            return "Luz: " + mc.theWorld.getLightFromNeighbors(p);
        }
        return null;
    }

    private void renderKeystrokes(Minecraft mc) {
        ScaledResolution sr = new ScaledResolution(mc);
        int keyW = 24, keyH = 22, gap = 3;
        int x = 6, y = sr.getScaledHeight() - 6 - (keyH * 4 + gap * 3);
        drawKey(mc, x + keyW + gap, y, keyW, keyH, "W", mc.gameSettings.keyBindForward.isKeyDown());
        drawKey(mc, x, y + keyH + gap, keyW, keyH, "A", mc.gameSettings.keyBindLeft.isKeyDown());
        drawKey(mc, x + keyW + gap, y + keyH + gap, keyW, keyH, "S", mc.gameSettings.keyBindBack.isKeyDown());
        drawKey(mc, x + (keyW + gap) * 2, y + keyH + gap, keyW, keyH, "D", mc.gameSettings.keyBindRight.isKeyDown());
        drawKey(mc, x, y + (keyH + gap) * 2, keyW * 3 + gap * 2, keyH, "SPACE",
                mc.gameSettings.keyBindJump.isKeyDown());
        int mouseY = y + (keyH + gap) * 3;
        drawKey(mc, x, mouseY, keyW + 10, keyH, "LMB", Mouse.isButtonDown(0));
        drawKey(mc, x + keyW + gap + 10, mouseY, keyW + 10, keyH, "RMB", Mouse.isButtonDown(1));
    }

    private void drawKey(Minecraft mc, int x, int y, int w, int h, String label, boolean pressed) {
        Gui.drawRect(x, y, x + w, y + h, 0xD9100D0D);
        Gui.drawRect(x, y, x + w, y + 2, pressed ? 0xFFFF343F : 0xFF552027);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0xFF080606);
        int color = pressed ? 0xFFFFFFFF : 0xFFCCBDBD;
        int textX = x + (w - mc.fontRendererObj.getStringWidth(label)) / 2;
        int textY = y + (h - mc.fontRendererObj.FONT_HEIGHT) / 2;
        mc.fontRendererObj.drawStringWithShadow(label, textX, textY, color);
    }
}
