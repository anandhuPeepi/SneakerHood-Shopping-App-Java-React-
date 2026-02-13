package PPs.ShoppingApp.controller;

import PPs.ShoppingApp.Dto.ProductDTO;
import PPs.ShoppingApp.model.Product;
import PPs.ShoppingApp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService prodService;

    // ---------------------------
    // LIST (DTO) + Pagination + Optional Category Filter
    // ---------------------------
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String category
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // If category is provided, filter results by category
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(prodService.getProductsByCategory(category, pageable));
        }

        // Otherwise return all products with pagination
        return ResponseEntity.ok(prodService.getAllProducts(pageable));
    }

    // ---------------------------
    // GET BY ID (DTO)
    // ---------------------------
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {

        // If product does not exist, ProductNotFoundException is thrown
        // GlobalExceptionHandler converts it into a 404 JSON response
        return ResponseEntity.ok(prodService.getProdByIdDto(id));
    }

    // ---------------------------
    // CREATE (Entity saved, return DTO)
    // ---------------------------
    @PostMapping("/product")
    public ResponseEntity<ProductDTO> addProduct(
            @RequestPart Product product,
            @RequestPart(required = false) MultipartFile imageFile
    ) throws IOException {

        // Save product and optional image
        Product saved = prodService.addProduct(product, imageFile);

        // Return DTO to avoid exposing imageData field
        ProductDTO dto = prodService.getProdByIdDto(saved.getId());

        // 201 CREATED indicates successful resource creation
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // ---------------------------
    // IMAGE (Entity internal)
    // ---------------------------
    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageProductById(@PathVariable int productId) {

        // If product does not exist → 404 handled globally
        Product product = prodService.getProdByIdEntity(productId);

        // If image is not available → return 204 No Content
        if (product.getImageData() == null || product.getImageType() == null) {
            return ResponseEntity.noContent().build();
        }

        // Return image with correct Content-Type header
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImageType()))
                .body(product.getImageData());
    }

    // ---------------------------
    // UPDATE (Entity saved)
    // ---------------------------
    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable int id,
            @RequestPart Product product,
            @RequestPart(required = false) MultipartFile imageFile
    ) throws IOException {

        // If product does not exist → 404 handled globally
        prodService.updateProduct(id, product, imageFile);

        // 200 OK indicates successful update
        return ResponseEntity.ok("Successfully Updated");
    }

    // ---------------------------
    // DELETE
    // ---------------------------
    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {

        // If product does not exist → 404 handled globally
        prodService.deleteProduct(id);

        // Return success response after deletion
        return ResponseEntity.ok("Deleted");
        // Optional improvement: return ResponseEntity.noContent().build(); for 204
    }

    // ---------------------------
    // SEARCH (DTO)
    // ---------------------------
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam String keyword) {

        // Returns matching products
        // Empty list is valid response (not an error)
        return ResponseEntity.ok(prodService.searchProductDto(keyword));
    }
}
