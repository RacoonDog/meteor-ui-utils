package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModuleExample extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Boolean> exampleSetting = sgGeneral.add(new BoolSetting.Builder()
        .name("example-setting")
        .description("This is an example setting!")
        .defaultValue(true)
        .build()
    );

    public ModuleExample() {
        super(AddonTemplate.CATEGORY, "example", "An example module in a custom category.");
    }

    @EventHandler
    private void exampleTickEvent(TickEvent.Pre event) {
        if (exampleSetting.get()) {
            AddonTemplate.LOG.info("Ticking!");
        }
    }
}
