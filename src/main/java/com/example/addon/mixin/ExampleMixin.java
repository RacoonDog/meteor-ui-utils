package com.example.addon.mixin;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = MeteorClient.class, remap = false)
public abstract class ExampleMixin {
    @Inject(method = "onInitializeClient", at = @At("TAIL"))
    private void exampleInject(CallbackInfo ci) {
        AddonTemplate.LOG.info("Some silly addon forgot to delete the example mixin!!!");
    }
}
