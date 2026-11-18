package br.com.firedclient.module;

import br.com.firedclient.module.impl.FpsModule;
import br.com.firedclient.module.impl.FeatureModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private final List<ClientModule> modules = new ArrayList<ClientModule>();

    public void registerDefaults() {
        register(new FpsModule());
        register(new FeatureModule("Coordinates", "HUD"));
        register(new FeatureModule("Ping", "HUD"));
        register(new FeatureModule("Clock", "HUD"));
        register(new FeatureModule("Server Address", "HUD"));
        register(new FeatureModule("Memory", "HUD"));
        register(new FeatureModule("Saturation", "HUD"));
        register(new FeatureModule("Armor Status", "HUD"));
        register(new FeatureModule("Potion Effects", "HUD"));
        register(new FeatureModule("Toggle Sprint", "MOVEMENT"));
        register(new FeatureModule("Fullbright", "RENDER"));
        register(new FeatureModule("Aura", "COSMETICS"));
        register(new FeatureModule("Trails", "COSMETICS"));
        register(new FeatureModule("Nametags", "RENDER"));

        register(new FeatureModule("Keystrokes", "HUD"));
        register(new FeatureModule("Stopwatch", "HUD"));
        register(new FeatureModule("Item Counter", "HUD"));
        register(new FeatureModule("Direction", "HUD"));
        register(new FeatureModule("Biome", "HUD"));
        register(new FeatureModule("Speed", "HUD"));
        register(new FeatureModule("Durability", "HUD"));
        register(new FeatureModule("Entity Counter", "HUD"));
        register(new FeatureModule("Player Counter", "HUD"));
        register(new FeatureModule("Health", "HUD"));
        register(new FeatureModule("Food Level", "HUD"));
        register(new FeatureModule("World Time", "HUD"));
        register(new FeatureModule("Weather", "HUD"));
        register(new FeatureModule("Light Level", "HUD"));

        String[][] catalog = {
            {"Reach Display","HUD"},
            {"Combo Counter","Combat"},
            {"Target HUD","Combat"},
            {"Low Fire","Render"},
            {"Hitboxes","Combat"},
            {"Hit Sounds","Combat"},
            {"Combat FPS","HUD"},
            {"Hit Bubbles","Combat"},
            {"Hit Marker","Combat"},
            {"Damage Numbers","Combat"},
            {"Health Indicators","Combat"},
            {"Zoom","Render"},
            {"Weather Changer","World"},
            {"Particles","Render"},
            {"Freelook","Render"},
            {"Waypoints","World"},
            {"Item Physics","Render"},
            {"Performance Panel","HUD"},
            {"Perspective","Render"},
            {"Clear Water","Render"},
            {"Kill Effects","Cosmetics"},
            {"Target ESP","Render"},
            {"Trajectories","Render"},
            {"Ambience","World"},
            {"Title Tweaks","HUD"},
            {"Bossbar Tweaks","HUD"},
            {"Scoreboard Tweaks","HUD"},
            {"Dynamic Island","HUD"},
            {"Client","Misc"},
            {"Theme","Cosmetics"},
            {"HUD Style","HUD"},
            {"CPS Counter","HUD"},
            {"Block Overlay","Render"},
            {"Crosshair","Render"},
            {"Reach Display Plus","HUD"},
            {"Auto Walk","Movement"},
            {"Auto Tool","Misc"},
            {"Item Alerts","HUD"}
        };
        for (String[] entry : catalog) register(new CatalogModule(entry[0], entry[1]));
    }

    public void register(ClientModule module) { modules.add(module); }
    public List<ClientModule> getModules() { return Collections.unmodifiableList(modules); }

    public ClientModule getByName(String name) {
        for (ClientModule module : modules)
            if (module.getName().equalsIgnoreCase(name)) return module;
        return null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (ClientModule module : modules)
            if (module.isEnabled() && module instanceof TickModule)
                ((TickModule) module).onTick();
    }

    public void renderHud(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        for (ClientModule module : modules)
            if (module.isEnabled() && module instanceof HudModule)
                ((HudModule) module).render(event);
    }
}
