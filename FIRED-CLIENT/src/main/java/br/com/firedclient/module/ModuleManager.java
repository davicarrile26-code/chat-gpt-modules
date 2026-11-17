package br.com.firedclient.module;

import br.com.firedclient.module.impl.FpsModule;
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
        String[][] catalog = {
            {"CPS","HUD"},{"Keystrokes","HUD"},{"Armor Status","HUD"},{"Potion Effects","HUD"},
            {"Coordinates","HUD"},{"Ping","HUD"},{"Clock","HUD"},{"Server Address","HUD"},
            {"Memory","HUD"},{"Saturation","HUD"},{"Toggle Sprint","Movement"},{"Hit Color","Combat"},
            {"Reach Display","HUD"},{"Combo Counter","Combat"},{"Target HUD","Combat"},{"Low Fire","Render"},
            {"Hitboxes","Combat"},{"Hit Sounds","Combat"},{"Combat FPS","HUD"},{"Hit Bubbles","Combat"},
            {"Hit Marker","Combat"},{"Damage Numbers","Combat"},{"Health Indicators","Combat"},{"Zoom","Render"},
            {"Fullbright","Render"},{"Crosshair","Render"},{"Block Outline","Render"},{"Time Changer","World"},
            {"Weather Changer","World"},{"Particles","Render"},{"Freelook","Render"},{"Cinematic Lyrics","Cosmetics"},
            {"Auto GG","Misc"},{"Nick Hider","Misc"},{"Address Hider","Misc"},{"Waypoints","World"},
            {"Item Physics","Render"},{"Friends","Misc"},{"Performance Panel","HUD"},{"Aspect Ratio","Render"},
            {"Viewmodel","Render"},{"Perspective","Render"},{"Hurt Cam","Render"},{"Clear Water","Render"},
            {"Color Saturation","Render"},{"Motion Blur","Render"},{"Kill Effects","Cosmetics"},{"Target ESP","Render"},
            {"Jump Circles","Cosmetics"},{"Trails","Cosmetics"},{"China Hat","Cosmetics"},{"Aura","Render"},
            {"Trajectories","Render"},{"Ambience","World"},{"Glint Color","Cosmetics"},{"Realistic Capes","Cosmetics"},
            {"Title Tweaks","HUD"},{"Bossbar Tweaks","HUD"},{"Scoreboard Tweaks","HUD"},{"Nametags","Render"},
            {"Friends Ingame","Misc"},{"Dynamic Island","HUD"},{"Client","Misc"},{"Theme","Cosmetics"},{"HUD Style","HUD"}
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
