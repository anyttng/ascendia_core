package com.ascendiacore.gui.layout;

import java.util.List;

import com.ascendiacore.gui.layout.model.Length;

public final class Distribution {
    public record Item(Length length, int natural) {
    }

    private Distribution() {
    }

    public static int[] sizes(int available, int gap, List<Item> items) {
        int[] sizes = preferredSizes(items);
        int space = available - gap * Math.max(items.size() - 1, 0);
        int wanted = sum(sizes);
        if (wanted > space) {
            shrinkFlexible(sizes, items, wanted - space);
        } else {
            growFill(sizes, items, space - wanted);
        }
        return sizes;
    }

    public static int preferredSize(Item item) {
        return switch (item.length()) {
            case Length.Fixed fixed -> fixed.pixels();
            case Length.Natural natural -> item.natural();
            case Length.Flexible flexible -> flexible.preferredPixels();
            case Length.Fill fill -> 0;
        };
    }

    private static int[] preferredSizes(List<Item> items) {
        int[] sizes = new int[items.size()];
        for (int index = 0; index < sizes.length; index++) {
            sizes[index] = preferredSize(items.get(index));
        }
        return sizes;
    }

    private static void shrinkFlexible(int[] sizes, List<Item> items, int deficit) {
        int[] room = new int[sizes.length];
        for (int index = 0; index < sizes.length; index++) {
            if (items.get(index).length() instanceof Length.Flexible flexible) {
                room[index] = flexible.preferredPixels() - flexible.minPixels();
            }
        }
        int[] cuts = share(Math.min(deficit, sum(room)), room);
        for (int index = 0; index < sizes.length; index++) {
            sizes[index] -= cuts[index];
        }
    }

    private static void growFill(int[] sizes, List<Item> items, int surplus) {
        int[] weights = new int[sizes.length];
        for (int index = 0; index < sizes.length; index++) {
            if (items.get(index).length() instanceof Length.Fill fill) {
                weights[index] = fill.weight();
            }
        }
        int[] gains = share(surplus, weights);
        for (int index = 0; index < sizes.length; index++) {
            sizes[index] += gains[index];
        }
    }

    private static int[] share(int amount, int[] weights) {
        int[] parts = new int[weights.length];
        long total = sum(weights);
        if (total == 0) {
            return parts;
        }
        long seen = 0;
        long given = 0;
        for (int index = 0; index < weights.length; index++) {
            seen += weights[index];
            long upToHere = amount * seen / total;
            parts[index] = (int) (upToHere - given);
            given = upToHere;
        }
        return parts;
    }

    private static int sum(int[] values) {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum;
    }
}
