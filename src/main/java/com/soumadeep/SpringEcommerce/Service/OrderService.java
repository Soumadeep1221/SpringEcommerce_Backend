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
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private PgVectorStore vectorStore;

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

            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
            productRepo.save(product);

            Filter.Expression filter=new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("productId"),
                    new Filter.Value(String.valueOf(product.getId()))
            );
            vectorStore.delete(filter);

            String updatedContent=String.format("""
                Product Name: %s
                Description: %s
                Brand: %s
                Category: %s
                Price: %.2f
                Release Date: %s
                Available: %s
                Stock: %s
                """,
                    product.getName(),
                    product.getDescription(),
                    product.getBrand(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getReleaseDate(),
                    product.isProductAvailable(),
                    product.getStockQuantity()
            );

            Document updatedDoc=new Document(
                    UUID.randomUUID().toString(),
                    updatedContent,
                    Map.of("productId",String.valueOf(product.getId())));

            vectorStore.add(List.of(updatedDoc));

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

        StringBuilder content=new StringBuilder();
        content.append("Order Summary: \n");
        content.append("Order ID: ").append(saveOrder.getOrderId()).append("\n");
        content.append("Customer: ").append(saveOrder.getCustomerName()).append("\n");
        content.append("Email: ").append(saveOrder.getEmail()).append("\n");
        content.append("Date: ").append(saveOrder.getOrderDate()).append("\n");
        content.append("Status: ").append(saveOrder.getStatus()).append("\n");
        content.append("Products: \n");

        for(OrderItem item:saveOrder.getOrderItems()){
            content.append("- ").append(item.getProduct().getName())
                    .append(" X ").append(item.getQuantity())
                    .append(" = ").append(item.getTotalPrice()).append("\n");
        }

        Document document=new Document(
                UUID.randomUUID().toString(),
                content.toString(),
                Map.of("orderId",String.valueOf(saveOrder.getId())));

        vectorStore.add(List.of(document));

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

    @Transactional(readOnly = true)
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
