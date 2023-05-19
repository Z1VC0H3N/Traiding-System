package domain.user;

import domain.store.product.Product;
import org.json.JSONObject;
import utils.infoRelated.ProductInfo;

import java.util.HashMap;
import java.util.List;

public interface User {

    public void addProductToCart(int storeId, ProductInfo product, int quantity) throws Exception;
    public void removeProductFromCart(int storeId, int productId) throws Exception;
    public void changeQuantityInCart(int storeId, ProductInfo product, int change) throws Exception;
    public List<Basket> getCartContent();
    public List<JSONObject> getCartJson();
    public ShoppingCart getShoppingCart();
    public void purchaseMade(int orderId, double totalPrice);
    public void emptyCart();
}
