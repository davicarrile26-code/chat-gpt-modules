package br.com.firedclient.ui;

import br.com.firedclient.FiredClient;
import br.com.firedclient.module.CatalogModule;
import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.impl.FpsModule;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class ClickGui extends GuiScreen {
    private static final int TAB_MODS = 0;
    private static final int TAB_COSMETICS = 1;
    private int tab = TAB_MODS;
    private int panelLeft;
    private int panelTop;
    private int scroll;
    private int selectedCosmeticSection;
    private ClientModule settingsModule;

    @Override
    public void initGui() {
        panelLeft = width / 2 - 175;
        panelTop = height / 2 - 115;
        scroll = 0;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && tab == TAB_MODS && settingsModule == null) {
            int max = Math.max(0, FiredClient.MODULES.getModules().size() - 8);
            scroll = Math.max(0, Math.min(max, scroll + (wheel > 0 ? -2 : 2)));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int right = panelLeft + 350;
        int bottom = panelTop + 230;

        drawRect(panelLeft, panelTop, right, bottom, 0xF0100D0C);
        drawGradientRect(panelLeft, panelTop, right, panelTop + 3, 0xFFFF3517, 0xFFFFA51C);
        drawRect(panelLeft, panelTop + 3, right, panelTop + 43, 0xFF1D1412);

        // Brand mark and title
        drawRect(panelLeft + 12, panelTop + 11, panelLeft + 34, panelTop + 33, 0xFF76220F);
        drawString(fontRendererObj, "F", panelLeft + 19, panelTop + 17, 0xFFFFB02E);
        drawString(fontRendererObj, "FIRED CLIENT", panelLeft + 42, panelTop + 10, 0xFFFF8A24);
        drawString(fontRendererObj, "1.8.9  |  CUSTOM CLIENT", panelLeft + 42, panelTop + 24, 0xFFBDA69A);

        // Main navigation
        drawTab(panelLeft + 190, panelTop + 13, 64, "MODS", tab == TAB_MODS);
        drawTab(panelLeft + 258, panelTop + 13, 82, "COSMETICS", tab == TAB_COSMETICS);

        if (tab == TAB_MODS) {
            drawMods(mouseX, mouseY, right, bottom);
        } else {
            drawCosmetics(mouseX, mouseY, right, bottom);
        }

        if (settingsModule != null) drawSettings(right, mouseX, mouseY);
        drawString(fontRendererObj, "RSHIFT  •  CLOSE", panelLeft + 12, bottom - 12, 0xFF9F887D);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawTab(int x, int y, int w, String label, boolean active) {
        int color = active ? 0xFF713018 : 0xFF30201B;
        drawRect(x, y, x + w, y + 22, color);
        if (active) drawRect(x, y + 20, x + w, y + 22, 0xFFFF7A1A);
        drawCenteredString(fontRendererObj, label, x + w / 2, y + 7, active ? 0xFFFFC18A : 0xFFB9A69B);
    }

    private void drawMods(int mouseX, int mouseY, int right, int bottom) {
        drawString(fontRendererObj, "MODULES", panelLeft + 14, panelTop + 53, 0xFFFF9B42);
        drawString(fontRendererObj, "Clique no nome para ligar/desligar", panelLeft + 91, panelTop + 53, 0xFF9F887D);
        drawRect(panelLeft + 10, panelTop + 69, right - 10, bottom - 22, 0xB8201714);

        List<ClientModule> modules = FiredClient.MODULES.getModules();
        for (int row = 0; row < 8; row++) {
            int index = scroll + row;
            if (index >= modules.size()) break;
            ClientModule module = modules.get(index);
            int y = panelTop + 75 + row * 17;
            boolean hovered = inside(mouseX, mouseY, panelLeft + 13, y - 3, right - 13, y + 13);
            if (hovered) drawRect(panelLeft + 13, y - 3, right - 13, y + 13, 0x663D2118);

            String category = module instanceof CatalogModule ? ((CatalogModule) module).getCategory() : "HUD";
            drawString(fontRendererObj, category.toUpperCase(), panelLeft + 18, y, 0xFFFF9B42);
            drawString(fontRendererObj, module.getName(), panelLeft + 104, y, 0xFFF5E9DF);

            if (module instanceof CatalogModule) {
                drawString(fontRendererObj, "SOON", right - 83, y, 0xFFB98770);
            } else {
                drawString(fontRendererObj, module.isEnabled() ? "ON" : "OFF", right - 82, y,
                        module.isEnabled() ? 0xFFFF7A1A : 0xFF9B8A80);
            }
            drawString(fontRendererObj, "⚙", right - 29, y - 1, 0xFFFFB45B);
        }
        drawString(fontRendererObj, "66 ENTRADAS  •  SCROLL PARA NAVEGAR", panelLeft + 14, bottom - 24, 0xFFB99A87);
    }

    private void drawCosmetics(int mouseX, int mouseY, int right, int bottom) {
        drawString(fontRendererObj, "PERSONALIZE SEU FIRED", panelLeft + 14, panelTop + 54, 0xFFFF9B42);
        drawString(fontRendererObj, "Escolha uma seção para ver os acessórios", panelLeft + 14, panelTop + 68, 0xFFB9A69B);

        String[] sections = {"CAPES", "ASAS", "CHAPÉUS", "TRAILS", "AURAS"};
        for (int i = 0; i < sections.length; i++) {
            int x = panelLeft + 14 + (i % 3) * 108;
            int y = panelTop + 91 + (i / 3) * 43;
            boolean active = selectedCosmeticSection == i;
            drawRect(x, y, x + 99, y + 34, active ? 0xFF713018 : 0xFF281A16);
            if (active) drawRect(x, y + 31, x + 99, y + 34, 0xFFFF7A1A);
            drawCenteredString(fontRendererObj, sections[i], x + 49, y + 12,
                    active ? 0xFFFFC18A : 0xFFE6D4C8);
        }

        drawRect(panelLeft + 14, panelTop + 184, right - 14, bottom - 24, 0xFF201714);
        drawString(fontRendererObj, "COSMETICS", panelLeft + 25, panelTop + 194, 0xFFFF9B42);
        drawString(fontRendererObj, "A vitrine está pronta; os itens serão adicionados", panelLeft + 25,
                panelTop + 207, 0xFFB9A69B);
    }

    private void drawSettings(int right, int mouseX, int mouseY) {
        int x = right - 166;
        int y = panelTop + 52;
        drawRect(x, y, right - 10, panelTop + 164, 0xFF241714);
        drawRect(x, y, right - 10, y + 2, 0xFFFF7A1A);
        drawString(fontRendererObj, settingsModule.getName() + " SETTINGS", x + 9, y + 10, 0xFFFFA34A);

        if (settingsModule instanceof FpsModule) {
            drawOption(x + 9, y + 31, "Mostrar FPS", FpsModule.showFps);
            drawOption(x + 9, y + 51, "Background", FpsModule.showBackground);
            drawString(fontRendererObj, "Clique para alternar", x + 9, y + 78, 0xFFB9A69B);
        } else {
            drawString(fontRendererObj, "Sem opcoes ainda", x + 9, y + 39, 0xFFB9A69B);
        }
        drawString(fontRendererObj, "FECHAR  X", x + 9, y + 94, 0xFFFF9B42);
    }

    private void drawOption(int x, int y, String label, boolean enabled) {
        drawString(fontRendererObj, label, x, y, 0xFFEEDFD5);
        drawString(fontRendererObj, enabled ? "ON" : "OFF", x + 104, y,
                enabled ? 0xFFFF7A1A : 0xFF9B8A80);
    }

    private boolean inside(int mx, int my, int left, int top, int right, int bottom) {
        return mx >= left && mx <= right && my >= top && my <= bottom;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int right = panelLeft + 350;
            if (inside(mouseX, mouseY, panelLeft + 190, panelTop + 13, panelLeft + 254, panelTop + 35)) {
                tab = TAB_MODS;
                settingsModule = null;
            } else if (inside(mouseX, mouseY, panelLeft + 258, panelTop + 13, right - 10, panelTop + 35)) {
                tab = TAB_COSMETICS;
                settingsModule = null;
            } else if (tab == TAB_MODS && settingsModule != null) {
                int x = right - 166;
                int y = panelTop + 52;
                if (inside(mouseX, mouseY, x + 8, y + 89, right - 10, y + 108)) {
                    settingsModule = null;
                } else if (settingsModule instanceof FpsModule) {
                    if (inside(mouseX, mouseY, x + 5, y + 27, right - 12, y + 49)) {
                        FpsModule.showFps = !FpsModule.showFps;
                    } else if (inside(mouseX, mouseY, x + 5, y + 48, right - 12, y + 70)) {
                        FpsModule.showBackground = !FpsModule.showBackground;
                    }
                }
            } else if (tab == TAB_MODS) {
                List<ClientModule> modules = FiredClient.MODULES.getModules();
                for (int row = 0; row < 8; row++) {
                    int index = scroll + row;
                    if (index >= modules.size()) break;
                    int y = panelTop + 75 + row * 17;
                    ClientModule module = modules.get(index);
                    if (inside(mouseX, mouseY, right - 43, y - 4, right - 13, y + 13)) {
                        settingsModule = module;
                        break;
                    }
                    if (inside(mouseX, mouseY, panelLeft + 13, y - 3, right - 90, y + 13)
                            && !(module instanceof CatalogModule)) {
                        module.toggle();
                        break;
                    }
                }
            } else if (tab == TAB_COSMETICS) {
                String[] sections = {"CAPES", "ASAS", "CHAPÉUS", "TRAILS", "AURAS"};
                for (int i = 0; i < sections.length; i++) {
                    int x = panelLeft + 14 + (i % 3) * 108;
                    int y = panelTop + 91 + (i / 3) * 43;
                    if (inside(mouseX, mouseY, x, y, x + 99, y + 34)) selectedCosmeticSection = i;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
