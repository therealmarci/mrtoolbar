package at.marcelr.mrtoolbar.mod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * How tool slots are highlighted - per computer in config/mrtoolbar.json, so it can match the texture pack.
 * Fields are public for Gson; values are checked in {@link #clamp()}.
 */
public final class DesignConfig {

    public enum Style {
        /** Frame inside the slot. */
        FRAME,
        /** Only the four corners. */
        CORNERS,
        /** A bar at the bottom of the slot. */
        UNDERLINE,
        /** The slot background tinted. */
        FILL,
        /** The sprite mrtoolbar:hud/tool_slot - texture packs can replace it. */
        TEXTURE
    }

    /** What happens on the slot that is selected right now (it already has Minecraft's selection frame). */
    public enum Selected { SHOW, HIDE }

    public boolean enabled = true;
    public Style style = Style.FRAME;
    /** RGB. */
    public int color = 0xFFAA00;
    /** 10-100 %. */
    public int opacity = 100;
    /** 1-3 px. */
    public int thickness = 1;
    /** Distance from the slot edge, 0-4 px (0 = whole 20 px cell, 1 = inside the slot border, 2 = around the item). */
    public int inset = 1;
    public Selected selected = Selected.SHOW;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("mrtoolbar.json");
    }

    public static DesignConfig load() {
        Path file = file();
        if (Files.exists(file)) {
            try {
                DesignConfig config = GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), DesignConfig.class);
                if (config != null) {
                    return config.clamp();
                }
            } catch (IOException | JsonParseException e) {
                MRtoolbarClient.LOG.warn("config/mrtoolbar.json is invalid, using defaults: {}", e.getMessage());
            }
        }
        return new DesignConfig();
    }

    public void save() {
        try {
            Files.createDirectories(file().getParent());
            Files.writeString(file(), GSON.toJson(clamp()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            MRtoolbarClient.LOG.warn("Could not save config/mrtoolbar.json: {}", e.getMessage());
        }
    }

    public DesignConfig copy() {
        return GSON.fromJson(GSON.toJson(this), DesignConfig.class);
    }

    /** ARGB with the opacity applied. */
    public int argb() {
        return (Math.round(opacity * 2.55f) << 24) | (color & 0xFFFFFF);
    }

    DesignConfig clamp() {
        if (style == null) {
            style = Style.FRAME;
        }
        if (selected == null) {
            selected = Selected.SHOW;
        }
        color &= 0xFFFFFF;
        opacity = Math.clamp(opacity, 10, 100);
        thickness = Math.clamp(thickness, 1, 3);
        inset = Math.clamp(inset, 0, 4);
        return this;
    }
}
