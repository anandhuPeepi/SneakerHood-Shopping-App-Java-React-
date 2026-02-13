package PPs.ShoppingApp.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(int id) {
        super("Product not found with id: " + id);
    }
}