package client.inventory;

import constants.GameConstants;

public class ModifyInventory {

    private final int mode;
    private Item item;
    private Short newPos = null;
    public ModifyInventory(final int mode, final Item item) {
        this.mode = mode;
        this.item = item.copy();
    }

    public ModifyInventory(final int mode, final Item item, final short oldPos) {
        this.mode = mode;
        this.item = item.copy();
        this.newPos = this.item.getPosition();
        this.item.setPosition(oldPos);
    }

    public final int getMode() {
        return mode;
    }

    public final int getInventoryType() {
        return GameConstants.getInventoryType(item.getItemId()).getType();
    }

    public final short getPosition() {
        return item.getPosition();
    }

    public final short getNewPosition() {
        return newPos == null ? 0 : newPos;
    }

    public final short getQuantity() {
        return item.getQuantity();
    }

    public final Item getItem() {
        return item;
    }

    public final void clear() {
        this.item = null;
    }

    public static class Types {
        public static final int ADD = 0;
        public static final int UPDATE = 1;
        public static final int MOVE = 2;
        public static final int REMOVE = 3;
        public static final int MOVE_TO_BAG = 5;
        public static final int UPDATE_IN_BAG = 6;
        public static final int REMOVE_IN_BAG = 7;
        public static final int MOVE_IN_BAG = 8;
        public static final int ADD_IN_BAG = 9;
    }
}
