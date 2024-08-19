package com.sky.controller.admin;

import com.sky.annotation.AutoFill;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

    public final static String KEY = "SHOP_STATUS";

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("修改店铺营业状态")
    public Result changeStatus(@PathVariable Integer status) {

        log.info("正在修改营业状态为：{}", status==1 ? "营业中" : "打烊中");

        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }


    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取营业状态为：{}", (status == 1) ? "营业中" : "查询中");
        return Result.success(status);
    }


}
