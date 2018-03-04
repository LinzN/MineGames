package de.linzn.minegames.events;

import de.linzn.minegames.GameManager;
import de.linzn.minegames.MessageManager;
import de.linzn.minegames.SettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class CommandCatch implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0];

        //if (!GameManager.getInstance().isPlayerActive(event.getPlayer()) && !GameManager.getInstance().isPlayerInactive(event.getPlayer()) && !GameManager.getInstance().isSpectator(event.getPlayer()))
        //    return;
        if (!GameManager.getInstance().isPlayerActive(event.getPlayer()) && !GameManager.getInstance().isSpectator(event.getPlayer()))
            return;
        if (command.equalsIgnoreCase("/list")) {
            event.getPlayer().sendMessage(
                    GameManager.getInstance().getStringList(
                            GameManager.getInstance().getPlayerGameId(event.getPlayer())));
            return;
        }
        if (!SettingsManager.getInstance().getConfig().getBoolean("disallow-commands")) {
            return;
        }
        if (command.equalsIgnoreCase("/mg") || command.equalsIgnoreCase("/minegames") || command.equalsIgnoreCase("/sg") || command.equalsIgnoreCase("/hg") || command.equalsIgnoreCase("/hungergames") || command.equalsIgnoreCase("/tell") || command.equalsIgnoreCase("/r") || command.equalsIgnoreCase("/msg") || command.equalsIgnoreCase("/g") || command.equalsIgnoreCase("/tc") || command.equalsIgnoreCase("/gc") || command.equalsIgnoreCase("/h")) {
            return;
        } else if (SettingsManager.getInstance().getConfig().getStringList("cmdwhitelist").contains(command)) {
            return;
        }
        MessageManager.getInstance().sendMessage(MessageManager.PrefixType.INFO, ChatColor.RED + "Dies ist leider in der Arena nicht möglich!", event.getPlayer());
        event.setCancelled(true);
    }
}
