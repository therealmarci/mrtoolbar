package at.marcelr.mrtoolbar.command;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.Messages;
import at.marcelr.mrtoolbar.ToolbarManager;
import at.marcelr.mrtoolbar.gui.SortMenu;
import at.marcelr.mrtoolbar.gui.ToolbarMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;

/**
 * /toolbar [on|off|takeback|sort] - the player menu, switching the toolbar on/off, taking all tools back, or the
 * sorting exceptions.
 */
public final class ToolbarCommand implements TabExecutor {

    private final MRToolbar plugin;

    public ToolbarCommand(MRToolbar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label,
                             String @NonNull [] args) {
        // Every text of the command in the language of the sender.
        return plugin.messages().as(sender, () -> execute(sender, label, args));
    }

    private boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().send(sender, "player-only");
            return true;
        }
        if (args.length == 0) {
            ToolbarMenu.open(plugin, player);
            return true;
        }
        // Sorting has its own permission and works without the mod.
        if (args[0].equalsIgnoreCase("sort") || args[0].equalsIgnoreCase("sortieren")) {
            SortMenu.open(plugin, player);
            return true;
        }
        if (!player.hasPermission(ToolbarManager.PERM_USE)) {
            plugin.messages().send(player, "no-permission");
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "on", "an" -> toggle(player, true);
            case "off", "aus" -> toggle(player, false);
            case "takeback", "zurueck" -> {
                int returned = plugin.toolbars().takeBackAll(player);
                plugin.messages().send(player, "take-back-done", Messages.tag("count", returned));
            }
            default -> plugin.messages().send(player, "toolbar-usage", Messages.tag("label", label));
        }
        return true;
    }

    private void toggle(Player player, boolean enabled) {
        plugin.toolbars().setEnabled(player, enabled);
        plugin.messages().send(player, enabled ? "toolbar-enabled" : "toolbar-disabled");
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label,
                                      String @NonNull [] args) {
        if (args.length != 1) {
            return List.of();
        }
        return List.of("on", "off", "takeback", "sort").stream().filter(option -> option.startsWith(args[0].toLowerCase(Locale.ROOT))).toList();
    }
}
