package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class TestStopCommand extends CommandHandler {

    public TestStopCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        sender.sendMessage(ChatColor.DARK_AQUA + "Stopping and undoing the test.");
        plugin.getServer().dispatchCommand(sender, "road stop");
        plugin.getServer().dispatchCommand(sender, "road undo");
        return true;
    }
}
