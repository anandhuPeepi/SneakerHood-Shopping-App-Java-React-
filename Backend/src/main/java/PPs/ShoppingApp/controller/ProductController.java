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
    // LIST (DTO)
    // ---------------------------
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String category
    ) {
        Pageable pageable = PageRequest.of(page, size);

        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(prodService.getProductsByCategory(category, pageable));
        }

        return ResponseEntity.ok(prodService.getAllProducts(pageable));
    }

    // ---------------------------
    // GET BY ID (DTO)
    // ---------------------------
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        ProductDTO productDto = prodService.getProdByIdDto(id);

        if (productDto != null) {
            return ResponseEntity.ok(productDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // ---------------------------
    // CREATE (stores entity in DB)
    // ---------------------------
    @PostMapping("/product")
    public ResponseEntity<ProductDTO> addProduct(
            @RequestPart Product product,
            @RequestPart(required = false) MultipartFile imageFile
    ) {
        try {
            Product saved = prodService.addProduct(product, imageFile);
            // return DTO after saving
            ProductDTO dto = prodService.getProdByIdDto(saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ---------------------------
    // IMAGE (Entity internal)
    // ---------------------------
    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageProductById(@PathVariable int productId) {
        Product product = prodService.getProdByIdEntity(productId);

        if (product == null || product.getImageData() == null || product.getImageType() == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImageType()))
                .body(product.getImageData());
    }

    // ---------------------------
    // UPDATE (stores entity in DB)
    // ---------------------------
    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable int id,
            @RequestPart Product product,
            @RequestPart(required = false) MultipartFile imageFile
    ) {
        try {
            Product updated = prodService.updateProduct(id, product, imageFile);

            if (updated != null) {
                return ResponseEntity.ok("Successfully Updated");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update");
        }
    }

    // ---------------------------
    // DELETE
    // ---------------------------
    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        Product product = prodService.getProdByIdEntity(id);

        if (product != null) {
            prodService.deleteProduct(id);
            return ResponseEntity.ok("Deleted");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }

    // ---------------------------
    // SEARCH (DTO)
    // ---------------------------
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam String keyword) {
        List<ProductDTO> products = prodService.searchProductDto(keyword);
        return ResponseEntity.ok(products);
    }
}
