package de.linzn.minegames;

import de.linzn.minegames.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;


public class CommandHandler implements CommandExecutor {
    private Plugin plugin;
    private HashMap<String, SubCommand> commands;
    private HashMap<String, Integer> helpinfo;
    private MessageManager msgmgr = MessageManager.getInstance();

    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;
        commands = new HashMap<>();
        helpinfo = new HashMap<>();
        loadCommands();
        loadHelpInfo();
    }

    private void loadCommands() {
        commands.put("createarena", new CreateArena());
        commands.put("join", new Join());
        commands.put("addwall", new AddWall());
        commands.put("setspawn", new SetSpawn());
        commands.put("getcount", new ListArenas());
        commands.put("disable", new Disable());
        commands.put("start", new ForceStart());
        commands.put("enable", new Enable());
        commands.put("vote", new Vote());
        commands.put("leave", new Leave());
        commands.put("setlobbyspawn", new SetLobbySpawn());
        commands.put("setlobbywall", new SetLobbyWall());
        commands.put("resetspawns", new ResetSpawns());
        commands.put("delarena", new DelArena());
        commands.put("flag", new Flag());
        commands.put("spectate", new Spectate());
        commands.put("lq", new LeaveQueue());
        commands.put("leavequeue", new LeaveQueue());
        commands.put("list", new ListPlayers());
        commands.put("listarenas", new ListArenas());
        commands.put("tp", new Teleport());
        commands.put("reload", new Reload());
        commands.put("refill", new Refill());
//		commands.put("setstatswall", new SetStatsWall());
        commands.put("resetarena", new ResetArena());
//		commands.put("test", new Test());
    }

    private void loadHelpInfo() {
        helpinfo.put("join", 1);
        helpinfo.put("vote", 1);
        helpinfo.put("spectate", 1);
        helpinfo.put("lq", 1);
        helpinfo.put("leavequeue", 1);
        helpinfo.put("list", 1);
        helpinfo.put("listarenas", 1);
        helpinfo.put("leave", 1);
        helpinfo.put("disable", 2);
        helpinfo.put("start", 2);
        helpinfo.put("enable", 2);
        helpinfo.put("createarena", 3);
        helpinfo.put("addwall", 3);
        helpinfo.put("setspawn", 3);
        helpinfo.put("getcount", 3);
        helpinfo.put("setlobbyspawn", 3);
        helpinfo.put("resetspawns", 3);
        helpinfo.put("delarena", 3);
        helpinfo.put("flag", 3);
        helpinfo.put("reload", 3);
        helpinfo.put("refill", 3);
        helpinfo.put("resetarena", 3);
        //helpinfo.put("setstatswall", 1);
    }

    public boolean onCommand(CommandSender sender, Command cmd1, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            msgmgr.logMessage(MessageManager.PrefixType.WARNING, "Geht leider nur im Spiel.! ");
            return true;
        }

        Player player = (Player) sender;

        if (MineGames.config_todate == false) {
            msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Die Konfig ist fehlerhaft.", player);
            return true;
        }

        if (MineGames.dbcon == false) {
            msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Die Datenbank ist offline", player);
            return true;
        }

        if (cmd1.getName().equalsIgnoreCase("minegames")) {
            if (args == null || args.length < 1) {
                msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Gib /mg help <spieler | team | admin> ein für die Hilfeseiten", player);
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                if (args.length == 1) {
                    help(player, 1);
                } else {
                    if (args[1].toLowerCase().startsWith("spieler")) {
                        help(player, 1);
                        return true;
                    }
                    if (args[1].toLowerCase().startsWith("team")) {
                        help(player, 2);
                        return true;
                    }
                    if (args[1].toLowerCase().startsWith("admin")) {
                        help(player, 3);
                        return true;
                    } else {
                        msgmgr.sendMessage(MessageManager.PrefixType.WARNING, args[1] + " ist keine gültige Eingabe! Gültig ist Spieler, Team und Admin.", player);
                    }
                }
                return true;
            }
            String sub = args[0];
            Vector<String> l = new Vector<String>();
            l.addAll(Arrays.asList(args));
            l.remove(0);
            args = (String[]) l.toArray(new String[0]);
            if (!commands.containsKey(sub)) {
                msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Diesen Befehl gibt es nicht.", player);
                msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Gib /mg help ein für die Hilfeseiten", player);
                return true;
            }
            try {
                commands.get(sub).onCommand(player, args);
            } catch (Exception e) {
                e.printStackTrace();
                msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Gib /mg help ein für die Hilfeseiten", player);
            }
            return true;
        }
        return false;
    }

    public void help(Player p, int page) {
        if (page == 1) {
            p.sendMessage(ChatColor.DARK_GREEN + "-=========== [" + ChatColor.GOLD + ChatColor.BOLD + "MineGames Spieler" + ChatColor.RESET + ChatColor.DARK_GREEN + "] ===========-");
        }
        if (page == 2) {
            p.sendMessage(ChatColor.DARK_GREEN + "-=========== [" + ChatColor.GOLD + ChatColor.BOLD + "MineGames Team" + ChatColor.RESET + ChatColor.DARK_GREEN + "] ===========-");
        }
        if (page == 3) {
            p.sendMessage(ChatColor.DARK_GREEN + "-=========== [" + ChatColor.GOLD + ChatColor.BOLD + "MineGames Admin" + ChatColor.RESET + ChatColor.DARK_GREEN + "] ===========-");
        }

        for (String command : commands.keySet()) {
            try {
                if (helpinfo.get(command) == page) {

                    msgmgr.sendMessage(MessageManager.PrefixType.INFO, commands.get(command).help(p), p);
                }
            } catch (Exception e) {
            }
        }
		/*for (SubCommand v : commands.values()) {
            if (v.permission() != null) {
                if (p.hasPermission(v.permission())) {
                    msgmgr.sendMessage(PrefixType.INFO1, v.help(p), p);
                } else {
                    msgmgr.sendMessage(PrefixType.WARNING, v.help(p), p);
                }
            } else {
                msgmgr.sendMessage(PrefixType.INFO, v.help(p), p);
            }
        }*/
    }
}
