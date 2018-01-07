package de.linzn.minegames.commands;

import de.linzn.minegames.GameManager;
import de.linzn.minegames.SettingsManager;
import org.bukkit.entity.Player;


public class LeaveQueue implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        GameManager.getInstance().removeFromOtherQueues(player, -1);
        return true;
    }

    public String help(Player p) {
        return "/sg lq - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.leavequeue", "Leave the queue for any queued games");
    }

    public String permission() {
        return null;
    }

}
