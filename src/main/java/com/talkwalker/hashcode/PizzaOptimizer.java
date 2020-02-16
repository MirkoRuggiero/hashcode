package com.talkwalker.hashcode;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PizzaOptimizer {


    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        int totalFinalScore = 0;
        //num of slice - ordered flag
        Map<Integer, Boolean> menu = new TreeMap<>(Comparator.reverseOrder()); //first element = most expensive pizza

        //read file and load menu
        String baseFolder = "/home/mirko/development/google-hashcode/";
        String example1 = "a_example.in";
        String example2 = "b_small.in";
        String example3 = "c_medium.in";
        String example4 = "d_quite_big.in";
        String example5 = "e_also_big.in";

        List<String> files = ImmutableList.of(example1, example2, example3, example4, example5);
        for (String example : files) {


            int maxSlices = 0;
            int pizzaTypes = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(baseFolder + example))) {
                boolean first = true;
                String line;
                while ((line = br.readLine()) != null) {
                    String[] s = line.split(" ");
                    if (first) {
                        maxSlices = Integer.parseInt(s[0]);
                        pizzaTypes = Integer.parseInt(s[1]);
                        System.out.println("Max Pizza Slice: " + maxSlices);
                        System.out.println("Different pizza types available: " + pizzaTypes);
                        first = false;
                        continue;
                    }
                    //load pizzas in the menu with ordered-pizza-flag = false
                    Arrays.stream(s).
                            forEach(n -> menu.put(Integer.parseInt(n), false));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            //algorithm
            int maxScore = 0;
            while (!menu.isEmpty()) {
                int slicesLeft = maxSlices;
                //go through the menu and order pizza
                for (Map.Entry<Integer, Boolean> menuEntry : menu.entrySet()) {
                    if (slicesLeft - menuEntry.getKey() >= 0) {
                        slicesLeft -= menuEntry.getKey();
                        menu.put(menuEntry.getKey(), true);
                    }
                }
                //count num of slices ordered
                int score = menu.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .mapToInt(Map.Entry::getKey)
                        .sum();

                //update max
                if (score <= maxSlices) {
                    maxScore = Math.max(score, maxScore);
                }
                //remove most expensive pizza
                Map.Entry<Integer, Boolean> entry = menu.entrySet().iterator().next();
                menu.remove(entry.getKey(), entry.getValue());
                //reset menu entries
                menu.forEach((key, value) -> menu.put(key, false));
            }
            totalFinalScore += (maxSlices - maxScore);
            System.out.println("Final score: " + maxScore);
            System.out.println("Difference with optimum solution: " + (maxSlices - maxScore));
            System.out.println("________________________________________________________________");
        }
        System.out.println("Total inverse final score: " + totalFinalScore);
        System.out.println("Time elapsed: " + stopwatch);
    }
}
