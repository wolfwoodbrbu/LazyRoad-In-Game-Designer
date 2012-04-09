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
public class SetGradeCommand extends CommandHandler {

    private Road road = null;

    public SetGradeCommand(LazyRoadInGameDesigner plugin) {
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
            try {
                if (setGradiant(Integer.parseInt(args[0]))) {
                    plugin.roadsetup.put(sender.getName(), road);
                    plugin.RoadInfo(sender, road, keepid);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "Enter a positive number as the gradiant next time.");
                    return true;
                }

            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.DARK_RED + "Enter a positive number as the gradiant next time.");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you to add a gradiant to.");
            return true;
        }
    }

    private boolean setGradiant(int grade) {
        if (grade >= 0) {
            road.setMaxGradient(grade);
            return true;
        } else {
            return false;
        }

    }
}
