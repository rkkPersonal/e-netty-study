package com.easy.trip.tis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Steven
 * @date 2022年09月12日 5:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tis <T,A,B>{

    private T t;
    private A a;
    private B b;



}
