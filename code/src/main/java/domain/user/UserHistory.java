package domain.user;


import utils.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserHistory {

    //the hashmap shows from orderId to the shopping cart content
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> purchaseHistory;
    private HashMap <Integer, Integer> ordersAndPrices;
    private List<String> names;
    private List<String> passwords;
    private List<String> emails;
    private List<Pair<String, String>> securityQuestions;

    public UserHistory(){
        purchaseHistory = new HashMap<>();
        ordersAndPrices = new HashMap<>();
        names = new LinkedList<>();
        passwords = new LinkedList<>();
        emails = new LinkedList<>();
        securityQuestions = new LinkedList<>();
    }

    public void addPurchaseMade(int orderId, int totalPrice, HashMap<Integer, HashMap<Integer, Integer>> purchase){
        purchaseHistory.put(orderId, purchase);
        ordersAndPrices.put(orderId, totalPrice);
    }

    public void addName(String name){
        names.add(name);
    }

    public void addPassword(String password){
        passwords.add(password);
    }
    public void addEmail(String email){
        emails.add(email);
    }

    public void addQuestion(Pair<String, String> question){
        securityQuestions.add(question);
    }

    public boolean checkOrderOccurred(int orderId){
        return purchaseHistory.containsKey(orderId);
    }

    public boolean checkOrderContainsStore(int orderId, int storeId){
        if(purchaseHistory.containsKey(orderId))
            if(purchaseHistory.get(orderId).containsKey(storeId))
                return true;
        return false;
    }

    public boolean checkOrderContainsProduct(int orderId, int storeId, int productId){
        if(purchaseHistory.containsKey(orderId))
            if(purchaseHistory.get(orderId).containsKey(storeId))
                if(purchaseHistory.get(orderId).get(storeId).containsKey(productId))
                    return true;
        return false;
    }

    public String getUserPurchaseHistory(String name) {
        String purchases = "purchase history for user " + name + ":\n";
        for(int orderId : purchaseHistory.keySet()){
            purchases = purchases+"  orderId: " + orderId +"\n";
            for(int storeId : purchaseHistory.get(orderId).keySet()){
                purchases = purchases + "    storeId: " + storeId + "\n";
                for(int productId : purchaseHistory.get(orderId).get(storeId).keySet()) {
                    purchases = purchases + "      proudctId: " + productId + ", quantity: " + purchaseHistory.get(orderId).get(storeId).get(productId) + "\n";
                }
            }
            purchases = purchases + "  the total price was: " + ordersAndPrices.get(orderId) + "\n";
        }
       return purchases;
    }

    public String getInformation() {
        String ans = "the current name is: " + names.get(names.size()-1) + ".\n";
        ans = ans + " list of all names that were used:\n";
        for(String name : names)
            ans = ans + "  " + name + "\n";
        ans = ans + "the current email is: "+emails.get(emails.size()-1) + ".\n";
        ans = ans + " list of all emails that were used:\n";
        for(String email: emails)
            ans = ans + "  " + email + "\n";
        ans = ans + "the current password is: " + passwords.get(passwords.size()-1) + ".\n";
        ans = ans + " list of all passwords that were used:\n";
        for(String password: passwords)
            ans = ans + "  " + password + "\n";
        return ans;

    }

    public void changeAnswerForQuestion(String question, String newAnswer) {
        for(Pair<String, String> secQuestion : securityQuestions)
            if(secQuestion.getFirst().equals(question))
                secQuestion.setSecond(newAnswer);
    }
}
