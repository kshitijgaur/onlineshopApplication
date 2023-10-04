package com.kshitij.inventoryservice.controller;

import com.kshitij.inventoryservice.dto.InventoryResponse;
import com.kshitij.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

// http://localhost:8082/api/inventory?skuCode=iphone13&skuCOde=iphone13-red
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode ){
        return inventoryService.isInStock(skuCode);

    }
}
