package com.anonymoushippo.palette;

import java.util.ArrayList;
import java.util.Random;

public class MyUtility {

    public static ArrayList<Integer> Random(int bound, int n){
        int[] a = new int[n];
        ArrayList<Integer> result;

        Random random = new Random();

        for(int i = 0; i < n; i++){
            a[i] = random.nextInt(bound);

            for(int j = 0; j < i; j++){
                if(a[i] == a[j]){
                    i--;
                }
            }
        }

        result = new ArrayList<>();

        for (int i = 0; i < n; i++){
            result.add(a[i]);
        }

        return result;
    }
}

