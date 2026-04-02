package com.splendor.config;

import com.splendor.model.*;
import java.io.*;
import java.util.*;

public class NobleLoader {

    public List<Noble> loadNobles(String path) {
        List<Noble> nobles = new ArrayList<>();

        // using filepath to read noble.csv 
        try (Scanner filesc = new Scanner(new File(path))) {
            if (filesc.hasNextLine()) {
                filesc.nextLine(); // skip header
            }

            while (filesc.hasNextLine()) {
                String line = filesc.nextLine().trim();
                if (line.isEmpty()) {
                    continue; // skip bad line
                }
                // split line into respective fields
                String[] fields = line.split(",");
                if (fields.length != 7) {
                    continue; // skip bad line
                }

                try {
                    // first field is name 
                    String name = fields[0].trim();
                    // prestigePoints worth
                    int points = Integer.parseInt(fields[1].trim());

                    // instantiate a new list of GemColors and quantities to add
                    List<GemColor> requirementColors = new ArrayList<>();
                    List<Integer> requirementQty = new ArrayList<>();
                    // this is the order defined in the noble.csv
                    GemColor[] colorOrder = {
                        GemColor.WHITE,
                        GemColor.BLUE,
                        GemColor.GREEN,
                        GemColor.RED,
                        GemColor.BLACK
                    };

                    for (int i = 0; i < colorOrder.length; i++) {
                        int qty = Integer.parseInt(fields[i + 2].trim());
                        if (qty > 0) {
                            // add the qty of gems required if > 0 to the respective colors
                            requirementColors.add(colorOrder[i]);
                            requirementQty.add(qty);
                        }
                    }
                    // add processed noble to list 
                    nobles.add(new Noble(name, requirementColors, requirementQty, points));

                } catch (NumberFormatException e) {
                    // skip bad line
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Noble file not found: " + path, e);
        }
        // return the list of all nobles parsed from csv
        return nobles;
    }
}
