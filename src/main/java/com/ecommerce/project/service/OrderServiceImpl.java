package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartService cartService;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
       // Pre order
        Cart cart = cartRepository.findCartByEmail(emailId);
        if( cart == null)
            throw new ResourceNotFoundException("Cart", "email", emailId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", addressId));

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty())
            throw new APIException("Cart is empty");

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);

            orderItems.add(orderItem);
        }

         orderItems = orderItemRepository.saveAll(orderItems);

       // Post order
        cart.getCartItems().forEach( cartItem -> {
            int quantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            cartService.deleteProductFromCart(cart.getCartId(), cartItem.getProduct().getProductId());
        });

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(orderItem ->
                orderDTO.getOrderItems().
                        add(modelMapper.map(orderItem, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
