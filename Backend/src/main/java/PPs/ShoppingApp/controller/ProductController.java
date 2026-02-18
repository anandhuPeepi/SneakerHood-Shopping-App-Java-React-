package PPs.ShoppingApp.controller;

import PPs.ShoppingApp.Dto.ProductDTO;
import PPs.ShoppingApp.model.Product;
import PPs.ShoppingApp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService prodService;

    public ProductController(ProductService prodService) {
        this.prodService = prodService;
    }

    // ✅ CREATE (multipart: product JSON + optional image) + VALIDATION
    @PostMapping("/product")
    public ResponseEntity<ProductDTO> addProduct(
            @Valid @RequestBody ProductDTO product
    ) throws IOException {

        Product saved = prodService.addProduct(product, null); // no image
        ProductDTO dto = prodService.getProdByIdDto(saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    // ✅ UPDATE (multipart: product JSON + optional image) + VALIDATION
    @PutMapping("/product/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable int id,
            @Valid @RequestBody ProductDTO product
    ) throws IOException {

        Product updated = prodService.updateProduct(id, product, null); // no image
        ProductDTO dto = prodService.getProdByIdDto(updated.getId());
        return ResponseEntity.ok(dto);
    }


    // ✅ GET ALL (DTO list)
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(prodService.getAllProductsDto());
    }

    // ✅ GET ALL with Pagination (DTO page)
    @GetMapping("/products/page")
    public ResponseEntity<Page<ProductDTO>> getProductsPage(Pageable pageable) {
        return ResponseEntity.ok(prodService.getAllProducts(pageable));
    }

    // ✅ GET BY ID (DTO)
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        return ResponseEntity.ok(prodService.getProdByIdDto(id));
    }

    // ✅ DELETE
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        prodService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ SEARCH (DTO list) - matches your service method name
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(prodService.searchProductDto(keyword));
    }

    // ✅ FILTER BY CATEGORY (DTO page) - matches your service signature
    @GetMapping("/products/category/{category}")
    public ResponseEntity<Page<ProductDTO>> getByCategory(@PathVariable String category, Pageable pageable) {
        return ResponseEntity.ok(prodService.getProductsByCategory(category, pageable));
    }

    // ✅ IMAGE endpoint (ENTITY) - use your public service method
    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageProductById(@PathVariable int productId) {

        Product product = prodService.getProdByIdEntity(productId);
        byte[] image = product.getImageData();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImageType()))
                .body(image);
    }
}
