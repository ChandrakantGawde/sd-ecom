package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${product.image}")
    private String path;

    ProductResponse productResponse = new ProductResponse();
    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();
        for (Product product: products){
            if(product.getProductName().equals(productDTO.getProductName())){
                isProductNotPresent = false;
                break;
            }
        }

        if(isProductNotPresent){
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - (product.getDiscount() * 0.01 ) * product.getPrice();
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        } else {
            throw new APIException("Product already exist!!");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findAll(pageDetails);
        List<Product> products = productsPage.getContent();

        List<ProductDTO> productDTO = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        productResponse.setContent(productDTO);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findByCategoryOrderByPriceAsc(pageDetails, category);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty())
            throw new APIException("Products not found with category: "+ category.getCategoryName());

        List<ProductDTO> productDTO  = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        if (products.isEmpty())
            throw new APIException("No products exist for given category");

        productResponse.setContent(productDTO);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findByProductNameLikeIgnoreCase(pageDetails, keyword);

        List<Product> products = productsPage.getContent();

        if(products.isEmpty())
            throw new APIException("Products not found with keyword: "+ keyword);

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow( ()-> new ResourceNotFoundException("Product", "productId", productId));
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product  = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
         productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile file) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        String fileName = fileService.uploadImage(path, file);

         product.setImage(fileName);
        Product product1 = productRepository.save(product);
        return modelMapper.map(product1, ProductDTO.class);
    }

}
