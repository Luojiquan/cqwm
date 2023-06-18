package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关的接口")
public class ShopController {
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    // 获取店铺的营业状态
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result getStatus(){

        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺的营业状态, {}",status == 1 ? "营业中":"打烊中");
        return Result.success(status);
    }




    // 设置店铺的营业状态
    @PutMapping("/{status}")
    @ApiOperation("设置店铺的营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态, {}",status == 1 ? "营业中":"打烊中");

        // 把营业状态存储到redis中去   key名称叫: SHOP_STATUS
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();

    }


}
