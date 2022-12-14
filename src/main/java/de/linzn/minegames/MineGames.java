package de.linzn.minegames;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.linzn.minegames.events.*;
import de.linzn.minegames.hooks.HookManager;
import de.linzn.minegames.logging.LoggingManager;
import de.linzn.minegames.logging.QueueManager;
import de.linzn.minegames.stats.StatsManager;
import de.linzn.minegames.util.ChestRatioStorage;
import de.linzn.minegames.util.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MineGames extends JavaPlugin {
    public static MineGames plugin;
    public static Logger logger;
    public static boolean dbcon = false;
    public static boolean config_todate = false;
    public static int config_version = 3;
    private static File datafolder;
    private static boolean disabling = false;
    MineGames p = this;

    public static File getPluginDataFolder() {
        return datafolder;
    }

    public static boolean isDisabling() {
        return disabling;
    }

    public static void $(String msg) {
        logger.log(Level.INFO, msg);
    }

    public static void $(Level l, String msg) {
        logger.log(l, msg);
    }

    public static void debug(String msg) {
        if (SettingsManager.getInstance().getConfig().getBoolean("debug", false))
            $("[Debug] " + msg);
    }

    public static void info(String msg) {
        $(Level.INFO, msg);
    }

    public static void warning(String msg) {
        $(Level.WARNING, "[Warning] " + msg);
    }

    public static void error(String msg) {
        $(Level.SEVERE, "[Error] " + msg);
    }

    public static void debug(int a) {
        if (SettingsManager.getInstance().getConfig().getBoolean("debug", false))
            debug(a + "");
    }

    public void onDisable() {
        disabling = false;
        PluginDescriptionFile pdfFile = p.getDescription();
        SettingsManager.getInstance().saveSpawns();
        SettingsManager.getInstance().saveSystemConfig();
        for (Game g : GameManager.getInstance().getGames()) {
            try {
                g.disable();
            } catch (Exception ignored) {
            }
            QueueManager.getInstance().rollback(g.getID(), true);
        }

        logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " has now been disabled and reset");
    }

    public void onEnable() {
        plugin = this;
        logger = p.getLogger();
        startup();
        //getServer().getScheduler().scheduleSyncDelayedTask(this, new Startup(), 10);
    }

    public void setCommands() {
        getCommand("minegames").setExecutor(new CommandHandler(p));
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin worldEdit = getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit instanceof WorldEditPlugin) {
            return (WorldEditPlugin) worldEdit;
        } else {
            return null;
        }
    }

    public void startup() {

        datafolder = p.getDataFolder();

        PluginManager pm = getServer().getPluginManager();
        setCommands();

        SettingsManager.getInstance().setup(p);
        MessageManager.getInstance().setup();
        GameManager.getInstance().setup(p);

        try {
            FileConfiguration c = SettingsManager.getInstance().getConfig();
            if (c.getBoolean("stats.enabled"))
                DatabaseManager.getInstance().setup(p);
            QueueManager.getInstance().setup();
            StatsManager.getInstance().setup(p, c.getBoolean("stats.enabled"));
            dbcon = true;
        } catch (Exception e) {
            dbcon = false;
            e.printStackTrace();
            logger.severe("!!!Failed to connect to the database. Please check the settings and try again!!!");
            return;
        } finally {
            LobbyManager.getInstance().setup(p);
        }

        ChestRatioStorage.getInstance().setup();
        HookManager.getInstance().setup();
        pm.registerEvents(new PlaceEvent(), p);
        pm.registerEvents(new BreakEvent(), p);
        pm.registerEvents(new DeathEvent(), p);
        pm.registerEvents(new MoveEvent(), p);
        pm.registerEvents(new CommandCatch(), p);
        pm.registerEvents(new SignClickEvent(), p);
        pm.registerEvents(new ChestReplaceEvent(), p);
        pm.registerEvents(new LogoutEvent(), p);
        pm.registerEvents(new JoinEvent(p), p);
        pm.registerEvents(new TeleportEvent(), p);
        pm.registerEvents(LoggingManager.getInstance(), p);
        pm.registerEvents(new SpectatorEvents(), p);
        pm.registerEvents(new BandageUse(), p);
        pm.registerEvents(new KitEvents(), p);
        pm.registerEvents(new KeepLobbyLoadedEvent(), p);
        pm.registerEvents(new McMMOPreventer(), p);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (GameManager.getInstance().getBlockGameId(p.getLocation()) != -1) {
                p.teleport(SettingsManager.getInstance().getLobbySpawn());
            }
        }
    }
}

