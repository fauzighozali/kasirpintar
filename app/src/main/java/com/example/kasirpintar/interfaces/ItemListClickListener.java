package com.example.kasirpintar.interfaces;

import com.example.kasirpintar.model.ItemInfo;

public interface ItemListClickListener {
    void onListClick(ItemInfo item);
    void itemRemoved();
}

