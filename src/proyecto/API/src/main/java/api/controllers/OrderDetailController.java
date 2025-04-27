package api.controllers;

import api.dto.OrderDetailRequest;
import api.dto.OrderDetailResponse;
import api.service.OrderDetailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-detail")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @Operation(
            operationId = "getOrderDetailById",
            summary     = "Get order detail by ID",
            description = "Retrieve an order detail record by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(orderDetailService.findById(id));
    }

    @Operation(
            operationId = "createOrderDetail",
            summary     = "Create a new order detail",
            description = "Create a new order detail entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<OrderDetailResponse> create(@RequestBody @Valid OrderDetailRequest request) {
        return ResponseEntity.ok(orderDetailService.createOrderDetail(request));
    }

    @Operation(
            operationId = "updateOrderDetail",
            summary     = "Update order detail by ID",
            description = "Update the order detail identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> update(
            @PathVariable int id,
            @RequestBody @Valid OrderDetailRequest request
    ) throws Exception {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(request, id));
    }

    @Operation(
            operationId = "deleteOrderDetail",
            summary     = "Delete order detail by ID",
            description = "Remove the order detail record identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }
}