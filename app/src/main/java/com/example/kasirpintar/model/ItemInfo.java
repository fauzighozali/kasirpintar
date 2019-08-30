package com.example.kasirpintar.model;

public class ItemInfo {

    private int ItemSku;
    private String ItemDate;
    private String ItemName;
    private int ItemStock;
    private byte[] ItemImage;

    public ItemInfo(int itemSku, String itemDate, String itemName, int itemStock, byte[] itemImage) {
        ItemSku = itemSku;
        ItemDate = itemDate;
        ItemName = itemName;
        ItemStock = itemStock;
        ItemImage = itemImage;
    }

    public ItemInfo(String itemDate, String itemName, int itemStock, byte[] itemImage) {
        ItemDate = itemDate;
        ItemName = itemName;
        ItemStock = itemStock;
        ItemImage = itemImage;
    }

    public String getItemDate() {
        return ItemDate;
    }

    public int getItemSku() {
        return ItemSku;
    }

    public String getItemName() {
        return ItemName;
    }

    public int getItemStock() {
        return ItemStock;
    }

    public byte[] getItemImage() {
        return ItemImage;
    }
}
