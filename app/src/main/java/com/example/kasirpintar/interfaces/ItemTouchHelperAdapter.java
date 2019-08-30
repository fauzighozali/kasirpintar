package com.example.kasirpintar.interfaces;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwipe(int adapterPosition, int direction);
}
