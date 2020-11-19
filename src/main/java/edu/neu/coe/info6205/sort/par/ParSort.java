package edu.neu.coe.info6205.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
class ParSort {

    public static int cutoff = 1000;
    public static int threadCount = 64;
    public static ForkJoinPool myPool = new ForkJoinPool(threadCount);

    public static void sort(int[] array, int from, int to) {
        if (to - from < cutoff) Arrays.sort(array, from, to);
        else {
            CompletableFuture<int[]> parsort1 = parsort(array, from, from + (to - from) / 2); // TO IMPLEMENT
            CompletableFuture<int[]> parsort2 = parsort(array, from + (to - from) / 2, to); // TO IMPLEMENT
            //根据两个parsort的结果，进行转化后返回 According to the results of the two parsorts, return after conversion
            //xs1是parsort1的结果，xs2是parsort2的结果 xs1 is the result of parsort1, xs2 is the result of parsort2
            CompletableFuture<int[]> parsort = parsort1.thenCombine(parsort2, (xs1, xs2) -> {
                //result是辅助数组 result is an auxiliary array
                int[] result = new int[xs1.length + xs2.length];
                // TO IMPLEMENT
                int p1 = 0, p2 = 0, k = 0;
                while(p1<=xs1.length-1 && p2<=xs2.length-1){
                    if(xs1[p1] <= xs2[p2]){
                        result[k++] = xs1[p1++];
                    } else{
                        result[k++] = xs2[p2++];
                    }
                }
                while(p1<=xs1.length-1) result[k++] = xs1[p1++];
                while(p2<=xs2.length-1) result[k++] = xs2[p2++];
                return result;
            });

            parsort.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
//            System.out.println("# threads: "+ ForkJoinPool.commonPool().getRunningThreadCount());
            parsort.join();
        }
    }

    private static CompletableFuture<int[]> parsort(int[] array, int from, int to) {
        return CompletableFuture.supplyAsync(
                () -> {
                    int[] result = new int[to - from];
                    // TO IMPLEMENT
                    System.arraycopy(array, from, result, 0, result.length);
                    sort(result, 0, to - from);
                    return result;
                },myPool
        );
    }
}