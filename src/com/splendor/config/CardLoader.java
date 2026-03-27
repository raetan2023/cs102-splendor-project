package com.splendor.config;

import com.splendor.model.*;

import java.io.*;
import java.util.*;

public class CardLoader {

    public List<DevelopmentCard> loadCards(String path) {
        List<DevelopmentCard> cards = new ArrayList<>();

        try (Scanner filesc = new Scanner(new File(path))) {
            if (filesc.hasNextLine()) {
                filesc.nextLine(); // skip header
            }

            while (filesc.hasNextLine()) {
                String line = filesc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length != 8) {
                    continue;
                }

                try {
                    int tier = Integer.parseInt(fields[0].trim());
                    int points = Integer.parseInt(fields[1].trim());
                    GemColor bonus = GemColor.valueOf(fields[2].trim().toUpperCase());

                    // create map for card cost
                    Map<GemColor, Integer> cost = new EnumMap<>(GemColor.class);
                    // with GemColor color as key, and the number parsed respectively as value
                    cost.put(GemColor.BLACK, Integer.parseInt(fields[3].trim()));
                    cost.put(GemColor.BLUE, Integer.parseInt(fields[4].trim()));
                    cost.put(GemColor.GREEN, Integer.parseInt(fields[5].trim()));
                    cost.put(GemColor.RED, Integer.parseInt(fields[6].trim()));
                    cost.put(GemColor.WHITE, Integer.parseInt(fields[7].trim()));

                    // add processed card to cards list 
                    cards.add(new DevelopmentCard(tier, points, bonus, cost));

                } catch (IllegalArgumentException e) {
                    // skip bad line
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Card file not found: " + path, e);
        }

        return cards;
    }
}