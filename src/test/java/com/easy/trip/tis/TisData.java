package com.easy.trip.tis;

/**
 * @author Steven
 * @date 2022年09月12日 5:16
 */
public interface TisData<T, A, B> {

    <T1, T2, T3> Tis<T, A, B> show1(T t1, A t2, B t3);

    default <T> void show2(T t) {

    }

    default <T> T show3(T t) {
        return t;
    }


}
