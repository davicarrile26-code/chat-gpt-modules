package br.com.firedclient.ui;

import br.com.firedclient.FiredClient;
import br.com.firedclient.compat.OptiFineCompatibility;
import br.com.firedclient.module.CatalogModule;
import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.impl.FpsModule;
import br.com.firedclient.module.impl.FeatureModule;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FIRED Client interface. The layout is inspired by modern Minecraft client
 * menus, while keeping FIRED's own colors, labels and visual identity.
 */
public class ClickGui extends GuiScreen {
    private static final int TAB_MODS = 0;
    private static final int TAB_COSMETICS = 1;
    private static final int PANEL_WIDTH = 520;
    private static final int PANEL_HEIGHT = 320;
    private static final int SIDEBAR_WIDTH = 118;
    private static final int ROW_HEIGHT = 23;
    private static final int VISIBLE_ROWS = 8;

    private static final String[] CATEGORIES = {
            "TODOS", "HUD", "RENDER", "COMBAT", "MOVEMENT", "WORLD", "MISC", "COSMETICS"
    };
    private static final String[] COSMETIC_SECTIONS = {
            "CAPES", "ASAS", "CHAPEUS", "TRAILS", "AURAS"
    };

    private int tab = TAB_MODS;
    private int panelLeft;
    private int panelTop;
    private int scroll;
    private int selectedCosmeticSection;
    private String selectedCategory = "TODOS";
    private ClientModule settingsModule;

    @Override
    public void initGui() {
        panelLeft = width / 2 - PANEL_WIDTH / 2;
        panelTop = height / 2 - PANEL_HEIGHT / 2;
        scroll = 0;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && tab == TAB_MODS && settingsModule == null) {
            int max = Math.max(0, getFilteredModules().size() - VISIBLE_ROWS);
            scroll = Math.max(0, Math.min(max, scroll + (wheel > 0 ? -2 : 2)));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int right = panelLeft + PANEL_WIDTH;
        int bottom = panelTop + PANEL_HEIGHT;

        // Main shell and FIRED accent
        drawRect(panelLeft, panelTop, right, bottom, 0xF00D0B0A);
        drawGradientRect(panelLeft, panelTop, right, panelTop + 3, 0xFFFF3517, 0xFFFFA51C);
        drawRect(panelLeft, panelTop + 3, right, panelTop + 49, 0xFF171211);
        drawRect(panelLeft, panelTop + 49, right, bottom, 0xF0120F0E);

        // Brand mark
        drawGradientRect(panelLeft + 13, panelTop + 12, panelLeft + 39, panelTop + 38,
                0xFF9D260F, 0xFFFF741B);
        drawCenteredString(fontRendererObj, "F", panelLeft + 26, panelTop + 19, 0xFFFFFFFF);
        drawString(fontRendererObj, "FIRED CLIENT", panelLeft + 48, panelTop + 12, 0xFFFF963B);
        drawString(fontRendererObj, "1.8.9  /  CLIENT MENU", panelLeft + 48, panelTop + 27, 0xFF9F8D84);

        drawTab(panelLeft + 330, panelTop + 13, 70, "MODS", tab == TAB_MODS);
        drawTab(panelLeft + 405, panelTop + 13, 102, "COSMETICOS", tab == TAB_COSMETICS);

        if (tab == TAB_MODS) {
            drawMods(mouseX, mouseY, right, bottom);
        } else {
            drawCosmetics(mouseX, mouseY, right, bottom);
        }

        if (settingsModule != null) drawSettings(right);
        drawString(fontRendererObj, "RSHIFT  /  FECHAR", panelLeft + 14, bottom - 14, 0xFF88766E);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawTab(int x, int y, int w, String label, boolean active) {
        drawRect(x, y, x + w, y + 24, active ? 0xFF57251A : 0xFF241B18);
        if (active) drawRect(x, y + 21, x + w, y + 24, 0xFFFF7624);
        drawCenteredString(fontRendererObj, label, x + w / 2, y + 8,
                active ? 0xFFFFC18A : 0xFFB3A39A);
    }

    private void drawMods(int mouseX, int mouseY, int right, int bottom) {
        int sideX = panelLeft + 10;
        int sideTop = panelTop + 60;
        int contentX = panelLeft + SIDEBAR_WIDTH + 18;

        drawRect(sideX, sideTop, panelLeft + SIDEBAR_WIDTH, bottom - 27, 0xFF191413);
        drawString(fontRendererObj, "CATEGORIAS", sideX + 9, sideTop + 10, 0xFFFF8B32);

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = sideTop + 29 + i * 25;
            boolean active = selectedCategory.equals(CATEGORIES[i]);
            boolean hovered = inside(mouseX, mouseY, sideX + 4, y - 4,
                    panelLeft + SIDEBAR_WIDTH - 4, y + 17);
            if (active) {
                drawRect(sideX + 4, y - 4, panelLeft + SIDEBAR_WIDTH - 4, y + 17, 0xFF542419);
                drawRect(sideX + 4, y - 4, sideX + 7, y + 17, 0xFFFF7624);
            } else if (hovered) {
                drawRect(sideX + 4, y - 4, panelLeft + SIDEBAR_WIDTH - 4, y + 17, 0xFF2B201C);
            }
            drawString(fontRendererObj, CATEGORIES[i], sideX + 13, y + 2,
                    active ? 0xFFFFB36B : 0xFFB7A69D);
        }

        drawString(fontRendererObj, "MODULOS", contentX, panelTop + 62, 0xFFFF963B);
        drawString(fontRendererObj, "Clique para ativar  /  CFG para configurar",
                contentX + 66, panelTop + 62, 0xFF95837A);
        drawRect(contentX, panelTop + 78, right - 13, bottom - 28, 0xFF191413);

        List<ClientModule> modules = getFilteredModules();
        if (modules.isEmpty()) {
            drawCenteredString(fontRendererObj, "NENHUM MODULO NESTA CATEGORIA",
                    contentX + (right - contentX) / 2, panelTop + 120, 0xFFB7A69D);
        }

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int index = scroll + row;
            if (index >= modules.size()) break;
            ClientModule module = modules.get(index);
            int y = panelTop + 85 + row * ROW_HEIGHT;
            boolean hovered = inside(mouseX, mouseY, contentX + 5, y - 3, right - 18, y + 19);

            if (hovered) drawRect(contentX + 5, y - 3, right - 18, y + 19, 0xFF2B201C);
            String category = module instanceof CatalogModule
                    ? ((CatalogModule) module).getCategory().toUpperCase()
                    : (module instanceof FeatureModule ? ((FeatureModule) module).getCategory() : "HUD");
            drawString(fontRendererObj, module.getName(), contentX + 12, y + 2, 0xFFF0E5DE);
            drawString(fontRendererObj, category, contentX + 145, y + 2, 0xFF9D8980);

            if (module instanceof CatalogModule) {
                drawString(fontRendererObj, "EM BREVE", right - 112, y + 2, 0xFFB58B73);
            } else {
                boolean enabled = module.isEnabled();
                drawRect(right - 75, y - 1, right - 43, y + 14,
                        enabled ? 0xFF713019 : 0xFF332824);
                drawString(fontRendererObj, enabled ? "ON" : "OFF", right - 68, y + 3,
                        enabled ? 0xFFFFA04A : 0xFFAA9A91);
            }
            drawString(fontRendererObj, "[CFG]", right - 37, y + 2, 0xFFFFA34A);
        }

