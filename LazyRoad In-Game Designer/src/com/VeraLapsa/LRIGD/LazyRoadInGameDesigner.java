package com.VeraLapsa.LRIGD;

import com.VeraLapsa.LRIGD.commands.LRDCommand;
import com.creadri.lazyroad.Pillar;
import com.creadri.lazyroad.Road;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author VeraLapsa
 */
public class LazyRoadInGameDesigner extends JavaPlugin {

    public Log log = null;
    private static PluginDescriptionFile pdfFile = null;
    public static String B_PluginName = null;
    private Map<String, CommandHandler> commands = new HashMap<String, CommandHandler>();
    public Map<String, Road> roadsetup = new HashMap<String, Road>();
    public Map<String, Pillar> pillarsetup = new HashMap<String, Pillar>();
    public Map<String, Integer> keepid = new HashMap<String, Integer>();

    @Override
    public void onDisable() {
        log.Info("has been disabled!");
    }

    @Override
    public void onEnable() {
        pdfFile = this.getDescription();
        log = new Log(this);
        Plugin p = this.getServer().getPluginManager().getPlugin("LazyRoad");
        if (p != null) {
            getDataFolder().mkdirs();

            this.getConfig().options().copyDefaults(true);
            saveConfig();

            commands.put("lrd", new LRDCommand(this));

            log.Info("v" + pdfFile.getVersion() + "has been enabled!");
        } else {
            log.Severe("The server does not seem to have the LazyRoad plugin, it is required.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandHandler handler = commands.get(command.getName().toLowerCase());

        if (getConfig().getBoolean("debug.commandcalls")) {
            log.Debug(sender.getName() + " ran the /" + label + "command. Args: " + args.toString());
        }

        if (handler != null) {
            return handler.perform(sender, args);
        } else {
            return false;
        }
    }
}
