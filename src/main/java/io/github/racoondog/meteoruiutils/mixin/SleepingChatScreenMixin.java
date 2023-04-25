package io.github.racoondog.meteoruiutils.mixin;

import io.github.racoondog.meteorsharedaddonutils.features.ScreenContainer;
import io.github.racoondog.meteoruiutils.utils.OverlayContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SleepingChatScreen.class)
public abstract class SleepingChatScreenMixin extends ChatScreen {
    @Unique private ScreenContainer container;

    private SleepingChatScreenMixin(String originalChatText) {
        super(originalChatText);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(CallbackInfo ci) {
        this.container = new OverlayContainer<>(this);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void injectInit(CallbackInfo ci) {
        container.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (container.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (container.mouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        container.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (container.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (container.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (container.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (container.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        container.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        container.resize(client, width, height);
        super.resize(client, width, height);
    }

    @Override
    public void removed() {
        container.removed();
        super.removed();
    }
}
