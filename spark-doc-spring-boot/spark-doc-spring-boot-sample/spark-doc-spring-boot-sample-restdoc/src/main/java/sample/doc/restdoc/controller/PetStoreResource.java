package sample.doc.restdoc.controller;

import info.spark.starter.basic.Result;
import info.spark.starter.rest.base.AbstractController;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import sample.doc.restdoc.entity.Order;
import sample.doc.restdoc.repository.MapBackedRepository;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 22:23
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/stores", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Stores API", description = "Store相关API")
public class PetStoreResource extends AbstractController {
    /** STORE_DATA */
    private static final StoreData STORE_DATA = new StoreData();

    /**
     * Gets order by id *
     *
     * @param orderId order id
     * @return the order by id
     * @throws NotFoundException not found exception
     * @since 1.0.0
     */
    @GetMapping("/order/{orderId}")
    @ApiOperation(
        value = "通过ID查找订单",
        response = Order.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Order not found")})
    public Result<Order> getOrderById(
        @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true)
        @PathVariable("orderId") String orderId)
        throws NotFoundException {
        return this.ok(STORE_DATA.get(Long.valueOf(orderId)));
    }

    /**
     * Place order response entity
     *
     * @param order order
     * @return the response entity
     * @since 1.0.0
     */
    @PostMapping(value = "/order")
    @ApiOperation(value = "生成一个订单", response = Order.class)
    @ApiResponses( {@ApiResponse(code = 400, message = "Invalid Order")})
    public Result<String> placeOrder(Order order) {
        STORE_DATA.add(order);
        return this.ok();
    }

    /**
     * Delete order response entity
     *
     * @param orderId order id
     * @return the response entity
     * @since 1.0.0
     */
    @DeleteMapping(value = "/order/{orderId}")
    @ApiOperation(value = "删除指定ID的订单")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"),
                           @ApiResponse(code = 404, message = "Order not found")})
    public Result<String> deleteOrder(@PathVariable String orderId) {
        STORE_DATA.delete(Long.valueOf(orderId));
        return this.ok();
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.2.4
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.15 22:16
     * @since 1.0.0
     */
    private static class StoreData extends MapBackedRepository<Long, Order> {
    }
}
