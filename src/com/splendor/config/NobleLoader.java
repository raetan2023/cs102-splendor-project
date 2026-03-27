package com.splendor.config;

import java.io.*;
import java.util.*;
import com.splendor.model.*;

public class NobleLoader {

    public List<Noble> loadNobles(String path) {
        List<Noble> nobles = new ArrayList<>();

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
                if (fields.length != 7) {
                    continue;
                }

                try {
                    String name = fields[0].trim();
                    int points = Integer.parseInt(fields[1].trim());

                    List<GemColor> requirementColors = new ArrayList<>();
                    List<Integer> requirementQty = new ArrayList<>();

                    GemColor[] colorOrder = {
                        GemColor.BLACK,
                        GemColor.BLUE,
                        GemColor.GREEN,
                        GemColor.RED,
                        GemColor.WHITE
                    };

                    for (int i = 0; i < colorOrder.length; i++) {
                        int qty = Integer.parseInt(fields[i + 2].trim());
                        if (qty > 0) {
                            // add the qty of gems required if > 0 to the respective colors
                            requirementColors.add(colorOrder[i]);
                            requirementQty.add(qty);
                        }
                    }
                    // add to list of nobles 
                    nobles.add(new Noble(name, requirementColors, requirementQty, points));

                } catch (NumberFormatException e) {
                    // skip bad line
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Noble file not found: " + path, e);
        }

        return nobles;
    }
}
