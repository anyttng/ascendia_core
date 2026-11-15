package com.ascendiacore.gui.layout.model;

public sealed interface Length {
    record Fixed(int pixels) implements Length {
        public Fixed {
            if (pixels < 0) {
                throw new IllegalArgumentException("A fixed length cannot be negative: " + pixels);
            }
        }
    }

    record Natural() implements Length {
    }

    record Flexible(int min, int preferred) implements Length {
        public Flexible {
            if (min < 0 || min > preferred) {
                throw new IllegalArgumentException("A flexible length needs 0 <= min <= preferred: " + min + ", " + preferred);
            }
        }
    }

    record Fill(int weight) implements Length {
        public Fill {
            if (weight <= 0) {
                throw new IllegalArgumentException("A fill weight must be positive: " + weight);
            }
        }
    }

    static Length fixed(int pixels) {
        return new Fixed(pixels);
    }

    static Length natural() {
        return new Natural();
    }

    static Length flexible(int min, int preferred) {
        return new Flexible(min, preferred);
    }

    static Length fill() {
        return new Fill(1);
    }

    static Length fill(int weight) {
        return new Fill(weight);
    }
}
