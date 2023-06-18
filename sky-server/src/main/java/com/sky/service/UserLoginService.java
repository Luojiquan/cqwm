package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserLoginService {
    User login(UserLoginDTO loginDTO);
}
