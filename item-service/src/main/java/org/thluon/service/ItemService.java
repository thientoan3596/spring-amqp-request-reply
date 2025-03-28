package org.thluon.service;

import org.springframework.stereotype.Service;
import org.thluon.entity.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Example service
 */
@Service
public class ItemService {
    private static final List<Item> ITEMS = new ArrayList<>();
    {
        ITEMS.add(new Item(1L, "Laptop", "Dell", 1000.0));
        ITEMS.add(new Item(2L, "Mobile", "Samsung", 500.0));
        ITEMS.add(new Item(3L, "Tablet", "Apple", 800.0));
        ITEMS.add(new Item(4L, "Camera", "Canon", 600.0));
    }
    public List<Item> getItems() {
        return ITEMS;
    }
}
