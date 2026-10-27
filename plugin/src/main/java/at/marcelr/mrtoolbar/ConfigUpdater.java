package at.marcelr.mrtoolbar;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;

/**
 * Brings an existing config.yml up to the layout of the bundled one: the bundled file (with its comments and order)
 * is taken as template and every value the server owner had set is carried over. The old file is kept as backup.
 * <p>
 * Files are always written as UTF-8 <b>with BOM</b>, so editors like Notepad++, WinSCP or FileZilla
 * recognise the encoding and show umlauts correctly.
 */
public final class ConfigUpdater {

    public static final int CURRENT_VERSION = 11;
    public static final String BOM = "\uFEFF";
    /**
     * Keys whose value is replaced by the new default, with the config-version that introduced the change: only files
     * older than that version are affected - a later update must not overwrite the value again.
     */
    private static final Map<String, Integer> REPLACED_KEYS = Map.of(
            // v6: "auto" (language of each player) replaces the fixed language of the test versions
            "language", 6);
    /** Defaults that changed: whoever still has the old default gets the new one, changed values are kept. */
    private static final Map<String, Object> OLD_DEFAULTS = Map.of(
            // v10: the mod is downloaded from the website instead of GitHub Releases
            "mod.download-url", "https://github.com/therealmarci/mrtoolbar/releases");
    /** Keys that were renamed: old path -> new path (the value is carried over). */
    private static final Map<String, String> RENAMED_KEYS = Map.of(
            // (noch keine)
            );

    private ConfigUpdater() {
    }

    public static void update(MRToolbar plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveDefaultConfig();
            return;
        }
        try {
            String current = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            YamlConfiguration existing = new YamlConfiguration();
            existing.loadFromString(stripBom(current));
            int version = existing.getInt("config-version", 1);
            if (version >= CURRENT_VERSION) {
                if (!current.startsWith(BOM)) {
                    // Same content, only add the BOM so editors detect UTF-8.
                    Files.writeString(file.toPath(), BOM + current, StandardCharsets.UTF_8);
                }
                return;
            }

            YamlConfiguration template = new YamlConfiguration();
            try (InputStream in = plugin.getResource("config.yml")) {
                if (in == null) {
                    return;
                }
                template.loadFromString(stripBom(new String(in.readAllBytes(), StandardCharsets.UTF_8)));
            }

            int carried = 0;
            for (String oldKey : existing.getKeys(true)) {
                String key = RENAMED_KEYS.getOrDefault(oldKey, oldKey);
                if (existing.isConfigurationSection(oldKey) || key.equals("config-version")
                        || version < REPLACED_KEYS.getOrDefault(key, 0) || !template.contains(key)
                        || existing.get(oldKey).equals(OLD_DEFAULTS.get(key))) {
                    continue;
                }
                template.set(key, existing.get(oldKey));
                carried++;
            }
            // v11: every player gets the language of their game; a fixed language so far stays for Discord & co.
            if (version < 11) {
                String old = existing.getString("language", "auto").trim();
                if (!old.isEmpty() && !old.equalsIgnoreCase("auto")) {
                    template.set("server-language", old);
                }
                template.set("language", "auto");
            }
            template.set("config-version", CURRENT_VERSION);
            // Keep long messages on one line instead of folding them.
            template.options().width(10_000);

            File backup = new File(plugin.getDataFolder(), "config.v" + version + ".backup.yml");
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.writeString(file.toPath(), BOM + template.saveToString(), StandardCharsets.UTF_8);
            plugin.getLogger().info("Updated config.yml from version " + version + " to " + CURRENT_VERSION + " ("
                    + carried + " settings carried over, backup: " + backup.getName() + ").");
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, "Could not update config.yml: " + e.getMessage());
        }
    }

    /** Loads config.yml (tolerating a BOM) with the bundled config as defaults for missing keys. */
    public static YamlConfiguration load(MRToolbar plugin) {
        YamlConfiguration config = new YamlConfiguration();
        File file = new File(plugin.getDataFolder(), "config.yml");
        try {
            if (file.exists()) {
                config.loadFromString(stripBom(Files.readString(file.toPath(), StandardCharsets.UTF_8)));
            }
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "config.yml is invalid, using defaults: " + e.getMessage());
        }
        try (InputStream in = plugin.getResource("config.yml")) {
            if (in != null) {
                YamlConfiguration defaults = new YamlConfiguration();
                defaults.loadFromString(stripBom(new String(in.readAllBytes(), StandardCharsets.UTF_8)));
                config.setDefaults(defaults);
                // Fill in values missing from the file. Bukkit ignores the defaults whenever code passes its own
                // fallback (getString(path, "...")), so missing keys would otherwise get those fallbacks.
                for (String key : defaults.getKeys(true)) {
                    if (!defaults.isConfigurationSection(key) && !config.isSet(key)) {
                        config.set(key, defaults.get(key));
                    }
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, "Could not read the bundled config: " + e.getMessage());
        }
        return config;
    }

    public static String stripBom(String text) {
        return text.startsWith(BOM) ? text.substring(1) : text;
    }
}
