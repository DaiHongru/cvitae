package com.freework.cvitae.controller;

import com.freework.cvitae.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author daihongru
 */
@RestController
@RequestMapping(value = "client")
public class ClientController {
    @Autowired
    private ClientService clientService;

    /**
     * 根据用户ID获取的简历信息
     *
     * @param userId
     * @return
     */
    @PostMapping("getUserCvitaeInfo")
    public Map<String, Integer> getUserCvitaeInfo(@RequestBody Integer userId) {
        return clientService.getUserCvitaeInfo(userId);
    }
}
