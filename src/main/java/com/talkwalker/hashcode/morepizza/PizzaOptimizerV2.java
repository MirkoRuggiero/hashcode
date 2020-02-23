package com.talkwalker.hashcode.morepizza;

import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PizzaOptimizerV2 {
    public static void main(String[] args) {
        String baseFolder = "/home/dev/hashcode/";
        String example1 = "a_example.in";
        String example2 = "b_small.in";
        String example3 = "c_medium.in";
        String example4 = "d_quite_big.in";
        String example5 = "e_also_big.in";

        List<String> inputs = ImmutableList.of(example1, example2, example3, example4, example5);
        for (String example : inputs) {

            List<Integer> selectedPizzas = new ArrayList<>();

//            Stopwatch sw = Stopwatch.createStarted();
            int[] menu = {};
            int[] slices = {};
            int[] slice_index = {};
            int maxSlices = 0;
            int pizzaTypes = 0;
            //read file and load menu
            try (BufferedReader br = new BufferedReader(new FileReader(baseFolder + example))) {
                boolean first = true;
                String line;
                while ((line = br.readLine()) != null) {
                    String[] s = line.split(" ");
                    if (first) {
                        maxSlices = Integer.parseInt(s[0]);
                        pizzaTypes = Integer.parseInt(s[1]);
                        menu = new int[pizzaTypes];
                        slices = new int[1 + maxSlices];
                        slice_index = new int[1 + maxSlices];
//            System.out.println("Max Pizza Slice: " + maxSlices);
//            System.out.println("Different pizza types available: " + pizzaTypes);
                        first = false;
                        continue;
                    }
                    //load pizzas in the menu with ordered-pizza-flag = false
                    for (int i = 0; i < pizzaTypes; i++) {
                        menu[i] = Integer.parseInt(s[i]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //algorithm
            int last = 0;

            for (int pizzaId = 0; pizzaId < menu.length && slices[slices.length - 1] == 0; pizzaId++) {

//        System.out.println(" p" + pizzaId);
//        if (pizzaId % 10 == 0) {
//          System.out.println(String.format("%6.4f zeoroes", countZeroes(slices) / (float) slices.length));
//        }
                int pizza = menu[pizzaId];

                for (int i = last - 1; i >= 0; i--) {
                    if (slices[slice_index[i]] != 0 && slice_index[i] + pizza < slices.length && slices[slice_index[i] + pizza] == 0) {
                        slices[slice_index[i] + pizza] = slice_index[i];
                        slice_index[last] = slice_index[i] + pizza;
                        last++;
                    }
                }

                if (slices[pizza] == 0) {
                    slices[pizza] = -1;
                    slice_index[last] = pizza;
                    last++;
                }

            }
            //find first non-zero element fromm the bottom
            int sum = 0;
            for (int i = slices.length - 1; i >= 1; i--) {
                if (slices[i] != 0) {
                    sum = i;
                    break;
                }
            }
//      System.out.println(String.format("%d = ", sum));
            int counter = 0;
            for (int i = sum; i != -1; i = slices[i]) {
                counter++;
                int nextPizza;

                if (slices[i] == -1) {
                    nextPizza = i;

                } else {
                    nextPizza = i - slices[i];
                }
//        System.out.print(String.format(" + %d  ", nextPizza));
                selectedPizzas.add(nextPizza);
            }


            List<Integer> menuList = Arrays.stream(menu).boxed().collect(Collectors.toList());
            List<Integer> selectedPizzasIndex = new ArrayList<>(selectedPizzas.size());

            for (int s : selectedPizzas) {
                int index = menuList.indexOf(s);
                menuList.set(index, 0);
                selectedPizzasIndex.add(index);
            }


            String resultString = selectedPizzasIndex.stream().sorted().map(s -> "" + s).collect(Collectors.joining(" "));

//      System.out.println("--------------");
            System.out.println(selectedPizzasIndex.size());
            System.out.println(resultString);

//      System.out.println("took " + sw);
//      System.out.println("_______________________________________________________");
//      System.out.println();
        }
    }

    private static int countZeroes(int[] ar) {
        int count = 0;
        for (int value : ar) {
            if (value == 0) {
                count++;
            }
        }
        return count;
    }
}