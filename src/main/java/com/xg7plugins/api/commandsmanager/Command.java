package com.xg7plugins.api.commandsmanager;

import com.xg7plugins.xg7menus.api.items.Button;
import com.xg7plugins.xg7menus.api.items.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@Getter
public abstract class Command {

    private String name;
    private ItemStack icon;
    private String description;
    private String permission;
    private String[] aliases;
    private SubCommand[] subCommands;

    public String getSyntax() {

        StringBuilder builder = new StringBuilder();
        builder.append("/").append(name);
        for (SubCommand subCommand : subCommands) {
            if (subCommand.isRequired()) builder.append(" <").append(subCommand.getName()).append(">");
            else builder.append(" [").append(subCommand.getName()).append("]");
        }
        return builder.toString();

    }

    public abstract void onCommand(org.bukkit.command.Command command, CommandSender sender, String[] args, String label);
    public abstract List<String> onTabComplete(org.bukkit.command.Command command, CommandSender sender, String[] args, String label);

}
