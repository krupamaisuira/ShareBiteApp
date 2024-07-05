package com.example.sharebiteapp.Utility;

public enum FoodStatus {
    Pending(0),
    expired(1),
    damaged(2),
    Donated(3);

    private int index;

    FoodStatus(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index + 1;
    }
    public static FoodStatus getByIndex(int index) {
        for (FoodStatus status : FoodStatus.values()) {
            if (status.getIndex() == index) {
                return status;
            }
        }
        throw new IllegalArgumentException("No FoodStatus found with index: " + index);
    }

}
