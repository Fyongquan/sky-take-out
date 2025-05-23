package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
@Api("工作台相关接口")
@Slf4j
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 查询今日运营数据
     * @return
     */
    @GetMapping("businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> businessData(){

        BusinessDataVO businessDataVO = workSpaceService.businessData();

        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> overviewOrders(){

        OrderOverViewVO orderOverViewVO = workSpaceService.overviewOrders();

        return Result.success(orderOverViewVO);
    }

    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> overviewDishes(){

        DishOverViewVO dishOverViewVO = workSpaceService.overviewDishes();

        return Result.success(dishOverViewVO);
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals(){

        SetmealOverViewVO setmealOverViewVO = workSpaceService.overviewSetmeals();

        return Result.success(setmealOverViewVO);
    }
}
