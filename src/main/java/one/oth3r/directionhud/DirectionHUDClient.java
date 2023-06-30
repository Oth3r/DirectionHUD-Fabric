package one.oth3r.directionhud;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DirectionHUDClient implements ClientModInitializer {
    public static boolean onSupportedServer = false;
    public static boolean hudState = false;
    private static KeyBinding keyBinding;
    @Override
    public void onInitializeClient() {
        DirectionHUD.isClient = true;
        DirectionHUD.initializeCommon();
        //CLIENT ONLY
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.directionhud.keybind.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.directionhud.all"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                assert client.player != null;
                if (client.isInSingleplayer() || onSupportedServer) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()); // Create a new PacketByteBuf
                    buf.writeString("/your_command_here"); // Write the command string to the buffer
                    client.player.networkHandler.sendPacket(new ChatMessageC2SPacket(buf));
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(PacketBuilder.INITIALIZATION_PACKET, (client, handler, buf, responseSender) -> {
            PacketBuilder packet = new PacketBuilder(buf);
            assert client.player != null;
            client.execute(() -> {
                DirectionHUD.LOGGER.info(packet.getMessage());
                onSupportedServer = true;
                PacketBuilder sPacket = new PacketBuilder("");
                sPacket.sendToServer(PacketBuilder.INITIALIZATION_PACKET);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(PacketBuilder.HUD_STATE, (client, handler, buf, responseSender) -> {
            PacketBuilder packet = new PacketBuilder(buf);
            assert client.player != null;
            client.execute(() -> hudState = Boolean.parseBoolean(packet.getMessage()));
        });
    }
}
