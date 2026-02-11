package PPs.ShoppingApp.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import PPs.ShoppingApp.model.Product;
import PPs.ShoppingApp.service.ProductService;
import org.hibernate.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService prodService;

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String category
    ) {
        Pageable pageable = PageRequest.of(page, size);

        if (category != null && !category.isEmpty()) {
            return new ResponseEntity<>(
                    prodService.getProductsByCategory(category, pageable),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                prodService.getAllProducts(pageable),
                HttpStatus.OK
        );
    }



    @GetMapping("/product/{id}")
    public ResponseEntity <Product> getProductById(@PathVariable int id){

        Product product= prodService.getProdById(id);

        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        }else{
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }}

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile){

        try {
           Product product1 = prodService.addProduct(product, imageFile);
           return new ResponseEntity<>(product1, HttpStatus.OK);
        } catch (Exception e) {
            return
        new ResponseEntity<>(HttpStatus.NOT_FOUND);}
    }

    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageProductById(@PathVariable int productId) {
        Product product = prodService.getProdById(productId);

        if (product == null || product.getImageData() == null || product.getImageType() == null) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImageType()))
                .body(product.getImageData());
    }


    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable  int id,
                                                @RequestPart Product product,
                                                @RequestPart MultipartFile imageFile){

        Product product1 = null;
        try {
            product1 = prodService.updateProduct(id, product,imageFile);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to update da", HttpStatus.BAD_REQUEST);
        }

       if (product1 != null){
           return new ResponseEntity<>("Successfully Updated da", HttpStatus.OK);
       }
       else {
           return new ResponseEntity<>("Failed to update da", HttpStatus.BAD_REQUEST);
       }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> DeleteProduct(@PathVariable int id){

       Product product = prodService.getProdById(id);
       if (product != null)
       {
            prodService.deleteProduct(id);
           return new ResponseEntity<>("Deleted", HttpStatus.OK);
       }
       else
       {

           return new ResponseEntity<>("Product not found", HttpStatus.OK);
       }
    }

    @GetMapping("products/search")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String keyword){
        List<Product> products = prodService.searchprduct(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK );
        }
}
