package com.talkwalker.hashcode.books;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MinMaxPriorityQueue;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Talkwalker NPE Catchers Team:
 *      - btuna
 *      - mruggiero
 *      - mstolorz
 */


public class HashCode2020 {


    public static void main(String[] args) throws IOException {

        String file1 = "/home/dev/hashcode/a_example.txt";
        String file2 = "/home/dev/hashcode/b_read_on.txt";
        String file3 = "/home/dev/hashcode/c_incunabula.txt";
        String file4 = "/home/dev/hashcode/d_tough_choices.txt";
        String file5 = "/home/dev/hashcode/e_so_many_books.txt";
        String file6 = "/home/dev/hashcode/f_libraries_of_the_world.txt";

        List<String> files = ImmutableList.of(file1, file2, file3, file4, file5, file6);

        for (String inputFile : files) {

            int numBook = 0;
            int numLib = 0;
            int numDays = 0;
            int[] bookScores = {};

            List<Library> libraries = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                boolean first = true;
                boolean second = false;
                String line;
                while ((line = br.readLine()) != null) {
                    String[] s = line.split(" ");
                    if (first) {
                        numBook = Integer.parseInt(s[0]);
                        numLib = Integer.parseInt(s[1]);
                        numDays = Integer.parseInt(s[2]);
                        first = false;
                        second = true;
                        continue;
                    }
                    if (second) {
                        bookScores = new int[numBook];
                        for (int i = 0; i < s.length; i++) {
                            bookScores[i] = Integer.parseInt(s[i]);
                        }
                        second = false;
                        continue;
                    }
                    //read other lines and load them into something
                    Library library = new Library();

                    library.libraryId = libraries.size();

                    library.setBookNumInLibrary(Integer.parseInt(s[0]));
                    library.setNumDayToSignup(Integer.parseInt(s[1]));
                    library.setBookThatCanBeShippedPerDay(Integer.parseInt(s[2]));
                    line = br.readLine();
                    if (line != null) {
                        s = line.split(" ");
                    } else {
                        break;
                    }
                    List<Integer> listBook = new ArrayList<>();
                    for (int i = 0; i < library.getBookNumInLibrary(); i++) {
                        listBook.add(Integer.parseInt(s[i]));
                    }
                    library.setBooksInLib(listBook);
                    libraries.add(library);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // how much value we can get from each library

            List<String> output = new ArrayList<>();

            while (!libraries.isEmpty() && numDays > 0) {
                final int[] _bookScores = bookScores.clone();
                int[] libraryScores = new int[libraries.size()];
                int[] libraryProductiveDays = new int[libraries.size()];

                for (int i = 0; i < libraries.size(); i++) {

                    Library lib = libraries.get(i);

                    long productiveDays = Math.max(0, numDays - lib.getNumDayToSignup());
                    long maxBooksScanned = Math.min(productiveDays * lib.getBookThatCanBeShippedPerDay(), lib.bookNumInLibrary);


                    int libraryMaxScore = lib.getBooksInLib().stream()
                            .mapToInt(bookId -> _bookScores[bookId])
                            .boxed()
                            .sorted(Comparator.reverseOrder())
                            .limit(maxBooksScanned)
                            .reduce(0, Integer::sum);

                    libraryScores[i] = libraryMaxScore;
                    libraryProductiveDays[i] = (int) Math.ceil(maxBooksScanned / (float) lib.getBookThatCanBeShippedPerDay());
                }

                // lets pick top 10 libraries with highest score

                Comparator<Integer> fancyLibraryComparator = (lib1, lib2) -> {
                    if (libraryScores[lib1] == libraryScores[lib2]) {
                        return Integer.compare(libraryProductiveDays[lib2], libraryProductiveDays[lib1]);
                    } else {
                        return Integer.compare(libraryScores[lib2], libraryScores[lib1]);
                    }
                };

                MinMaxPriorityQueue<Integer> pq = MinMaxPriorityQueue.orderedBy(fancyLibraryComparator)
                        .maximumSize(100)
                        .create();

                for (int i = 0; i < libraryScores.length; i++) {
                    pq.add(i);
                }

                List<Integer> bestLibraries = IntStream.range(0, libraryScores.length).boxed().sorted(fancyLibraryComparator).limit(50).collect(Collectors.toList());

                while (!pq.isEmpty()) {

                    int i = pq.poll();

                    Library lib = libraries.get(i);
                    libraryScores[i] += scoreOfNextbestLibrary(libraries, numDays - lib.getNumDayToSignup(), _bookScores, lib, bestLibraries);
                }


                int bestLibraryId = 0;
                for (int i = 0; i < libraryScores.length; i++) {
                    if (libraryScores[i] > libraryScores[bestLibraryId] ||
                            (libraryScores[i] == libraryScores[bestLibraryId] && libraryProductiveDays[i] > libraryProductiveDays[bestLibraryId])) {
                        bestLibraryId = i;
                    }
                }


                // lets work out best library
                {
                    Library bestLibrary = libraries.get(bestLibraryId);

                    long productiveDays = Math.max(0, numDays - bestLibrary.getNumDayToSignup());
                    long maxBooksScanned = Math.min(productiveDays * bestLibrary.getBookThatCanBeShippedPerDay(), bestLibrary.bookNumInLibrary);


                    List<Integer> booksToProcess = bestLibrary.getBooksInLib().stream()
                            .sorted(Comparator.comparingInt(b -> _bookScores[b]))
                            .sorted(Comparator.reverseOrder())
                            .limit(maxBooksScanned)
                            .collect(Collectors.toList());

                    if (booksToProcess.size() > 0) {
                        output.add("" + bestLibrary.libraryId + " " + booksToProcess.size());

                        // books to process

                        StringBuilder sb = new StringBuilder();

                        for (int b : booksToProcess) {
                            sb.append(b).append(" ");
                        }
                        output.add(sb.toString());

                    }
                    for (int bookId : booksToProcess) {
                        bookScores[bookId] = 0;
                    }

                    numDays = numDays - bestLibrary.getNumDayToSignup();

                    libraries.remove(bestLibraryId);

                    if (libraries.size() % 100 == 0) {
                        System.out.println("days left: " + numDays);
                    }
                }
            }

            // write to file

            String outputFile = inputFile.replaceAll("hashcode/", "hashcode/6th/") + "out";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            writer.write(String.format("%d\n", output.size() / 2));
            System.out.println(output.size() / 2);

            for (String line : output) {
                writer.write(line + "\n");
                System.out.println(line);
            }

            writer.close();
        }
    }


    public static int scoreOfNextbestLibrary(List<Library> libraries, int daysLeft, final int[] _bookScores, Library currentLibrary,
                                             List<Integer> bestLibrariesIds) {

        List<Integer> booksToskip = currentLibrary.getBooksInLib();

        int libraryMaxScore = 0;

        for (int i : bestLibrariesIds) {
            Library lib = libraries.get(i);

            long productiveDays = Math.max(0, daysLeft - lib.getNumDayToSignup());
            long maxBooksScanned = Math.min(productiveDays * lib.getBookThatCanBeShippedPerDay(), lib.bookNumInLibrary);


            int score = lib.getBooksInLib().stream()
                    .filter(bookId -> !booksToskip.contains(bookId))
                    .mapToInt(bookId -> _bookScores[bookId])
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(maxBooksScanned)
                    .reduce(0, (a, b) -> a + b);

            libraryMaxScore = Math.max(score, libraryMaxScore);

        }

        return libraryMaxScore;
    }


}


class Library {

    int libraryId;
    int bookNumInLibrary;
    int numDayToSignup;
    int bookThatCanBeShippedPerDay;

    List<Integer> booksInLib = new ArrayList<>();


    public int getBookNumInLibrary() {
        return bookNumInLibrary;
    }

    public void setBookNumInLibrary(int bookNumInLibrary) {
        this.bookNumInLibrary = bookNumInLibrary;
    }

    public int getNumDayToSignup() {
        return numDayToSignup;
    }

    public void setNumDayToSignup(int numDayToSignup) {
        this.numDayToSignup = numDayToSignup;
    }

    public int getBookThatCanBeShippedPerDay() {
        return bookThatCanBeShippedPerDay;
    }

    public void setBookThatCanBeShippedPerDay(int bookThatCanBeShippedPerDay) {
        this.bookThatCanBeShippedPerDay = bookThatCanBeShippedPerDay;
    }

    public List<Integer> getBooksInLib() {
        return booksInLib;
    }

    public void setBooksInLib(List<Integer> booksInLib) {
        this.booksInLib = booksInLib;
    }
}