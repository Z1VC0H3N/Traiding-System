package domain.store.order;


import utils.Status;

import java.util.HashMap;
import java.util.List;

public class Order {
    private int orderId;
    private Status status;
    private int userId;
    private HashMap<Integer, HashMap<Integer,Integer>> productsInStores; 
    //<storeID,<productID, quantity>> 
    public Order(int id, int user_id,HashMap<Integer,HashMap<Integer,Integer>> products){
        orderId = id;
        userId = user_id;
        status = Status.pending;
        productsInStores = products; 
    }

    public int getOrderId() {
        return orderId;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status stat){
        this.status = stat;
    }
    public int getUserId() {
        return userId;
    }
    public HashMap<Integer, HashMap<Integer, Integer>> getProductsInStores() {
        return productsInStores;
    }
    /**
     * This functions' purpose is to add the products,quantity list into the
        global variable this.productsInStores in the storeID entry.
        If the entry doesn't exist it adds a new store entry and the products associated
        with it, otherwise it will add to the quantity of the product if the productID entry
        exists or creates a new productID entry with the specified quantity.
     * @param storeID int
     * @param products HashMap<productID,quantity>
     */
    public void addProductsToOrder(int storeID,HashMap<Integer, Integer> products) {
        HashMap<Integer,Integer> prod4Store = products;
        if(this.productsInStores.containsKey(storeID)){
            prod4Store = this.productsInStores.get(storeID);
            for(int prodId:products.keySet()){
                if(prod4Store.containsKey(prodId)){
                    prod4Store.put(prodId, prod4Store.get(prodId) + products.get(prodId));
                }
                else{
                    prod4Store.put(prodId,products.get(prodId));
                }
            }
        }
        this.productsInStores.put(storeID,prod4Store);
    }
    
    /**
     * the name says it all, dont be daft.
     * @param storeID int
     * @param products HashMap<productID,quantity>
     */
    public void replaceProductsInOrder(int storeID,HashMap<Integer, Integer> products){
        this.productsInStores.put(storeID,products);
    }
    

}