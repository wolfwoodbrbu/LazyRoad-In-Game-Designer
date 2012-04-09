package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class TestPillarCommand extends CommandHandler {

    public TestPillarCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        if (plugin.pillarsetup.containsKey(sender.getName())) {
            plugin.getServer().dispatchCommand(sender, "savepillar temp_" + sender.getName() + " true");
            if (args.length == 0 && plugin.roadsetup.containsKey(sender.getName())) {
                plugin.getServer().dispatchCommand(sender, "saveroad temp_" + sender.getName() + " true");
                sender.sendMessage(ChatColor.DARK_AQUA + "Testing currently set road and pillar. " + ChatColor.WHITE + "/teststop" + ChatColor.DARK_AQUA + " to stop and undo.");
                plugin.getServer().dispatchCommand(sender, "bridge temp_" + sender.getName() + " temp_" + sender.getName());
                return true;
            } else if (args.length > 0) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Testing currently set road. " + ChatColor.WHITE + "/teststop" + ChatColor.DARK_AQUA + " to stop and undo.");
                plugin.getServer().dispatchCommand(sender, "bridge " + args[0] + " temp_" + sender.getName());
                return true;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not a road set yet to use for testing a pillar.");
                sender.sendMessage(ChatColor.DARK_RED + "Either set a road or run with" + ChatColor.WHITE + " /testpillar <name of saved road>");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you yet.");
            return true;
        }
    }
}
