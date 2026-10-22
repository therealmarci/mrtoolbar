package at.marcelr.mrtoolbar;

import at.marcelr.mrtoolbar.api.MRadminAdminMode;
import at.marcelr.mrtoolbar.command.MRToolbarCommand;
import at.marcelr.mrtoolbar.command.ToolbarCommand;
import at.marcelr.mrtoolbar.gui.MenuListener;
import at.marcelr.mrtoolbar.gui.BeltEditor;
import at.marcelr.mrtoolbar.gui.ProtectionListener;
import at.marcelr.mrtoolbar.sort.InventorySorter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class MRToolbar extends JavaPlugin {

    private FileConfiguration config;
    private Settings settings;
    /**
     * lang-version 4: toolbar only with the mod (help, belt-empty and usage texts changed); 5: /mrtoolbar debug;
     * 6: gui, view; 7: inventory sorting; 8: sorting containers.
     */
    private final Messages messages = new Messages(8, Map.of(
            4, Set.of("belt-empty", "gui-help-lore", "gui-slot-tool-lore", "gui-belt-info-name", "gui-belt-info-lore",
                    "toolbar-usage"),
            5, Set.of("admin-usage"),
            6, Set.of("admin-usage"),
            7, Set.of("toolbar-usage"),
            8, Set.of("gui-sort-help-lore")));
    private final ToolbarManager toolbars = new ToolbarManager(this);
    private final MRadminAdminMode adminMode = new MRadminAdminMode(getLogger());
    private final ModBridge modBridge = new ModBridge(this);
    private final InventorySorter sorter = new InventorySorter(this);
    private UpdateChecker updateChecker;
    private MenuListener menuListener;
    private BukkitTask guardTask;

    @Override
    public void onEnable() {
        ConfigUpdater.update(this);
        loadConfiguration();

        updateChecker = new UpdateChecker(this);
        updateChecker.start();
        menuListener = new MenuListener(this);
        menuListener.start();
        modBridge.start();

        getServer().getPluginManager().registerEvents(new ToolbarListener(this), this);
        getServer().getPluginManager().registerEvents(menuListener, this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(modBridge, this);
        register("toolbar", new ToolbarCommand(this));
        register("mrtoolbar", new MRToolbarCommand(this));

        // After /reload confirm the players are already online.
        Bukkit.getOnlinePlayers().forEach(toolbars::get);
        // Safety net: the tool slots are also checked after every inventory change (ProtectionListener).
        guardTask = Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            toolbars.guard(player);
            modBridge.update(player);
        }), 20L, 20L);
    }

    @Override
    public void onDisable() {
        if (guardTask != null) {
            guardTask.cancel();
        }
        // Open belt windows put their tools back first.
        BeltEditor.closeAll();
        if (menuListener != null) {
            menuListener.stop();
            menuListener.closeAll();
        }
        if (updateChecker != null) {
            updateChecker.stop();
        }
        modBridge.stop();
        Bukkit.getOnlinePlayers().forEach(toolbars::unload);
    }

    /** Reloads config.yml and the language file; tools that no longer fit go back to their owners. */
    public void reload() {
        BeltEditor.closeAll();
        ConfigUpdater.update(this);
        loadConfiguration();
        updateChecker.start();
        menuListener.start();
        Bukkit.getOnlinePlayers().forEach(toolbars::enforceLimits);
    }

    private void loadConfiguration() {
        config = ConfigUpdater.load(this);
        messages.load(this, config.getString("language", "auto"), config.getString("fallback-language", "en"));
        settings = Settings.from(config, getLogger());
    }

    /** Opens the belt of a tool slot (if the toolbar works for the player right now). */
    public void openBelt(Player player, ToolSlot slot) {
        if (toolbars.active(player)) {
            messages.run(player, () -> BeltEditor.open(this, player, slot));
        }
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            config = ConfigUpdater.load(this);
        }
        return config;
    }

    @Override
    public void reloadConfig() {
        config = ConfigUpdater.load(this);
    }

    private void register(String name, TabExecutor executor) {
        PluginCommand command = Objects.requireNonNull(getCommand(name), name);
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    public Settings settings() {
        return settings;
    }

    public Messages messages() {
        return messages;
    }

    public ToolbarManager toolbars() {
        return toolbars;
    }

    public UpdateChecker updateChecker() {
        return updateChecker;
    }

    public MRadminAdminMode adminMode() {
        return adminMode;
    }

    public ModBridge modBridge() {
        return modBridge;
    }

    public InventorySorter sorter() {
        return sorter;
    }
}
