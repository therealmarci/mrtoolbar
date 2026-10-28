package at.marcelr.mrtoolbar.command;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.Messages;
import at.marcelr.mrtoolbar.ModBridge;
import at.marcelr.mrtoolbar.PlayerToolbar;
import at.marcelr.mrtoolbar.ToolSlot;
import at.marcelr.mrtoolbar.ToolbarManager;
import at.marcelr.mrtoolbar.UpdateChecker;
import at.marcelr.mrtoolbar.gui.AdminMenu;
import at.marcelr.mrtoolbar.gui.BeltViewMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static at.marcelr.mrtoolbar.Messages.tag;

/** /mrtoolbar reload | version | debug [player] - administration. */
public final class MRToolbarCommand implements TabExecutor {

    private static final String PERM_RELOAD = "mrtoolbar.admin.reload";
    private static final String PERM_DEBUG = "mrtoolbar.admin.debug";

    private final MRToolbar plugin;

    public MRToolbarCommand(MRToolbar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label,
                             String @NonNull [] args) {
        // Every text of the command in the language of the sender.
        return plugin.messages().as(sender, () -> execute(sender, label, args));
    }

    private boolean execute(CommandSender sender, String label, String[] args) {
        Messages messages = plugin.messages();
        String sub = args.length == 0 ? "version" : args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "version", "ver" -> {
                // For players too (in mrtoolbar.player).
                if (sender.hasPermission("mrtoolbar.version")) {
                    sendVersion(sender);
                } else {
                    messages.send(sender, "no-permission");
                }
            }
            case "reload" -> {
                if (!sender.hasPermission(PERM_RELOAD)) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                plugin.reload();
                messages.send(sender, "reloaded");
                plugin.getLogger().info(sender.getName() + " reloaded the configuration.");
            }
            case "gui" -> {
                if (!(sender instanceof Player player)) {
                    messages.send(sender, "player-only");
                } else if (!sender.hasPermission(AdminMenu.PERM_GUI)) {
                    messages.send(sender, "no-permission");
                } else {
                    new AdminMenu(plugin, player).open();
                }
            }
            case "view" -> {
                Player target = args.length > 1 ? Bukkit.getPlayerExact(args[1]) : null;
                if (!(sender instanceof Player player)) {
                    messages.send(sender, "player-only");
                } else if (!sender.hasPermission(AdminMenu.PERM_VIEW)) {
                    messages.send(sender, "no-permission");
                } else if (target == null) {
                    messages.send(sender, "debug-no-player");
                } else {
                    new BeltViewMenu(plugin, player, target, null).open();
                }
            }
            case "debug" -> {
                if (!sender.hasPermission(PERM_DEBUG)) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                debug(sender, args);
            }
            default -> messages.send(sender, "admin-usage", tag("label", label));
        }
        return true;
    }

    /** Mod connection and tool slots of a player - to find out why something does not work. */
    private void debug(CommandSender sender, String[] args) {
        Messages messages = plugin.messages();
        Player target = args.length > 1 ? Bukkit.getPlayerExact(args[1]) : sender instanceof Player self ? self : null;
        if (target == null) {
            messages.send(sender, "debug-no-player");
            return;
        }
        ModBridge.Client client = plugin.modBridge().client(target);
        String mod = client == null
                ? messages.plain(plugin.modBridge().outdated(target) ? "debug-mod-outdated" : "debug-mod-none")
                : messages.plain("debug-mod", "version", client.modVersion(), "protocol", client.protocol(),
                        "status", messages.plain("debug-status-" + client.status().name().toLowerCase(Locale.ROOT).replace('_', '-')));
        ToolbarManager toolbars = plugin.toolbars();
        PlayerToolbar toolbar = toolbars.get(target);
        int size = toolbars.beltSize(target);
        List<String> slots = new ArrayList<>();
        for (ToolSlot slot : toolbar.slots()) {
            slots.add((slot.hotbar() + 1) + " (" + slot.count(size) + "/" + size + ")");
        }
        messages.send(sender, "debug-info", tag("player", target.getName()), tag("mod", mod),
                tag("active", messages.plain(toolbars.active(target) ? "debug-yes" : "debug-no")),
                tag("enabled", messages.plain(toolbar.enabled() ? "debug-yes" : "debug-no")),
                tag("slots", slots.isEmpty() ? "-" : String.join(", ", slots)));
    }

    private void sendVersion(CommandSender sender) {
        Messages messages = plugin.messages();
        UpdateChecker updates = plugin.updateChecker();
        messages.send(sender, "version",
                tag("version", updates.currentVersion()), tag("mc", updates.minecraftVersion()),
                tag("author", String.join(", ", plugin.getPluginMeta().getAuthors())));
        Runnable links = () -> messages.send(sender, "version-links",
                tag("website", link("Website", updates.websitePage())),
                tag("modrinth", link("Website", updates.websitePage())),
                tag("github", link("GitHub", updates.githubPage())));
        if (plugin.settings().update().enabled()) {
            updates.checkNow(sender, links);
        } else {
            updates.sendStatus(sender);
            links.run();
        }
    }

    private static Component link(String label, String url) {
        return Component.text(label, NamedTextColor.AQUA, TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl(url));
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label,
                                      String @NonNull [] args) {
        if (args.length == 2 && (args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("debug"))) {
            String start = args[1].toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(start)).toList();
        }
        if (args.length != 1) {
            return List.of();
        }
        List<String> options = new ArrayList<>(List.of("version"));
        if (sender.hasPermission(PERM_RELOAD)) {
            options.add("reload");
        }
        if (sender.hasPermission(PERM_DEBUG)) {
            options.add("debug");
        }
        if (sender.hasPermission(AdminMenu.PERM_GUI)) {
            options.add("gui");
        }
        if (sender.hasPermission(AdminMenu.PERM_VIEW)) {
            options.add("view");
        }
        return options.stream().filter(option -> option.startsWith(args[0].toLowerCase(Locale.ROOT))).toList();
    }
}
