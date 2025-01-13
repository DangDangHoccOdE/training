package com.luvina.training_final.SpringBootProject.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MyServices {

    @Cacheable("myCache") // Tên cache trùng với alias trong file ehcache.xml
    public String getData(String id) {
        // Xử lý tốn thời gian
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Data for ID: " + id;
    }
}