        drawString(fontRendererObj, modules.size() + " MODULOS  /  SCROLL PARA NAVEGAR",
                contentX + 2, bottom - 25, 0xFF9C887F);
    }

    private List<ClientModule> getFilteredModules() {
        List<ClientModule> result = new ArrayList<ClientModule>();
        for (ClientModule module : FiredClient.MODULES.getModules()) {
            String category = module instanceof CatalogModule
                    ? ((CatalogModule) module).getCategory().toUpperCase() : "HUD";
            if ("TODOS".equals(selectedCategory) || selectedCategory.equals(category)) {
                result.add(module);
            }
        }
        return result;
    }

    private void drawCosmetics(int mouseX, int mouseY, int right, int bottom) {
        int contentX = panelLeft + 16;
        drawString(fontRendererObj, "PERSONALIZACAO", contentX, panelTop + 62, 0xFFFF963B);
        drawString(fontRendererObj, "Cosmeticos FIRED e colecoes de parceiros",
                contentX + 112, panelTop + 62, 0xFF95837A);

        for (int i = 0; i < COSMETIC_SECTIONS.length; i++) {
            int x = contentX + (i % 3) * 164;
            int y = panelTop + 86 + (i / 3) * 75;
            boolean active = selectedCosmeticSection == i;
            boolean hovered = inside(mouseX, mouseY, x, y, x + 151, y + 61);
            drawRect(x, y, x + 151, y + 61,
                    active ? 0xFF542419 : (hovered ? 0xFF30211C : 0xFF211815));
            if (active) drawRect(x, y + 57, x + 151, y + 61, 0xFFFF7624);
            drawRect(x + 10, y + 10, x + 39, y + 39, 0xFF713019);
            drawCenteredString(fontRendererObj, String.valueOf(i + 1), x + 24, y + 21, 0xFFFFB36B);
            drawString(fontRendererObj, COSMETIC_SECTIONS[i], x + 48, y + 13,
                    active ? 0xFFFFC18A : 0xFFE6D8D0);
            drawString(fontRendererObj, "EM DESENVOLVIMENTO", x + 48, y + 29, 0xFFAA8C7D);
        }

        drawRect(contentX, panelTop + 246, right - 16, bottom - 28, 0xFF191413);
        drawString(fontRendererObj, "COLECAO FIRED", contentX + 12, panelTop + 255, 0xFFFF963B);
        drawString(fontRendererObj, "Os itens cosmeticos serao adicionados e testados nesta area.",
                contentX + 12, panelTop + 270, 0xFFB7A69D);
        drawString(fontRendererObj, "COMPATIBILIDADE: " + OptiFineCompatibility.getStatus(),
                contentX + 12, panelTop + 285, 0xFFFFA04A);
    }

    private void drawSettings(int right) {
        int x = right - 190;
        int y = panelTop + 58;
        drawRect(x, y, right - 12, panelTop + 190, 0xFF251916);
        drawRect(x, y, right - 12, y + 3, 0xFFFF7624);
        drawString(fontRendererObj, settingsModule.getName().toUpperCase(),
                x + 10, y + 12, 0xFFFFA34A);
        drawString(fontRendererObj, "CONFIGURACOES", x + 10, y + 26, 0xFFB7A69D);

        if (settingsModule instanceof FpsModule) {
            drawOption(x + 10, y + 51, "Mostrar FPS", FpsModule.showFps);
            drawOption(x + 10, y + 72, "Background", FpsModule.showBackground);
        } else {
            drawString(fontRendererObj, "Opcoes ainda nao implementadas",
                    x + 10, y + 55, 0xFFB7A69D);
        }
        drawString(fontRendererObj, "[ FECHAR ]", x + 10, y + 108, 0xFFFF963B);
    }

    private void drawOption(int x, int y, String label, boolean enabled) {
        drawString(fontRendererObj, label, x, y, 0xFFEEDFD5);
        drawString(fontRendererObj, enabled ? "ON" : "OFF", x + 132, y,
                enabled ? 0xFFFF7A1A : 0xFF9B8A80);
    }

    private boolean inside(int mx, int my, int left, int top, int right, int bottom) {
        return mx >= left && mx <= right && my >= top && my <= bottom;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int right = panelLeft + PANEL_WIDTH;

            if (inside(mouseX, mouseY, panelLeft + 330, panelTop + 13,
                    panelLeft + 400, panelTop + 37)) {
                tab = TAB_MODS;
                settingsModule = null;
            } else if (inside(mouseX, mouseY, panelLeft + 405, panelTop + 13,
                    panelLeft + 507, panelTop + 37)) {
                tab = TAB_COSMETICS;
                settingsModule = null;
            } else if (tab == TAB_MODS && settingsModule != null) {
                int x = right - 190;
                int y = panelTop + 58;
                if (inside(mouseX, mouseY, x + 5, y + 100, right - 12, y + 127)) {
                    settingsModule = null;
                } else if (settingsModule instanceof FpsModule) {
                    if (inside(mouseX, mouseY, x + 5, y + 45, right - 12, y + 68)) {
                        FpsModule.showFps = !FpsModule.showFps;
                    } else if (inside(mouseX, mouseY, x + 5, y + 67, right - 12, y + 90)) {
                        FpsModule.showBackground = !FpsModule.showBackground;
                    }
                }
            } else if (tab == TAB_MODS) {
                int sideX = panelLeft + 10;
                int sideTop = panelTop + 60;
                for (int i = 0; i < CATEGORIES.length; i++) {
                    int y = sideTop + 29 + i * 25;
                    if (inside(mouseX, mouseY, sideX + 4, y - 4,
                            panelLeft + SIDEBAR_WIDTH - 4, y + 17)) {
                        selectedCategory = CATEGORIES[i];
                        scroll = 0;
                        break;
                    }
                }

                List<ClientModule> modules = getFilteredModules();
                int contentX = panelLeft + SIDEBAR_WIDTH + 18;
                for (int row = 0; row < VISIBLE_ROWS; row++) {
                    int index = scroll + row;
                    if (index >= modules.size()) break;
                    int y = panelTop + 85 + row * ROW_HEIGHT;
                    ClientModule module = modules.get(index);
                    if (inside(mouseX, mouseY, right - 43, y - 3, right - 15, y + 19)) {
                        settingsModule = module;
                        break;
                    }
                    if (inside(mouseX, mouseY, contentX + 5, y - 3, right - 48, y + 19)
                            && !(module instanceof CatalogModule)) {
                        module.toggle();
                        break;
                    }
                }
            } else if (tab == TAB_COSMETICS) {
                int contentX = panelLeft + 16;
                for (int i = 0; i < COSMETIC_SECTIONS.length; i++) {
                    int x = contentX + (i % 3) * 164;
                    int y = panelTop + 86 + (i / 3) * 75;
                    if (inside(mouseX, mouseY, x, y, x + 151, y + 61)) {
                        selectedCosmeticSection = i;
                        break;
                    }
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
