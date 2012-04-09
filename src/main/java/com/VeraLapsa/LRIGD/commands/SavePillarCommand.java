package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import com.creadri.lazyroad.Pillar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class SavePillarCommand extends CommandHandler {

    private Pillar pillar = null;

    public SavePillarCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        if (!isValidName(args[0])) {
            sender.sendMessage(ChatColor.DARK_RED + "sorry that is not a valid name for a file");
            return true;
        }
        if (plugin.pillarsetup.containsKey(sender.getName())) {
            switch (args.length) {
                case 1:
                    if (savePillar(sender, args[0], false)) {
                        return true;
                    } else {
                        return false;
                    }
                case 2:
                    if (args[1].equalsIgnoreCase("true")) {
                        if (savePillar(sender, args[0], true)) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                    }
                default:
                    return false;
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "No pillars has been set up yet to save.");
            return true;
        }
    }

    private boolean savePillar(CommandSender sender, String name, boolean temp) {
        pillar = plugin.pillarsetup.get(sender.getName());
        if (pillar.size() == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "You must have at least one part in order to save the road");
            return true;
        }
        File folder = new File(plugin.getServer().getPluginManager().getPlugin("LazyRoad").getDataFolder(), "pillars");
        if (folder == null || !folder.isDirectory()) {
            folder = plugin.getDataFolder();
            log.Severe("Error getting the LazyRoad's pillars folder. Defaulting to the Designer's folder");
            sender.sendMessage(ChatColor.DARK_RED + "Error getting the LazyRoad's pillars folder.");
            sender.sendMessage(ChatColor.DARK_RED + "Defaulting to the Designer's folder");
        }
        File saveFile = new File(folder, name.concat(".ser"));
        try {

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));

            oos.writeObject(pillar);

            oos.close();

        } catch (Exception ex) {
            sender.sendMessage(ChatColor.DARK_RED + "An error occured when trying to save " + saveFile.getName());
            log.Severe(ChatColor.DARK_RED + "An error occured when trying to save " + saveFile.getName());
            log.Severe(ex.toString());
        }
        if (!temp) {
            sender.sendMessage(ChatColor.DARK_GREEN + "The pillar " + ChatColor.WHITE + ChatColor.BOLD + saveFile.getName() + ChatColor.DARK_GREEN + " was saved.");
        }
        plugin.getServer().dispatchCommand(sender, "road reload");
        return true;
    }

    private static boolean isValidName(String text) {
        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n"
                + "^                                # Anchor to start of string.        \n"
                + "(?!                              # Assert filename is not: CON, PRN, \n"
                + "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n"
                + "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n"
                + "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n"
                + "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n"
                + "  (?:\\.[^.]*)?                  # followed by optional extension    \n"
                + "  $                              # and end of string                 \n"
                + ")                                # End negative lookahead assertion. \n"
                + "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n"
                + "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n"
                + "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        boolean isMatch = matcher.matches();
        return isMatch;
    }
}
