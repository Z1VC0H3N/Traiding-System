package domain.store.storeManagement;

import domain.store.discount.AbstractDiscount;
import domain.store.discount.Discount;
import domain.store.discount.discountDataObjects.DiscountDataObject;
import domain.store.discount.discountDataObjects.PredicateDataObject;
import domain.store.discount.predicates.DiscountPredicate;
import domain.user.Basket;
import domain.user.Member;
import domain.user.ShoppingCart;
import org.json.JSONObject;
import utils.infoRelated.ProductInfo;
import utils.Filter.ProductFilter;
import utils.infoRelated.StoreInfo;
import utils.messageRelated.ProductReview;
import utils.messageRelated.Question;
import utils.messageRelated.StoreReview;
import utils.orderRelated.Order;
import domain.store.product.Product;
import utils.messageRelated.Message;
import utils.infoRelated.OrderInfo;
import utils.infoRelated.Receipt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreController {

    public ConcurrentHashMap<Integer, Store> storeList; //storeid, Store
    AtomicInteger storescounter;
    AtomicInteger productIDs = new AtomicInteger(0);
    ConcurrentHashMap<Integer, Product> products; //for fast access

    public StoreController() {
        storeList = new ConcurrentHashMap<>();
        storescounter = new AtomicInteger(0);
        products = new ConcurrentHashMap<>();
    }

    /**
     * adds a new product to a store.
     */
    public synchronized int addProduct(int storeid, String name, String desc, int price, int quantity) throws Exception {
        Store st;
        //Product prod;
        int id = -1;
        if ((st = getStore(storeid)) != null) {
            Product p = st.addNewProduct(name, desc, productIDs,price,quantity);
            p.replaceQuantity(quantity);
            addToProducts(p.clone());
            id = p.getID();
        }
        return id;
    }

    public synchronized int addProduct(int storeid, String name, String desc, int price, int quantity, String img) throws Exception {
        Store st;
        //Product prod;
        int id = -1;
        if ((st = getStore(storeid)) != null) {
            Product p = st.addNewProduct(name, desc, productIDs,price,quantity, img);
            p.replaceQuantity(quantity);
            addToProducts(p.clone());
            id = p.getID();
        }
        return id;
    }
    public void addToCategory(int storeId, int productId, List<String> categories) throws Exception{
        Store st;
        if((st = getStore(storeId))!= null){
            st.addToCategories(productId,categories);
        }
    }

    private Product getExistingProductByName(String prodName) {
        for (Product p : products.values()) {
            if (p.getName().equalsIgnoreCase(prodName)) {
                return p;
            }
        }
        return null;
    }

    public int addQuestion(Question q) throws Exception
    {
        Store store = getActiveStore(q.getStoreId());
        return store.addQuestion(q);
    }
    public Store getActiveStore(int storeID) throws Exception
    {
        Store store = getStore(storeID);
        if (store.isActive()){return store;}
        throw new Exception("Store is not active at the moment");
    }

    public Store openStore(String desc, Member user) {
        Store store = new Store(storescounter.getAndIncrement(), desc, user);
        storeList.put(store.getStoreId(), store);
        return store;
    }

    public Store openStore(String name, String desc, String img, Member user) {
        Store store = new Store(storescounter.getAndIncrement(), name, desc, img, user);
        storeList.put(store.getStoreId(), store);
        return store;
    }

    /**
     * @return the store creator id if the store or order doesn't exist return -1
     */
    public int writeReviewForStore(StoreReview message) throws Exception {
        Store store = getActiveStore(message.getStoreId());
        return store.addReview(message.getOrderId(), message);
    }

    public int writeReviewForProduct(ProductReview message) throws Exception {
        Store store = getActiveStore(message.getStoreId());
        return store.addProductReview(message);
    }

    public int calculatePrice(ShoppingCart cart) throws Exception{
        int total = 0;
        for(Basket basket : cart.getBaskets()){
            total += getStore(basket.getStoreId()).calculatePrice(basket);
        }
        return total;
    }
    public void setPrices(Order or) throws Exception {
        List<ProductInfo> shoppingCart = or.getProductsInStores();
        HashMap<Integer,HashMap<Integer,Integer>> prices = or.getPrices();
        for(ProductInfo product : shoppingCart){
            if(!prices.containsKey(product.getStoreId())){
                prices.put(product.getStoreId(),new HashMap<>());
            }
            int quantity = product.quantity;
            Store s = getStore(product.getStoreId());
            Product p = s.getInventory().getProduct(product.getId());
            prices.get(product.getStoreId()).put(product.getId(),p.getPrice() * quantity);
        }
    }
//    /**
//     * checks if the purchasing is possible
//     *
//     * @param shoppingcart the client shopping cart
//     * @return if the purchasing is possible returns the total price else return -1
//     */
//    public int createOrder(HashMap<Integer, HashMap<Integer, Integer>> shoppingcart) {
//        int totalprice = 0;
//        for (Integer storeid : shoppingcart.keySet()) {
//            Store store = getStore(storeId
;//            try {
//                totalprice += store.createOrder(shoppingcart.get(storeid));
//            } catch (Exception e) {
//                return -1;
//            }
//        }
//        return totalprice;
//    }

    /**
     * performs the purchasing
     * @param shoppingCart the client shopping cart
     * @return if successful returns the store owners ids else null
     */
    public synchronized Set<Integer> purchaseProducts(ShoppingCart shoppingCart, Order order) throws Exception {
        Set<Integer> storeOwnersIDS = new HashSet<>();
        //should apply discounts here
        for (Basket b : shoppingCart.getBaskets()) {
            Store store = storeList.get(b.getStoreId());
            store.handleDiscount(order);
            if (!(store.makeOrder(b))) {
                return null;
            }
            storeOwnersIDS.add(store.getCreator());
            store.addOrder(order);
        }
        return storeOwnersIDS;
    }

    /**
     * adds the product to the store controller to view which products exists in the market and create
     *
     * @param prod
     * @return
     */
    private synchronized boolean addToProducts(Product prod) {
        if (products.containsKey(prod.getID())) {
            return false;
        }
        products.put(prod.getID(), prod);
        return true;
    }

    public Store getStore(int storeId) throws Exception{
        if(storeList.containsKey(storeId))
            return storeList.get(storeId);
        throw new Exception("the storeId given does not belong to any store in the system");
    }


    public Store createNewStore(Member creator, String description) {
        Store store = new Store(storescounter.get(), description, creator);
        int storeid = storescounter.getAndIncrement();
        storeList.put(storeid, store);
        return store;
    }

//    public ArrayList<Product> getProductByCategories(ArrayList<String> categories) {
//        ArrayList<Product> products = new ArrayList<>();
//        for (Store store :
//                storeList.values()) {
//            products.addAll(store.getProductByCategories(categories));
//        }
//        return products;
//    }

//    public ArrayList<Product> getProductByKeyWords(ArrayList<String> keywords) {
//        ArrayList<Product> products = new ArrayList<>();
//        for (Store store :
//                storeList.values()) {
//            products.addAll(store.getProductByKeywords(keywords));
//        }
//        return products;
//    }

    public String getProductName(int storeId, int productId) throws Exception {
        Store store = getStore(storeId);
        if (store != null && store.isActive()) {
            return store.getProductName(productId);
        } else {
            throw new Exception("store doesnt Exist or Open");
        }
    }

    public ArrayList<String> checkMessages(int storeID) throws Exception {
        Store store = getActiveStore(storeID);
        return store.checkMessages();
//        Store store = getStore(storeID);
//        if (store != null && store.isActive()) {
//            return store.checkMessages();
//        } else {
//            throw new Exception("store doesnt Exist or Open");
//        }
    }


    public List<Message> getQuestions(int storeId) throws Exception {
        Store store = getActiveStore(storeId);
        return store.getQuestions();
    }

    public void answerQuestion(int storeId, int questionId, String answer) throws Exception{
        Store store = getActiveStore(storeId);
        store.answerQuestion(questionId, "you got an answer for question: " + questionId + " to store: " + storeId
                +" the answer is: " + answer);
    }

    public List<OrderInfo> getStoreOrderHistory(int storeId) throws Exception {
        Store store = getActiveStore(storeId);
        return store.getOrdersHistory();
//        Store store = getStore(storeId);
//        if (store != null && store.isActive())
//        {
//            return store.getOrdersHistory();
//        }
//        throw new Exception("store doesnt Exist or Open");
    }

    public AppHistory getAppointments(int storeId) throws Exception{
        Store store = getActiveStore(storeId);
        return store.getAppHistory();
    }


    public Set<Integer> closeStorePermanently(int storeId) throws Exception {
        Store store = getStore(storeId);
        if(store != null){
            storeList.remove(storeId);
            return store.getUsersInStore();
        }
        else
            throw new Exception("the store does not exist in the system");
    }

    public void removeProduct(int storeId, int productId) throws Exception  {
        Store st;
        if((st = storeList.get(storeId))!=null){
            st.removeProduct(productId);
        }
        else{
            throw new Exception("Store id doesn't exist.");
        }
    }

    public void updateProduct(int storeId, int productId, List<String> categories, String name, String description,
                              int price, int quantity, String img) throws Exception {
        Store st;
        if((st = storeList.get(storeId))!=null){
            st.updateProduct(productId, categories, name,  description, price, quantity, img);
        }
        else{
            throw new Exception("Store id doesn't exist.");
        }
    }

    public List<StoreReview> viewReviews(int storeId) throws Exception {
        Store store = getActiveStore(storeId);
        return store.getStoreReviews();
    }

    public ArrayList<String> showFilterOptions() {
        return new ProductFilter().getNames();
    }

    public ArrayList<ProductInfo> filterBy(HashMap<String,String> filterOptions) {
        ArrayList<ProductInfo> result = new ArrayList<>();
        for(Store st : storeList.values()){
            result.addAll(st.filterBy(filterOptions));
        }
        return result;
    }

    public List<ProductInfo> getProducts(int storeId) throws Exception {
        Store s = getStore(storeId);
        if(s != null){
            return s.getProducts();
        }
        else
            throw new Exception("the id given does not match any store");
    }

    public StoreInfo getStoreInformation(int storeId) throws Exception{
        Store store = getStore(storeId);
        return store.getStoreInformation();

    }

    public ConcurrentHashMap<Integer, Store> getStoresInformation() {
        return storeList;
    }

    public void purchaseMade(Receipt receipt) throws Exception {
        List<Basket> cart = receipt.getContent();
        for(Basket b : cart){
            int storeId = b.getStoreId();
            Store store = getStore(storeId);
            for(ProductInfo product: b.getContent()){
                int productId = product.id;
                store.setProductQuantity(productId, store.getQuantityOfProduct(productId) - product.quantity);
            }
        }
    }

    public List<ProductInfo> getAllProducts() {
        List<ProductInfo> products = new ArrayList<>();
        for(Store s : storeList.values()){
            if(s.isActive())
                products.addAll(s.getProducts());
        }
        return products;
    }

    public void checkProductInStore(int storeId, int productId) throws Exception{
        Store s = getStore(storeId);
        s.checkProductInStore(productId);
    }

    public void setStoreAttributes(int storeId, String name, String description, String img) throws Exception{
        Store s = getStore(storeId);
        s.setStoreAttributes(name, description, img);
    }
    public ArrayList<PredicateDataObject> parsePredicateData(ArrayList<String> predData){
        ArrayList<PredicateDataObject> predicates = new ArrayList<>();
        for(String data : predData){
            JSONObject dataJson = new JSONObject(data);
            String predTypeStr = dataJson.getString("predType");
            DiscountPredicate.PredicateTypes predType = DiscountPredicate.PredicateTypes.valueOf(predTypeStr);
            String params = dataJson.getString("params");
            String composureStr = dataJson.getString("composore");
            DiscountPredicate.composore composore = DiscountPredicate.composore.valueOf(composureStr);
            predicates.add(new PredicateDataObject(predType,params,composore));
        }
        return predicates;
    }

    public void changeRegularDiscount(int storeId, int prodId, int percentage, String discountType, String discountedCategory, List<String> predicatesLst) throws Exception {
        Store s = getActiveStore(storeId);
        AbstractDiscount.discountTypes discountTypeEnum = AbstractDiscount.discountTypes.Store;
        if (Objects.equals(discountType.toLowerCase(), "product")){discountTypeEnum = AbstractDiscount.discountTypes.Product;}
        if (Objects.equals(discountType.toLowerCase(), "category")){discountTypeEnum = AbstractDiscount.discountTypes.Category;}
        s.addDiscount(new DiscountDataObject(percentage,discountTypeEnum,prodId, discountedCategory, parsePredicateData(new ArrayList<>(predicatesLst))));

    }

    public int getStoreId(String storeName) throws Exception{
        for(Store s : storeList.values())
            if(s.getName().equals(storeName))
                return s.getStoreId();
        throw new Exception("the name does not belong to any store");
    }
}
