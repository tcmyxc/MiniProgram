package com.tcmyxc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tcmyxc
 * @date 2021/1/6 18:14
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String productImg;
    private String productPrice;
    private String productTitle;
    private String productShop;
}
