package externalSortArg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class ExternalSortStrings {


    static int N = 2000000; // size of the file in disk
    static int M = 100000; // max items the memory buffer can hold

    public static void externalSort(String fileName) {
        String tfile = "temp-file-";
        String[] buffer = new String[M < N ? M : N];

        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            int slices = (int) Math.ceil((double) N / M);

            int i, j;
            i = j = 0;
            // Iterate through the elements in the file
            for (i = 0; i < slices; i++) {
                // Read M-element chunk at a time from the file
                for (j = 0; j < (M < N ? M : N); j++) {
                    String t = br.readLine();
                    if (t != null)
                        buffer[j] = t;
                    else
                        break;
                }
                // Sort M elements
                Arrays.sort(buffer);

                // Write the sorted numbers to temp file
                FileWriter fw = new FileWriter(tfile + Integer.toString(i) + ".txt");
                PrintWriter pw = new PrintWriter(fw);
                for (int k = 0; k < j; k++)
                    pw.println(buffer[k]);

                pw.close();
                fw.close();
            }

            br.close();
            fr.close();

            // Now open each file and merge them, then write back to disk
            String[] topStrings = new String[slices];
            BufferedReader[] brs = new BufferedReader[slices];

            for (i = 0; i < slices; i++) {
                brs[i] = new BufferedReader(new FileReader(tfile + Integer.toString(i) + ".txt"));
                String t = brs[i].readLine();
                if (t != null)
                    topStrings[i] = t;
                else
                    topStrings[i] = "zzzzzzzzzzz";
            }

            FileWriter fw = new FileWriter("external-sorted.txt");
            PrintWriter pw = new PrintWriter(fw);

            for (i = 0; i < N; i++) {

                //Integer - index of file (sufix of temp file)
                //String - real value of that file
                Map<Integer,String> indexWordMap = new HashMap<>();

                for (j = 0; j < slices; j++) {
                    indexWordMap.put(j, topStrings[j]);
                }

                Entry<Integer,String> entryWithMinValue = Collections.min(indexWordMap.entrySet(), Map.Entry.comparingByValue());

                pw.println(entryWithMinValue.getValue());
                String t = brs[entryWithMinValue.getKey()].readLine();
                if (t != null)
                    topStrings[entryWithMinValue.getKey()] = t;
                else
                    topStrings[entryWithMinValue.getKey()] = "zzzzzzzzzzz";

            }
            for (i = 0; i < slices; i++)
                brs[i].close();

            pw.close();
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String generateInput(int n) {
        String fileName = "external-sort.txt";

        try {
            FileWriter fw = new FileWriter(fileName);
            PrintWriter pw = new PrintWriter(fw);

            for (int i = 0; i < n; i++)
                pw.println(randomString());

            pw.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public static void main(String[] args) {
        String fileName = generateInput(N);
        externalSort(fileName);
    }


    public static String randomString() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        //System.out.println(generatedString);
        return generatedString;

    }
}
