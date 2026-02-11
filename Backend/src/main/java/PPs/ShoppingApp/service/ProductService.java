package PPs.ShoppingApp.service;
import PPs.ShoppingApp.Dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import PPs.ShoppingApp.model.Product;
import PPs.ShoppingApp.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

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

    @Autowired
    private ProductRepo prodRepo;

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return prodRepo.findAll(pageable).map(this::toDto);
    }

    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        return prodRepo.findByCategory(category, pageable).map(this::toDto);
    }


    public List<Product> getAllProducts() {
        return prodRepo.findAll();
    }

    public Product getProdById(int id) {
        return prodRepo.findById(id).orElse(null);
    }


    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());

        return prodRepo.save(product);
    }

    public Product updateProduct( int id, Product product, MultipartFile imageFile) throws IOException {
        product.setImageData(imageFile.getBytes());
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType() );
        return prodRepo.save(product);
    }

    public void deleteProduct(int id) {
        prodRepo.deleteById(id);
    }

    public List<Product> searchprduct(String keyword) {
        return prodRepo.searchProducts(keyword);
    }
}
