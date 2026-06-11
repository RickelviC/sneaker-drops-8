package com.pluralsight.sneakerdrops.service;

import org.springframework.stereotype.Service;

@Service
public class DropService {
    public DropService() {
    }
    public String getStatus() {
        return "Sneaker drops loading...";
    }
}
