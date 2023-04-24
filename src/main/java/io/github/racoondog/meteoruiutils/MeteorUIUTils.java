package io.github.racoondog.meteoruiutils;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class MeteorUIUTils extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
    }

    @Override
    public String getPackage() {
        return "io.github.racoondog.meteoruiutils";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("RacoonDog", "meteor-ui-utils", "main");
    }
}
