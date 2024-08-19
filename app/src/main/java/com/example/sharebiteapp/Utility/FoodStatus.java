package com.example.sharebiteapp.Utility;

public enum FoodStatus {
    Available(0),
    Expired(1),
    Donated(2),
    Requested(3),
    Cancelled(4);

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
    public static FoodStatus fromString(String statusString) {
        for (FoodStatus status : FoodStatus.values()) {
            if (status.name().equalsIgnoreCase(statusString)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No FoodStatus found with name: " + statusString);
    }

}
