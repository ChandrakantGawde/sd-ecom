package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;

import java.util.List;

public interface CartService {
    CartDTO appProductToCart(Long productId,Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cardId);

    CartDTO updateProductQuantityInCart(Long productId, int delete);
}
