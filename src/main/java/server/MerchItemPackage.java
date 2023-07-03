package server;

import client.inventory.Item;

import java.util.ArrayList;
import java.util.List;

public class MerchItemPackage {

    private long savedTime;
    private int meso = 0;
    private int packageId;
    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public long getSavedTime() {
        return savedTime;
    }

    public void setSavedTime(long time) {
        this.savedTime = time;
    }

    public int getMeso() {
        return meso;
    }

    public void setMeso(int meso) {
        this.meso = meso;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }
}
