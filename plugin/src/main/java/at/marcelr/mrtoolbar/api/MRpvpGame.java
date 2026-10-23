package at.marcelr.mrtoolbar.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Asks MRpvp whether a player is in a duel, the arena editor or watching ({@code at.marcelr.mrpvp.api.MRpvpApi
 * .inGame(UUID)}), found at runtime - no dependency in either direction. MRpvp saves, clears and gives back the
 * whole inventory then, so the toolbar must keep its hands off it. Without MRpvp nobody is in a game. The method is
 * looked up once per MRpvp instance, so a reload of MRpvp is picked up.
 */
public final class MRpvpGame {

    private static final String API_CLASS = "at.marcelr.mrpvp.api.MRpvpApi";

    private final Logger log;
    private Plugin resolvedFor;
    private MethodHandle inGame;

    public MRpvpGame(Logger log) {
        this.log = log;
    }

    public boolean active(Player player) {
        Plugin mrpvp = Bukkit.getPluginManager().getPlugin("MRpvp");
        if (mrpvp == null || !mrpvp.isEnabled()) {
            return false;
        }
        if (mrpvp != resolvedFor) {
            resolvedFor = mrpvp;
            inGame = resolve(mrpvp);
        }
        if (inGame == null) {
            return false;
        }
        try {
            return (boolean) inGame.invokeExact(player.getUniqueId());
        } catch (Throwable e) {
            log.warning("MRpvp game check failed, ignoring it from now on: " + e);
            inGame = null;
            return false;
        }
    }

    private MethodHandle resolve(Plugin mrpvp) {
        try {
            Class<?> api = Class.forName(API_CLASS, true, mrpvp.getClass().getClassLoader());
            return MethodHandles.publicLookup().findStatic(api, "inGame",
                    MethodType.methodType(boolean.class, UUID.class));
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
