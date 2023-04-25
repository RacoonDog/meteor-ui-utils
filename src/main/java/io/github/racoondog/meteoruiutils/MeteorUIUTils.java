package io.github.racoondog.meteoruiutils;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class MeteorUIUTils extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    private static final List<Packet<?>> delayedPackets = new ArrayList<>();
    private boolean cancelNextSignPacket = false;
    private boolean shouldPreventPackets = false;
    private boolean shouldDelayPackets = false;
    private Screen storedScreen = null;
    private ScreenHandler storedScreenHandler = null;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onInitialize() {
        MeteorClient.EVENT_BUS.subscribe(this);

        OverlayContainer.hookWindow(SleepingChatScreen.class, "Sleeping Chat", (theme, section, screen) -> {
            WButton button = section.add(theme.button("Client Wake Up")).expandX().widget();
            button.action = () -> {
                mc.player.wakeUp();
                mc.setScreen(null);
            };
        });

        OverlayContainer.hookWindow(SignEditScreen.class, "Sign Edit", (theme, section, screen) -> {
            WButton button = section.add(theme.button("Clientside Close")).expandX().widget();
            button.action = () -> {
                mc.setScreen(null);
                cancelNextSignPacket = true;
            };
        });

        List<Pair<Class<? extends Screen>, String>> screens = List.of( //todo link lectern
            new ObjectObjectImmutablePair<>(HandledScreen.class, "HandledScreen"),
            new ObjectObjectImmutablePair<>(LecternScreen.class, "Lectern")
        );

        OverlayContainer.hookWindows(screens, (theme, section, screen) -> {
            WButton clientSideClose = section.add(theme.button("Clientside Close")).expandX().widget();
            clientSideClose.action = () -> mc.setScreen(null);

            WButton serverSideClose = section.add(theme.button("Serverside Close")).expandX().widget();
            serverSideClose.action = () -> {
                mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
            };

            WButton sendPackets = section.add(theme.button("Prevent Packets")).expandX().widget();
            sendPackets.action = () -> shouldPreventPackets = !shouldPreventPackets;

            WButton delayPackets = section.add(theme.button("Delay Packets")).expandX().widget();
            delayPackets.action = () -> {
                if (!shouldDelayPackets && !delayedPackets.isEmpty()) {
                    for (var packet : delayedPackets) {
                        mc.getNetworkHandler().sendPacket(packet);
                    }

                    delayedPackets.clear();
                }
            };

            WButton disconnect = section.add(theme.button("Disconnect")).expandX().widget();
            disconnect.action = () -> {
                if (!delayedPackets.isEmpty()) {
                    shouldDelayPackets = false;

                    for (var packet : delayedPackets) mc.getNetworkHandler().sendPacket(packet);
                    mc.getNetworkHandler().getConnection().disconnect(Text.literal("Disconnected [%s packets sent]".formatted(delayedPackets.size())));
                    delayedPackets.clear();
                }
            };

            //todo disable load when no saved state
            WButton save = section.add(theme.button("Save")).expandX().widget();
            save.action = () -> {
                storedScreen = screen;
                storedScreenHandler = mc.player.currentScreenHandler;
            };

            WButton load = section.add(theme.button("Load")).expandX().widget();
            load.action = () -> {
                if (storedScreen != null && storedScreenHandler != null) {
                    mc.setScreen(storedScreen);
                    mc.player.currentScreenHandler = storedScreenHandler;
                }
            };

            section.add(theme.label("Sync ID: " + mc.player.currentScreenHandler.syncId));
            WButton copySyncId = section.add(theme.button("Copy Sync ID")).expandX().widget();
            copySyncId.action = () -> {
                mc.keyboard.setClipboard(String.valueOf(mc.player.currentScreenHandler.syncId));
            };

            section.add(theme.label("Revision: " + mc.player.currentScreenHandler.getRevision()));
            WButton copyRevision = section.add(theme.button("Copy Revision")).expandX().widget();
            copyRevision.action = () -> {
                mc.keyboard.setClipboard(String.valueOf(mc.player.currentScreenHandler.getRevision()));
            };

            WButton fabricate = section.add(theme.button("Fabricate Packet")).expandX().widget();
            fabricate.action = () -> {
                //todo make
            };
        });
    }

    @EventHandler
    private void onPacket(PacketEvent.Send send) {
        if (cancelNextSignPacket && send.packet instanceof UpdateSignC2SPacket) {
            cancelNextSignPacket = false;
            send.cancel();
        } else if (shouldPreventPackets && (send.packet instanceof ClickSlotC2SPacket || send.packet instanceof ButtonClickC2SPacket)) send.cancel();
        else if (shouldDelayPackets && (send.packet instanceof ClickSlotC2SPacket || send.packet instanceof ButtonClickC2SPacket)) {
            delayedPackets.add(send.packet);
            send.cancel();
        }
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
