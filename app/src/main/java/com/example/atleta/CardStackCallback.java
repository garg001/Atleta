package com.example.atleta;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class CardStackCallback extends DiffUtil.Callback {
    private List<ItemModel> old, nevv ;

    public CardStackCallback(List<ItemModel> old, List<ItemModel> nevv) {
        this.old = old;
        this.nevv = nevv;
    }

    @Override
    public int getOldListSize() {
        return old.size();
    }

    @Override
    public int getNewListSize() {
        return nevv.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).getImage() ==  nevv.get(newItemPosition).getImage();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition) == nevv.get(newItemPosition);
    }
}
