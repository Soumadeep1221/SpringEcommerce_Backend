package com.soumadeep.SpringEcommerce.Service;

import com.soumadeep.SpringEcommerce.Model.DTO.OrderItemRequest;
import com.soumadeep.SpringEcommerce.Model.DTO.OrderItemResponse;
import com.soumadeep.SpringEcommerce.Model.DTO.OrderRequest;
import com.soumadeep.SpringEcommerce.Model.DTO.OrderResponse;
import com.soumadeep.SpringEcommerce.Model.Order;
import com.soumadeep.SpringEcommerce.Model.OrderItem;
import com.soumadeep.SpringEcommerce.Model.Product;
import com.soumadeep.SpringEcommerce.Repo.OrderRepo;
import com.soumadeep.SpringEcommerce.Repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private OrderRepo orderRepo;

    public OrderResponse placeOrder(OrderRequest orderRequest) {

        Order order=new Order();
        String orderId="ORD"+ UUID.randomUUID().toString().substring(0,8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomerName(orderRequest.customerName());
        order.setEmail(orderRequest.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());

        List<OrderItem> orderItems=new ArrayList<>();
        for(OrderItemRequest itemReq:orderRequest.items())
        {
            Product product=productRepo.findById(itemReq.productId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));

            if(product.getStockQuantity() < itemReq.quantity()){

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Insufficient stock for product: "
                        + product.getName()
                        + ". Available: "
                        + product.getStockQuantity()
                );
            }

            product.setStockQuantity(product.getStockQuantity()- itemReq.quantity());
            productRepo.save(product);

            OrderItem orderItem=OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())))
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        Order saveOrder=orderRepo.save(order);

        List<OrderItemResponse> itemResponses=new ArrayList<>();
        for(OrderItem item:order.getOrderItems())
        {
            OrderItemResponse orderItemResponse=new OrderItemResponse(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getTotalPrice());
            itemResponses.add(orderItemResponse);
        }

        OrderResponse orderResponse=new OrderResponse(
                saveOrder.getOrderId(),
                saveOrder.getCustomerName(),
                saveOrder.getEmail(),
                saveOrder.getStatus(),
                saveOrder.getOrderDate(),
                itemResponses);

        return orderResponse;
    }

    public List<OrderResponse> getAllOrderResponses() {

        List<Order> orders=orderRepo.findAll();

        List<OrderResponse> orderResponses=new ArrayList<>();

        for(Order order:orders)
        {
            List<OrderItemResponse> itemResponses = order
                    .getOrderItems()
                    .stream()
                    .map(item -> new OrderItemResponse(
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getTotalPrice()
                    ))
                    .toList();

            OrderResponse orderResponse=new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    itemResponses);

            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }
}
