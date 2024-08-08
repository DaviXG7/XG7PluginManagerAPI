package com.xg7plugins.api.menumanager;

import com.xg7plugins.xg7menus.api.items.Button;
import com.xg7plugins.xg7menus.api.items.Item;
import com.xg7plugins.xg7menus.api.items.SkullItem;
import com.xg7plugins.xg7menus.api.utils.XSeries.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.Objects;
import java.util.function.Consumer;

public class ItemConverter {

    public static Item convertItem(FileConfiguration configuration, String path, Consumer<InventoryClickEvent> eventConsumer) {
        Item item;
        if (configuration.getString(path + ".material").startsWith("PLAYER_HEAD")) {

            String[] value = configuration.getString(path + ".material").split(", ")[0].split("=");
            item = SkullItem.newSkullItem(
                    Integer.parseInt(Objects.requireNonNull(configuration.getString(path + ".item"))),
                    configuration.getInt(path + ".amount")
            );
            if (value[0].equals("OWNER")) ((SkullItem) item).setOwner(value[1]);
            if (value[0].equals("VALUE")) ((SkullItem) item).setValue(value[1]);
        } else {
            item = Item.newItem(
                    XMaterial.matchXMaterial(configuration.getString(path + ".material")).get().parseItem(configuration.getInt(path + ".amount")),
                    Integer.parseInt(Objects.requireNonNull(configuration.getString(path + ".item"))),
                    Button.click(eventConsumer)
            );
        }

        item.click(Button.click(eventConsumer))
                .name(configuration.getString(path + ".name"))
                .lore(configuration.getStringList(path + ".lore"))
                .unbreakable(configuration.getBoolean(path + ".unbreakable"));
        if (configuration.getConfigurationSection(path + ".item-flags") != null) {
            configuration.getStringList(path + ".item-flags").stream().map(ItemFlag::valueOf).forEach(item::addFlags);
        }

        if (configuration.getConfigurationSection(path + ".custom-model-data") != null) {
            item.setCustomModelData(configuration.getInt(path + ".custom-model-data"));
        }
        if (configuration.getConfigurationSection(path + ".enchantments") != null) {
            configuration.getStringList(path + ".enchantments").forEach(s ->
                    item.addEnchant(Enchantment.getByName(s.split(", ")[0]), Integer.parseInt(s.split(", ")[1]))
            );
        }

        return item;
    }
}
