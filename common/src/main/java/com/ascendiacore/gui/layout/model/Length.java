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

    record Flexible(int minPixels, int preferredPixels) implements Length {
        public Flexible {
            if (minPixels < 0 || minPixels > preferredPixels) {
                throw new IllegalArgumentException("A flexible length needs 0 <= minPixels <= preferredPixels: " + minPixels + ", " + preferredPixels);
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

    static Length flexible(int minPixels, int preferredPixels) {
        return new Flexible(minPixels, preferredPixels);
    }

    static Length fill() {
        return new Fill(1);
    }

    static Length fill(int weight) {
        return new Fill(weight);
    }
}
