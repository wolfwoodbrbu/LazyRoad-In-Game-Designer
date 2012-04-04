package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class TestRoadCommand extends CommandHandler {

    public TestRoadCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        if (plugin.roadsetup.containsKey(sender.getName())) {
            plugin.getServer().dispatchCommand(sender, "saveroad temp true");
            sender.sendMessage(ChatColor.DARK_AQUA + "Testing currently set road. " + ChatColor.WHITE + "/teststop" + ChatColor.DARK_AQUA + " to stop and undo.");
            plugin.getServer().dispatchCommand(sender, "road temp");
            return true;
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you yet.");
            return true;
        }
    }
}
