package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信用户登陆
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openId = getOpenId(userLoginDTO.getCode());
        // 判断OpenId是否为空，为空表示登陆失败，抛出异常
        if (openId == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断是否为新用户
        User user = userMapper.getByOpenId(openId);

        //若是，自动注册 - userMapper
        if (user == null) {
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            // 注意要回返id, by useGeneratedKeys
            userMapper.insert(user);
        }

        return user;
    }

    /**
     * 调用微信接口服务，获取用户的openid
     * @param code
     * @return
     */
    private String getOpenId(String code) {
        // 调用微信登陆接口，获取用户openId
        Map<String,String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openId = jsonObject.getString("openid");

        return openId;
    }

}
