package org.limit_breaker.UTILITIES;

/**
 * 這些只是一些數字, 你可以修改如果你看得明白內容的話
 * These are just some numbers; you can modify them if you understand the content.
 */

public class MainNumbers {
    public static final int MAX_CHUNK = 134217727;    //最大區塊
    public static final int MAX_BLOCK = 2147483647;   //最大方塊
    public static final int MIN_BLOCK_C = -2147483648;
    public static final int MAX_PLAYABLE_BLOCK = MAX_BLOCK - 16;  //最大可遊玩方塊
    public static final int MAX_PLAYABLE_CHUNK = MAX_CHUNK - 1;   //最大可遊玩的區塊
    public static final int MINESHAFT_LIMIT_CHUNK = 134217721;    //廢棄礦坑最大的區塊
}
