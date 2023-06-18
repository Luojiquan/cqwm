package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserLoginService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;
    @Override
    public User login(UserLoginDTO loginDTO) {
        //1,调用微信的 登录接口
        String openid = getOpenId(loginDTO);

        //2,判断openid是否为空,如果为空,则表示登录失败,抛出业务异常
        if(null == openid){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //3,判断是否是新用户
        User user = userMapper.getUserByOpenId(openid);
        if(null == user){
            //说明当前用户是  新用户,  需要完成注册(添加)
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);

        }

        return user;
    }

    private String getOpenId(UserLoginDTO loginDTO) {
        HashMap<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code", loginDTO.getCode());
        map.put("grant_type","authorization_code");
        //调用微信登录的接口
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
