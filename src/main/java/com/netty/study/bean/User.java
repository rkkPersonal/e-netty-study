package com.netty.study.bean;

import lombok.*;

/**
 * @author Steven
 * @date 2022年10月07日 23:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class User {

    private String username;

    private String data;

    private String hobby;

}
