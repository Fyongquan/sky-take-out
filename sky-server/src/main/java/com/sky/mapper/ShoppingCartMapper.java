package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据ShoppingCart查找ShoppingCart
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> getByShoppingCart(ShoppingCart shoppingCart);

    /**
     * 更新购物车数量
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 新增到购物车
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime}) ")
    void addShoppingCart(ShoppingCart shoppingCart);

    /**
     * 批量新增到购物车
     * @param shoppingCarts
     */
    void addBatchShoppingCart(List<ShoppingCart> shoppingCarts);

    /**
     * 删除购物车中的商品
     * @param shoppingCart
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(ShoppingCart shoppingCart);

    /**
     * 清空购物车商品
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
}
