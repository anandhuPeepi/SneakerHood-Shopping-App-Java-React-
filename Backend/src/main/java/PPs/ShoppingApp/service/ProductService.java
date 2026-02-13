package PPs.ShoppingApp.service;

import PPs.ShoppingApp.Dto.ProductDTO;
import PPs.ShoppingApp.exception.ProductNotFoundException;
import PPs.ShoppingApp.model.Product;
import PPs.ShoppingApp.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo prodRepo;

    private Product getProductOrThrow(int id) {
        return prodRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }


    // --------------------------
    // Entity -> DTO mapper
    // --------------------------
    private ProductDTO toDto(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setBrand(p.getBrand());
        dto.setPrice(p.getPrice());
        dto.setCategory(p.getCategory());
        dto.setReleaseDate(p.getReleaseDate());
        dto.setProductAvailable(p.isProductAvailable());
        dto.setStockQuantity(p.getStockQuantity());
        dto.setImageName(p.getImageName());
        dto.setImageType(p.getImageType());



        return dto;
    }

    // --------------------------
    // READ (DTO)
    // --------------------------
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return prodRepo.findAll(pageable).map(this::toDto);
    }

    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        return prodRepo.findByCategory(category, pageable).map(this::toDto);
    }

    public List<ProductDTO> getAllProductsDto() {
        return prodRepo.findAll().stream().map(this::toDto).toList();
    }

    public ProductDTO getProdByIdDto(int id) {
        return toDto(getProductOrThrow(id));
    }



    public List<ProductDTO> searchProductDto(String keyword) {
        return prodRepo.searchProducts(keyword).stream().map(this::toDto).toList();
    }

    // --------------------------
    // INTERNAL (Entity) - for image endpoint etc.
    // --------------------------
    public Product getProdByIdEntity(int id) {
        return getProductOrThrow(id);
    }


    // --------------------------
    // CREATE / UPDATE / DELETE (Entity in DB)
    // --------------------------
    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageName(imageFile.getOriginalFilename());
            product.setImageType(imageFile.getContentType());
            product.setImageData(imageFile.getBytes());
        }
        return prodRepo.save(product);
    }

    public Product updateProduct(int id, Product product, MultipartFile imageFile) throws IOException {

        Product existing = getProductOrThrow(id);

        // Update normal fields
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setBrand(product.getBrand());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        existing.setReleaseDate(product.getReleaseDate());
        existing.setProductAvailable(product.isProductAvailable());
        existing.setStockQuantity(product.getStockQuantity());

        // Update image only if a new image is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setImageData(imageFile.getBytes());
            existing.setImageName(imageFile.getOriginalFilename());
            existing.setImageType(imageFile.getContentType());
        }

        return prodRepo.save(existing);
    }


    public void deleteProduct(int id) {
        Product existing = getProductOrThrow(id);
        prodRepo.delete(existing);
    }

}
