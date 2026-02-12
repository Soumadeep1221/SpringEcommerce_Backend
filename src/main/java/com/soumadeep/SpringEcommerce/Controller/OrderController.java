package com.soumadeep.SpringEcommerce.Controller;

import com.soumadeep.SpringEcommerce.Model.DTO.OrderRequest;
import com.soumadeep.SpringEcommerce.Model.DTO.OrderResponse;
import com.soumadeep.SpringEcommerce.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest)
    {
        OrderResponse orderResponse=orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders()
    {
        List<OrderResponse> responses=orderService.getAllOrderResponses();
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }
}
