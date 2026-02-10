package PPs.ShoppingApp.service;
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

    @Autowired
    private ProductRepo prodRepo;

    public Page<Product> getAllProducts(Pageable pageable) {
        return prodRepo.findAll(pageable);
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
