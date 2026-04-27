package org.model;

public enum Wall {

    TOP, RIGHT, BOTTOM, LEFT;

    public Wall opposite() {
        return switch (this) {
            case TOP    -> BOTTOM;
            case BOTTOM -> TOP;
            case LEFT   -> RIGHT;
            case RIGHT  -> LEFT;
        };
    }

    public int rowDelta() {
        return switch (this) {
            case TOP    -> -1;
            case BOTTOM ->  1;
            default     ->  0;
        };
    }

    public int colDelta() {
        return switch (this) {
            case RIGHT ->  1;
            case LEFT  -> -1;
            default    ->  0;
        };
    }
}

