package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import com.creadri.lazyroad.Road;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class RoadInfoCommand extends CommandHandler {

    private Road road = null;

    public RoadInfoCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        int keepid = plugin.getConfig().getInt("users." + sender.getName() + ".keepid", -1);
        if (plugin.roadsetup.containsKey(sender.getName())) {
            road = plugin.roadsetup.get(sender.getName());
            plugin.RoadInfo(sender, road, keepid);
            return true;
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you yet.");
            return true;
        }
    }
}
