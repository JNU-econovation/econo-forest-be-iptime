package com.example.iptimeAPI.service.user.fegin;

import com.example.iptimeAPI.domain.user.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "user", url = "http://168.131.30.127:8080", fallbackFactory = FeignUserInfoFactory.class)
public interface FeignUserInfo {
    @RequestMapping(method = RequestMethod.GET, path = "/api/users/{userId}")
    UserInfoVO getUserInfo(@PathVariable("userId") Long userId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/users/token")
    UserInfoVO getUserInfoByToken(@RequestHeader(name = "Authorization") String accessToken);
}
