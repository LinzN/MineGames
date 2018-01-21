package de.linzn.minegames.events;

import de.linzn.minegames.GameManager;
import de.linzn.minegames.SettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class CommandCatch implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String m = event.getMessage();

        if (!GameManager.getInstance().isPlayerActive(event.getPlayer()) && !GameManager.getInstance().isPlayerInactive(event.getPlayer()) && !GameManager.getInstance().isSpectator(event.getPlayer()))
            return;
        if (m.equalsIgnoreCase("/list")) {
            event.getPlayer().sendMessage(
                    GameManager.getInstance().getStringList(
                            GameManager.getInstance().getPlayerGameId(event.getPlayer())));
            return;
        }
        if (!SettingsManager.getInstance().getConfig().getBoolean("disallow-commands"))
            return;
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("mg.staff.nocmdblock"))
            return;
        else if (m.startsWith("/mg") || m.startsWith("/minegames") || m.startsWith("/sg") || m.startsWith("/hg") || m.startsWith("/hungergames") || m.startsWith("/tell") || m.startsWith("/r") || m.startsWith("/msg") || m.startsWith("/g") || m.startsWith("/h")) {
            return;
        } else if (SettingsManager.getInstance().getConfig().getStringList("cmdwhitelist").contains(m)) {
            return;
        }
        event.setCancelled(true);
    }
}
