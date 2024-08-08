package com.xg7plugins.api.menumanager;

import com.xg7plugins.xg7menus.api.items.Item;
import com.xg7plugins.xg7menus.api.menus.Menu;
import com.xg7plugins.xg7menus.api.menus.MenuPages;

import java.util.HashMap;

public class MenuManager {

    private static final MenuPages pages = new MenuPages();
    private static final HashMap<String, Item> storedItems = new HashMap<>();

    public static void addMenus(Menu... menus) {
        pages.addPages(menus);
    }
    public static Menu getMenu(String id) {
        return pages.getMenu(id);
    }
    public static void storeItem(String path, Item item) {
        storedItems.put(path, item);
    }
    public static Item getItem(String path) {
        return storedItems.get(path);
    }
}
