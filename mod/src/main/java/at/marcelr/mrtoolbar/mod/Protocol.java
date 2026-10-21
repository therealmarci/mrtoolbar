package at.marcelr.mrtoolbar.mod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Messages between mod and plugin - must match {@code ModBridge} and {@code Compatibility} in the plugin.
 * See docs/COMPATIBILITY.md.
 * <p>
 * {@code hello} and {@code welcome} never change their format (fields may only be added at the end), so every mod
 * version can find out whether it fits a plugin version. Everything else belongs to {@link #VERSION}.
 */
public final class Protocol {

    /** Protocol this mod speaks. Raise it (here and in the plugin) whenever state, action or design change. */
    public static final int VERSION = 2;

    private Protocol() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("mrtoolbar", path);
    }

    public enum Action {
        OPEN, NEXT, PREVIOUS
    }

    /** Result codes of {@link WelcomePayload} (never reused or reordered). */
    public enum Status {
        OK, MOD_TOO_OLD, MOD_TOO_NEW, UNKNOWN;

        static Status of(int code) {
            return code >= 0 && code < UNKNOWN.ordinal() ? values()[code] : UNKNOWN;
        }
    }

    /** Client to server on joining: VarInt protocol, String mod version, String Minecraft version. Frozen format. */
    public record HelloPayload(int protocol, String modVersion, String minecraftVersion) implements CustomPacketPayload {

        public static final Type<HelloPayload> TYPE = new Type<>(id("hello"));
        public static final StreamCodec<FriendlyByteBuf, HelloPayload> CODEC = StreamCodec.of(
                (buf, payload) -> {
                    buf.writeVarInt(payload.protocol);
                    buf.writeUtf(payload.modVersion);
                    buf.writeUtf(payload.minecraftVersion);
                },
                buf -> new HelloPayload(buf.readVarInt(), buf.readUtf(), buf.readUtf()));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Server's answer: VarInt status, VarInt min protocol, VarInt max protocol, String plugin version, String minimum
     * mod version, String download URL. Frozen format.
     */
    public record WelcomePayload(Status status, int minProtocol, int maxProtocol, String pluginVersion,
                                 String minimumModVersion, String downloadUrl) implements CustomPacketPayload {

        public static final Type<WelcomePayload> TYPE = new Type<>(id("welcome"));
        public static final StreamCodec<FriendlyByteBuf, WelcomePayload> CODEC = StreamCodec.of(
                (buf, payload) -> {
                    buf.writeVarInt(payload.status.ordinal());
                    buf.writeVarInt(payload.minProtocol);
                    buf.writeVarInt(payload.maxProtocol);
                    buf.writeUtf(payload.pluginVersion);
                    buf.writeUtf(payload.minimumModVersion);
                    buf.writeUtf(payload.downloadUrl);
                },
                buf -> {
                    WelcomePayload payload = new WelcomePayload(Status.of(buf.readVarInt()), buf.readVarInt(),
                            buf.readVarInt(), buf.readUtf(), buf.readUtf(), buf.readUtf());
                    // Fields added by newer plugins are skipped.
                    buf.skipBytes(buf.readableBytes());
                    return payload;
                });

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Which hotbar slots are tool slots: boolean active, short bits (bit i = hotbar slot i). */
    public record StatePayload(boolean active, int slots) implements CustomPacketPayload {

        public static final Type<StatePayload> TYPE = new Type<>(id("state"));
        public static final StreamCodec<FriendlyByteBuf, StatePayload> CODEC = StreamCodec.of(
                (buf, payload) -> {
                    buf.writeBoolean(payload.active);
                    buf.writeShort(payload.slots);
                },
                buf -> new StatePayload(buf.readBoolean(), buf.readUnsignedShort()));

        public boolean isToolSlot(int hotbar) {
            return active && hotbar >= 0 && hotbar < 9 && (slots & (1 << hotbar)) != 0;
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** A key was pressed: byte action - the server does the rest. */
    public record ActionPayload(Action action) implements CustomPacketPayload {

        public static final Type<ActionPayload> TYPE = new Type<>(id("action"));
        public static final StreamCodec<FriendlyByteBuf, ActionPayload> CODEC = StreamCodec.of(
                (buf, payload) -> buf.writeByte(payload.action.ordinal()),
                buf -> new ActionPayload(Action.values()[Math.min(buf.readUnsignedByte(), Action.values().length - 1)]));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** The server asks to open the hotbar design screen (button in /toolbar). No content. */
    public record DesignPayload() implements CustomPacketPayload {

        public static final Type<DesignPayload> TYPE = new Type<>(id("design"));
        public static final StreamCodec<FriendlyByteBuf, DesignPayload> CODEC = StreamCodec.of(
                (buf, payload) -> {
                },
                buf -> new DesignPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
