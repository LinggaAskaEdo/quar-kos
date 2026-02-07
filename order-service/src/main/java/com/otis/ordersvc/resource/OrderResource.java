package com.otis.ordersvc.resource;

import java.util.List;

import com.otis.ordersvc.dto.CreateOrderRequest;
import com.otis.ordersvc.model.Order;
import com.otis.ordersvc.service.OrderService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    public Response createOrder(CreateOrderRequest request) {
        Order created = orderService.createOrder(request.getUserId(), request.getUsername(), request.getItems());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
