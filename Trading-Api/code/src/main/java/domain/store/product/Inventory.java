package domain.store.product;


import data.PositionInfo;
import utils.Filter.ProductFilter;
import utils.ProductInfo;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inventory {

    private static final int MAXRATING = 5;
    ConcurrentHashMap<Integer, Product> productList; // <id, product>
    // ConcurrentHashMap<Product, ArrayList<String>> categories;
    ConcurrentHashMap<String,ArrayList<Integer>> categories; // <Category String,<List<ProductID>>
    ConcurrentHashMap<Product, CopyOnWriteArrayList<Integer>> productgrading;

    // AtomicInteger prod_id = new AtomicInteger();
    public Inventory(){
        productList = new ConcurrentHashMap<>();
        categories = new ConcurrentHashMap<>();
        productgrading = new ConcurrentHashMap<>();
    }

    /**
     *
     * @param name String
     * @param description String
     * @param prod_id AtomicInteger
     * @param price int
     */
    public synchronized Product addProduct(String name,String description,AtomicInteger prod_id,int price) throws Exception {
        Product p = null;
        if(getProductByName(name)==null){
            int id = prod_id.getAndIncrement();
            p = new Product(id,name,description);
            p.setPrice(price);
            for(Product product : productList.values())
                if(p.getName().equals(product.getName()) && p.getDescription().equals(product.getDescription())) {
                    prod_id.getAndDecrement();
                    throw new Exception("the product already exists in the system, aborting add");
                }
            productList.put(id,p);
// categories.put(p,new ArrayList<>());
        }
        return p;
    }
    public synchronized Product addProduct(Product p) throws Exception{
        if(getProductByName(p.name) == null) productList.put(p.getID(), p);
        return p;
    }

    /**
     * returns all related categories for productId.
     * @param productId
     * @return ArrayList<Categories>
     */
    public synchronized ArrayList<String> getProductCategories(int productId){
        ArrayList<String> relatedCategories = new ArrayList<>();
        for(String category : categories.keySet()){
            if(categories.get(category).contains(productId)){
                relatedCategories.add(category);
            }
        }
        return relatedCategories;
    }
    /**
     * gets product id and return list of the grading the product got by buyers
     */
    public ArrayList<Integer> getProductReviews(int productID) throws Exception {
        Product p = getProduct(productID);
        if (p != null){return new ArrayList<>(productgrading.get(p));}
        throw new Exception("product doesnt exist G");
    }
    public void setDescription(int prodID, String desc)  throws Exception{
        Product p = getProduct(prodID);
        p.setDescription(desc);

    }
    public synchronized void setPrice(int prodID, int price) throws Exception {
        if (price <= 0)
        {
            throw new Exception("price must be positive");
        }
        Product p = getProduct(prodID);
        if(p != null ){
            p.setPrice(price);
            return;
        }
        throw new Exception("product doesnt exist");
    }

    public void addQuantity(int prodID,int quantity) throws Exception{
        Product p = getProduct(prodID);
        p.setQuantity(quantity);
    }

    public Product getProduct(Integer productID) throws Exception{
        if(productList.containsKey(productID)){
            return productList.get(productID);
        }
        throw new Exception("Product not found, ID: "+productID);
    }

    public ArrayList<String> getAllCategories(){
        return new ArrayList<>(categories.keySet());
    }


    /**
     * @param prodID INTEGER
     * @throws Exception if doesn't exist
     */
    public int getQuantity(int prodID) throws Exception{
        Product p = getProduct(prodID);
        if(p != null){
            return p.getQuantity();
        }
        throw new Exception("Boy that product doesn't exist");
    }

    public Product getProductByName(String name){
        for(Product p : productList.values()){
            if(p.getName().equalsIgnoreCase(name)){
                return p;
            }
        }
        return null;
    }

    public synchronized HashMap<Integer, Integer> getPrices() {
        HashMap<Integer,Integer> prices = new HashMap<>();
        for(Product p : this.productList.values()){
            prices.put(p.getID(),p.getPrice());
        }
        return prices;
    }

    public List<ProductInfo> getProducts() {
        List<ProductInfo> productInfos = new LinkedList<>();
        for (Product p : productList.values()){
            ProductInfo info = new ProductInfo(p.getID(), p.getName(), p.getDescription(), p.getPrice(), p.getQuantity());
            info.setCategories(getProductCategories(p.getID()));
            productInfos.add(info);
        }
        return productInfos;
    }
    public synchronized void addToCategory(String category, int productId) throws Exception {
        getProduct(productId);
//        category =category.toLowerCase();
        if(categories.containsKey(category)){
            if(!categories.get(category).contains(productId)){
                categories.get(category).add(productId);
            }
        }else{
            categories.put(category,new ArrayList<>());
            categories.get(category).add(productId);
        }
    }

    public synchronized int removeProduct(int productId) {
        if(productList.containsKey(productId)){
            productList.remove(productId);
            for(ArrayList<Integer> prodIds : categories.values()){
                if(prodIds.contains(productId)){
                    prodIds.remove(Integer.valueOf(productId));
                }
            }
            return 0;
        }
        return -1;
    }

    public void updateProduct(int productId, List<String> categories,String name, String description, int price, int quantity) throws Exception{
        if(productList.containsKey(productId)){
            if(categories!=null){
                replaceCategories(productId,categories);
            }
            else
                throw new Exception("categories empty");
            if(name!=null){
                setName(productId,name);
            }
            else
                throw new Exception("name is empty");
            if(description!=null){
                setDescription(productId,description);
            }
            else
                throw new Exception("description is empty");
            if(price > 0){
                setPrice(productId,price);
            }
            else
                throw new Exception("price is illegal");
            if(quantity > 0){
                replaceQuantity(productId,quantity);
            }
            else
                throw new Exception("quantity is illegal");
        }
    }

    private void replaceQuantity(int productId, int quantity) throws Exception {
        Product p = getProduct(productId);
        p.replaceQuantity(quantity);
    }

    private void setName(int productId, String name) throws Exception{
        Product p = getProduct(productId);
        p.setName(name);
    }

    private void replaceCategories(int productId, List<String> categories) throws Exception {
        for(ArrayList<Integer> category: this.categories.values()){
            if(category.contains(productId)){
                category.remove(Integer.valueOf(productId));
            }
        }
        for(String category: categories){
            addToCategory(category,productId);
        }
    }

    public synchronized void setProductsRatings(){
        for(Product p : productgrading.keySet()){
            double sum = 0;
            for(int rating : productgrading.get(p)){
                sum += rating;
            }
            double avg = sum/productgrading.get(p).size();
            DecimalFormat df = new DecimalFormat("#.#");
            avg = Double.parseDouble(df.format(avg));
            p.setRating(avg);
        }
    }
    public synchronized void rateProduct(int productID, int rating) throws Exception{
        if(rating > MAXRATING)
            throw new Exception("Product rating is out of bounds, expected 0 to 5 but got: "+rating);
        Product p = getProduct(productID);
        if(productgrading.containsKey(p))
            productgrading.get(p).add(rating);
        else
            productgrading.put(p,new CopyOnWriteArrayList<>(List.of(rating)));

    }
    public ArrayList<ProductInfo> filterBy(ProductFilter filter,double storeRating) {
//        filter.setOp(this::getProduct);
        filter.setCategories(categories);
        filter.setStoreRating(storeRating);
        setProductsRatings();
        //need to set more relevant things here as soon as all filters are implemented.
        ArrayList<Product> filtered = filter.filter(new ArrayList<>(productList.values()));
        ArrayList<ProductInfo> result = new ArrayList<>();
        for(Product p: filtered){
            ProductInfo info = p.getProductInfo();
            info.setCategories(getProductCategories(p.getID()));
            result.add(info);
        }
        return result;
    }
}