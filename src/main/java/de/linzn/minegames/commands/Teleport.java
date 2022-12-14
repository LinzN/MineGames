package de.linzn.minegames.commands;

import de.linzn.minegames.MessageManager;
import de.linzn.minegames.MessageManager.PrefixType;
import de.linzn.minegames.SettingsManager;
import org.bukkit.entity.Player;

public class Teleport implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        if (player.hasPermission(permission())) {
            if (args.length == 1) {
                try {
                    int a = Integer.parseInt(args[0]);
                    try {
                        player.teleport(SettingsManager.getInstance().getSpawnPoint(a, 1));
                    } catch (Exception e) {
                        MessageManager.getInstance().sendMessage(MessageManager.PrefixType.ERROR, "error.nospawns", player);
                    }
                } catch (NumberFormatException e) {
                    MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notanumber", player, "input-" + args[0]);
                }
            } else {
                MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notspecified", player, "input-Game ID");
            }
        } else {
            MessageManager.getInstance().sendFMessage(PrefixType.WARNING, "error.nopermission", player);
        }
        return true;
    }

    public String help(Player p) {
        return "/mg tp <arenaid> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.teleport", "Teleport to an arena");
    }

    public String permission() {
        return "mg.staff.teleport";
    }

}
