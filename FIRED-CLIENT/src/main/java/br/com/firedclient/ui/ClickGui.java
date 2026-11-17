package br.com.firedclient.ui;

import br.com.firedclient.FiredClient;
import br.com.firedclient.compat.OptiFineCompatibility;
import br.com.firedclient.module.CatalogModule;
import br.com.firedclient.module.ClientModule;
import br.com.firedclient.module.impl.FpsModule;
import br.com.firedclient.module.impl.FeatureModule;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {
    private static final int TAB_MODS = 0, TAB_COSMETICS = 1;
    private static final int PANEL_WIDTH = 520, PANEL_HEIGHT = 320, SIDEBAR_WIDTH = 118;
    private static final int ROW_HEIGHT = 23, VISIBLE_ROWS = 8;
    private static final String[] CATEGORIES = {"TODOS","HUD","RENDER","COMBAT","MOVEMENT","WORLD","MISC","COSMETICS"};
    private static final String[] COSMETIC_SECTIONS = {"CAPES","ASAS","CHAPEUS","TRAILS","AURAS"};
    private int tab = TAB_MODS, panelLeft, panelTop, scroll, selectedCosmeticSection;
    private float guiScale = 1.0F;
    private int guiOffsetX, guiOffsetY;
    private String selectedCategory = "TODOS";
    private ClientModule settingsModule;
    private GuiTextField searchField;

    @Override public void initGui() {
        // Mantem o painel dentro da area visivel, inclusive em janelas pequenas.
        guiScale = Math.min(1.0F, Math.min((width - 20.0F) / PANEL_WIDTH,
                (height - 20.0F) / PANEL_HEIGHT));
        guiScale = Math.max(0.1F, guiScale);
        guiOffsetX = (int) ((width - PANEL_WIDTH * guiScale) / 2.0F);
        guiOffsetY = (int) ((height - PANEL_HEIGHT * guiScale) / 2.0F);
        panelLeft = 0;
        panelTop = 0;
        scroll = 0;
        searchField = new GuiTextField(9001, fontRendererObj, PANEL_WIDTH - 151, 57, 136, 19);
        searchField.setMaxStringLength(32);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setTextColor(0xFFF0E5DE);
    }

    @Override public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && tab == TAB_MODS && settingsModule == null) {
            int max = Math.max(0, getFilteredModules().size() - VISIBLE_ROWS);
            scroll = Math.max(0, Math.min(max, scroll + (wheel > 0 ? -2 : 2)));
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiOffsetX, guiOffsetY, 0.0F);
        GlStateManager.scale(guiScale, guiScale, 1.0F);
        int right = panelLeft + PANEL_WIDTH, bottom = panelTop + PANEL_HEIGHT;
        drawRect(panelLeft, panelTop, right, bottom, 0xF0101216);
        drawGradientRect(panelLeft, panelTop, right, panelTop + 2, 0xFFE33A32, 0xFFFF8742);
        drawRect(panelLeft, panelTop + 2, right, panelTop + 49, 0xFF191C22);
        drawRect(panelLeft, panelTop + 49, right, bottom, 0xF013161B);
        drawRect(panelLeft + 1, panelTop + 49, right - 1, panelTop + 50, 0xFF2B3038);
        drawFireLogo(panelLeft + 13, panelTop + 11);
        drawString(fontRendererObj, "FIRED CLIENT", panelLeft + 48, panelTop + 12, 0xFFFF6848);
        drawString(fontRendererObj, "MINECRAFT 1.8.9", panelLeft + 48, panelTop + 27, 0xFF9CA3AA);
        drawTab(panelLeft + 278, panelTop + 13, 70, "MODS", tab == TAB_MODS);
        drawTab(panelLeft + 352, panelTop + 13, 102, "COSMETICOS", tab == TAB_COSMETICS);
        if (tab == TAB_MODS) drawMods(mouseX, mouseY, right, bottom);
        else drawCosmetics(mouseX, mouseY, right, bottom);
        if (settingsModule != null) drawSettings(right);
        drawString(fontRendererObj, "RSHIFT  /  FECHAR", panelLeft + 14, bottom - 14, 0xFF88766E);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawTab(int x, int y, int w, String label, boolean active) {
        if (active) {
            drawRect(x, y + 21, x + w, y + 24, 0xFFFF573D);
            drawString(fontRendererObj, label, x + (w - fontRendererObj.getStringWidth(label)) / 2,
                    y + 7, 0xFFFFE4DC);
        } else {
            drawString(fontRendererObj, label, x + (w - fontRendererObj.getStringWidth(label)) / 2,
                    y + 7, 0xFF9CA3AA);
        }
    }

    /** Marca provisoria desenhada em pixel-art; usa a identidade FIRED sem reutilizar o logo Lunar. */
    private void drawFireLogo(int x, int y) {
        drawRect(x + 8, y, x + 17, y + 5, 0xFFFFA044);
        drawRect(x + 5, y + 5, x + 20, y + 10, 0xFFFF6A35);
        drawRect(x + 3, y + 10, x + 22, y + 19, 0xFFE52B32);
        drawRect(x + 6, y + 19, x + 19, y + 24, 0xFFB91528);
        drawRect(x + 9, y + 6, x + 14, y + 12, 0xFFFFC05A);
        drawRect(x + 8, y + 12, x + 16, y + 19, 0xFFFF8A32);
        drawRect(x + 11, y + 15, x + 15, y + 21, 0xFFFFD18A);
        drawRect(x + 0, y + 12, x + 4, y + 17, 0xFFE52B32);
        drawRect(x + 21, y + 10, x + 25, y + 16, 0xFFE52B32);
    }

    private void drawMods(int mouseX, int mouseY, int right, int bottom) {
        int sideX = panelLeft + 10, sideTop = panelTop + 60, contentX = panelLeft + SIDEBAR_WIDTH + 18;
        drawRect(sideX, sideTop, panelLeft + SIDEBAR_WIDTH, bottom - 27, 0xFF1A1E24);
        drawString(fontRendererObj, "CATEGORIAS", sideX + 9, sideTop + 10, 0xFFFF925C);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = sideTop + 29 + i * 25;
            boolean active = selectedCategory.equals(CATEGORIES[i]);
            boolean hovered = inside(mouseX, mouseY, sideX + 4, y - 4, panelLeft + SIDEBAR_WIDTH - 4, y + 17);
            if (active) {
                drawRect(sideX + 4, y - 4, panelLeft + SIDEBAR_WIDTH - 4, y + 17, 0xFF50151B);
                drawRect(sideX + 4, y - 4, sideX + 7, y + 17, 0xFFE32636);
            } else if (hovered) drawRect(sideX + 4, y - 4, panelLeft + SIDEBAR_WIDTH - 4, y + 17, 0xFF30191D);
            drawString(fontRendererObj, CATEGORIES[i], sideX + 13, y + 2, active ? 0xFFFF8585 : 0xFFB7A69D);
        }
        drawString(fontRendererObj, "MODS", contentX, panelTop + 62, 0xFFFF4D4D);
        drawString(fontRendererObj, "Clique ativa  /  engrenagem configura", contentX + 43, panelTop + 62, 0xFF95837A);
        int searchX = right - 151, searchY = panelTop + 57;
        drawRect(searchX - 1, searchY - 1, right - 13, searchY + 19, 0xFF332020);
        drawRect(searchX, searchY, right - 14, searchY + 18, 0xFF100D0D);
        drawString(fontRendererObj, "\u2315", searchX + 5, searchY + 5, 0xFFFF4D4D);
        searchField.drawTextBox();
        if (searchField.getText().isEmpty() && !searchField.isFocused())
            drawString(fontRendererObj, "Pesquisar mods", searchX + 19, searchY + 5, 0xFF8E7D76);
        drawRect(contentX, panelTop + 78, right - 13, bottom - 28, 0xFF1A1E24);
        drawRect(contentX, panelTop + 78, right - 13, panelTop + 79, 0xFF303640);
        List<ClientModule> modules = getFilteredModules();
        if (modules.isEmpty()) drawCenteredString(fontRendererObj, "NENHUM MODULO NESTA CATEGORIA",
                contentX + (right - contentX) / 2, panelTop + 120, 0xFFB7A69D);
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int index = scroll + row;
            if (index >= modules.size()) break;
            ClientModule module = modules.get(index);
            int y = panelTop + 85 + row * ROW_HEIGHT;
            boolean hovered = inside(mouseX, mouseY, contentX + 5, y - 3, right - 18, y + 19);
            if (hovered) drawRect(contentX + 5, y - 3, right - 18, y + 19, 0xFF29242A);
            String category = module instanceof CatalogModule ? ((CatalogModule) module).getCategory().toUpperCase()
                    : module instanceof FeatureModule ? ((FeatureModule) module).getCategory() : "HUD";
            drawString(fontRendererObj, module.getName(), contentX + 12, y + 2, 0xFFF0E5DE);
            drawString(fontRendererObj, category, contentX + 145, y + 2, 0xFF9D8980);
            if (module instanceof CatalogModule) drawString(fontRendererObj, "EM BREVE", right - 112, y + 2, 0xFFB58B73);
            else {
                boolean enabled = module.isEnabled();
                drawRect(right - 75, y - 1, right - 43, y + 14, enabled ? 0xFF9B3029 : 0xFF343A43);
                drawString(fontRendererObj, enabled ? "ON" : "OFF", right - 68, y + 3, enabled ? 0xFFFFB078 : 0xFFB5BEC8);
            }
            drawGear(right - 31, y + 3, 0xFFFF4D4D);
        }
        drawString(fontRendererObj, modules.size() + " MODULOS  /  SCROLL PARA NAVEGAR", contentX + 2, bottom - 25, 0xFF9C887F);
    }

    private List<ClientModule> getFilteredModules() {
        List<ClientModule> result = new ArrayList<ClientModule>();
        for (ClientModule module : FiredClient.MODULES.getModules()) {
            String category = module instanceof CatalogModule ? ((CatalogModule) module).getCategory().toUpperCase()
                    : module instanceof FeatureModule ? ((FeatureModule) module).getCategory().toUpperCase() : "HUD";
            boolean categoryMatch = "TODOS".equals(selectedCategory) || selectedCategory.equals(category);
            boolean searchMatch = searchField == null || module.getName().toLowerCase()
                    .contains(searchField.getText().toLowerCase());
            if (categoryMatch && searchMatch) result.add(module);
        }
        return result;
    }

    private void drawCosmetics(int mouseX, int mouseY, int right, int bottom) {
        int x0 = panelLeft + 16;
        drawString(fontRendererObj, "PERSONALIZACAO", x0, panelTop + 62, 0xFFFF4D4D);
        drawString(fontRendererObj, "Cosmeticos FIRED e colecoes de parceiros", x0 + 112, panelTop + 62, 0xFF95837A);
        for (int i = 0; i < COSMETIC_SECTIONS.length; i++) {
            int x = x0 + (i % 3) * 164, y = panelTop + 86 + (i / 3) * 75;
            boolean active = selectedCosmeticSection == i, hovered = inside(mouseX, mouseY, x, y, x + 151, y + 61);
            drawRect(x, y, x + 151, y + 61, active ? 0xFF50151B : hovered ? 0xFF30211C : 0xFF211815);
            if (active) drawRect(x, y + 57, x + 151, y + 61, 0xFFE32636);
            drawRect(x + 10, y + 10, x + 39, y + 39, 0xFF8F1822);
            drawCenteredString(fontRendererObj, String.valueOf(i + 1), x + 24, y + 21, 0xFFFF8585);
            drawString(fontRendererObj, COSMETIC_SECTIONS[i], x + 48, y + 13, active ? 0xFFFFB0B0 : 0xFFE6D8D0);
            ClientModule effect = i == 3 ? FiredClient.MODULES.getByName("Trails")
                    : i == 4 ? FiredClient.MODULES.getByName("Aura") : null;
            String status = effect == null ? "EM BREVE" : effect.isEnabled() ? "ATIVO" : "CLIQUE PARA ATIVAR";
            int statusColor = effect == null ? 0xFFAA8C7D : effect.isEnabled() ? 0xFFFF5555 : 0xFFE0A0A0;
            drawString(fontRendererObj, status, x + 48, y + 29, statusColor);
        }
        drawRect(x0, panelTop + 246, right - 16, bottom - 28, 0xFF1A1E24);
        drawString(fontRendererObj, "COLECAO FIRED", x0 + 12, panelTop + 255, 0xFFFF4D4D);
        String detail = selectedCosmeticSection == 3 ? "Trail de fogo: particulas acompanham seus passos."
                : selectedCosmeticSection == 4 ? "Aura de fogo: particulas giram ao redor do seu personagem."
                : "Este cosmético ficará bloqueado até ser liberado pela equipe FIRED.";
        drawString(fontRendererObj, detail, x0 + 12, panelTop + 270, 0xFFB7A69D);
        drawString(fontRendererObj, "COMPATIBILIDADE: " + OptiFineCompatibility.getStatus(), x0 + 12, panelTop + 285, 0xFFFFA04A);
    }

    private void drawSettings(int right) {
        int x = right - 190, y = panelTop + 58;
        drawRect(x, y, right - 12, panelTop + 190, 0xFF20242B);
        drawRect(x, y, right - 12, y + 3, 0xFFFF573D);
        drawString(fontRendererObj, settingsModule.getName().toUpperCase(), x + 10, y + 12, 0xFFFF4D4D);
        drawString(fontRendererObj, "CONFIGURACOES", x + 10, y + 26, 0xFFB7A69D);
        if (settingsModule instanceof FpsModule) {
            drawOption(x + 10, y + 51, "Mostrar FPS", FpsModule.showFps);
            drawOption(x + 10, y + 72, "Background", FpsModule.showBackground);
        } else drawString(fontRendererObj, "Sem opcoes adicionais", x + 10, y + 55, 0xFFB7A69D);
        drawString(fontRendererObj, "[ FECHAR ]", x + 10, y + 108, 0xFFFF4D4D);
    }


    private void drawGear(int x, int y, int color) {
        // Pequena engrenagem pixel-art para abrir as configuracoes do modulo.
        drawRect(x + 3, y, x + 7, y + 10, color);
        drawRect(x, y + 3, x + 10, y + 7, color);
        drawRect(x + 1, y + 1, x + 3, y + 3, color);
        drawRect(x + 7, y + 1, x + 9, y + 3, color);
        drawRect(x + 1, y + 7, x + 3, y + 9, color);
        drawRect(x + 7, y + 7, x + 9, y + 9, color);
        drawRect(x + 4, y + 4, x + 6, y + 6, 0xFF191413);
    }

    private void drawOption(int x, int y, String label, boolean enabled) {
        drawString(fontRendererObj, label, x, y, 0xFFEEDFD5);
        drawString(fontRendererObj, enabled ? "ON" : "OFF", x + 132, y, enabled ? 0xFFFF7A1A : 0xFF9B8A80);
    }

    private boolean inside(int mx, int my, int l, int t, int r, int b) { return mx >= l && mx <= r && my >= t && my <= b; }

    @Override protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField != null && searchField.isFocused()) {
            if (keyCode == 1) searchField.setFocused(false);
            else {
                searchField.textboxKeyTyped(typedChar, keyCode);
                scroll = 0;
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mouseX = (int) ((mouseX - guiOffsetX) / guiScale);
        mouseY = (int) ((mouseY - guiOffsetY) / guiScale);
        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            int right = panelLeft + PANEL_WIDTH;
            if (inside(mouseX, mouseY, panelLeft + 278, panelTop + 13, panelLeft + 348, panelTop + 37)) {
                tab = TAB_MODS; settingsModule = null;
            } else if (inside(mouseX, mouseY, panelLeft + 352, panelTop + 13, panelLeft + 454, panelTop + 37)) {
                tab = TAB_COSMETICS; settingsModule = null;
            } else if (tab == TAB_MODS && settingsModule != null) {
                int x = right - 190, y = panelTop + 58;
                if (inside(mouseX, mouseY, x + 5, y + 100, right - 12, y + 127)) settingsModule = null;
                else if (settingsModule instanceof FpsModule) {
                    if (inside(mouseX, mouseY, x + 5, y + 45, right - 12, y + 68)) FpsModule.showFps = !FpsModule.showFps;
                    else if (inside(mouseX, mouseY, x + 5, y + 67, right - 12, y + 90)) FpsModule.showBackground = !FpsModule.showBackground;
                }
            } else if (tab == TAB_MODS) {
                int sx = panelLeft + 10, st = panelTop + 60;
                for (int i = 0; i < CATEGORIES.length; i++) {
                    int y = st + 29 + i * 25;
                    if (inside(mouseX, mouseY, sx + 4, y - 4, panelLeft + SIDEBAR_WIDTH - 4, y + 17)) {
                        selectedCategory = CATEGORIES[i]; scroll = 0; break;
                    }
                }
                List<ClientModule> modules = getFilteredModules();
                int cx = panelLeft + SIDEBAR_WIDTH + 18;
                for (int row = 0; row < VISIBLE_ROWS; row++) {
                    int index = scroll + row;
                    if (index >= modules.size()) break;
                    int y = panelTop + 85 + row * ROW_HEIGHT;
                    ClientModule module = modules.get(index);
                    if (inside(mouseX, mouseY, right - 43, y - 3, right - 15, y + 19)) { settingsModule = module; break; }
                    if (inside(mouseX, mouseY, cx + 5, y - 3, right - 48, y + 19) && !(module instanceof CatalogModule)) {
                        module.toggle(); break;
                    }
                }
            } else if (tab == TAB_COSMETICS) {
                int x0 = panelLeft + 16;
                for (int i = 0; i < COSMETIC_SECTIONS.length; i++) {
                    int x = x0 + (i % 3) * 164, y = panelTop + 86 + (i / 3) * 75;
                    if (inside(mouseX, mouseY, x, y, x + 151, y + 61)) {
                        selectedCosmeticSection = i;
                        ClientModule effect = i == 3 ? FiredClient.MODULES.getByName("Trails")
                                : i == 4 ? FiredClient.MODULES.getByName("Aura") : null;
                        if (effect != null) effect.toggle();
                        break;
                    }
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override public boolean doesGuiPauseGame() { return false; }
}
