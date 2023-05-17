package server;

import com.google.gson.Gson;
import domain.store.storeManagement.AppHistory;
import domain.store.storeManagement.Store;
import market.Admin;
import market.Market;
import org.json.JSONObject;
import utils.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import utils.marketRelated.MarketInfo;
import utils.marketRelated.Response;
import utils.messageRelated.Message;
import utils.orderRelated.Order;
import utils.orderRelated.OrderInfo;
import utils.stateRelated.Action;
import utils.stateRelated.Role;
import utils.userInfoRelated.Info;
import utils.userInfoRelated.Receipt;




public class API {
    public Market market;
    Gson gson;
    public API(){
        Admin admin = new Admin(-1, "elibenshimol6@gmail.com", "123Aaa");
        market = new Market(admin);
        gson = new Gson();
    }

    public Pair<Boolean, JSONObject> fromResToPair(Response res){
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            if(res.getValue().getClass() != String.class && res.getValue().getClass() != Integer.class)
                json.put("value", gson.toJson(res.getValue()));
            else
                json.put("value", res.getValue());
            return new Pair<>(true, json);
        }
    }

    //guest functions
    public Pair<Boolean, JSONObject> enterGuest(){
        Response<Integer> res = market.enterGuest();
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> exitGuest(int guestId){
        Response<String> res = market.exitGuest(guestId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> register(String email, String password, String birthday){
        Response<String> res = market.register(email, password, birthday);
        return fromResToPair(res);

    }

    public Pair<Boolean, JSONObject> addProductToCart(int userId,int storeId ,int productId, int quantity){
        Response<String> res = market.addProductToCart(userId, storeId, productId, quantity);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> removeProductFromCart(int userId,  int storeId, int productId){
        Response<String> res = market.removeProductFromCart(userId, storeId, productId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeQuantityInCart(int userId, int storeId, int productId, int change){
        Response<String> res = market.changeQuantityInCart(userId, storeId, productId, change);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> addQuantityInCart(int userId, int storeId, int productId, int change){
        Response<String> res = market.addQuantityInCart(userId, storeId, productId, change);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> removeQuantityInCart(int userId, int storeId, int productId, int change){
        Response<String> res = market.removeQuantityInCart(userId, storeId, productId, change);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> removeCart(int userId) {
        Response<String> res = market.removeCart(userId);
        return fromResToPair(res);
    }

    public List<JSONObject> getBaskets(HashMap<Integer, HashMap<Integer, Integer>> basketsMaps)
    {
        List<JSONObject> baskets = new ArrayList();
        for (Map.Entry<Integer, HashMap<Integer, Integer>> basketEntry : basketsMaps.entrySet()) {
            JSONObject basketJson = getBasket(basketEntry);
            baskets.add(basketJson);
        }
        return baskets;
    }

    public Pair<Boolean, JSONObject> getCart(int id){
        Response<HashMap<Integer, HashMap<Integer, Integer>>> res = market.getCart(id);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", getBaskets(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    public Pair<Boolean, JSONObject> makePurchase(int userId , String accountNumber){
        Response<Receipt> res = market.makePurchase(userId, accountNumber);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> getStoreDescription(int storeId){
        Response<String> res = market.getStoreDescription(storeId);
        return fromResToPair(res);
    }

    private List<JSONObject> getStoreInfoHM(HashMap<Integer, String> hashMap, String key, String value){
        List<JSONObject> jsonList = new ArrayList();
        if(hashMap != null)
        {
            for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
                JSONObject jsonO = new JSONObject();
                jsonO.put(key, entry.getKey());
                jsonO.put(value, entry.getValue());
                jsonList.add(jsonO);
            }
        }
        return jsonList;
    }

    private List<JSONObject> getStoreRole(HashMap<Integer, Role> hashMap, String key, String value){
        List<JSONObject> jsonList = new ArrayList();
        if(hashMap != null) {
            for (Map.Entry<Integer, Role> entry : hashMap.entrySet()) {
                JSONObject jsonO = new JSONObject();
                jsonO.put(key, entry.getKey());
                jsonO.put(value, entry.getValue());
                jsonList.add(jsonO);
            }
        }
        return jsonList;
    }
    private List<JSONObject> getStorePermissions(HashMap<Integer, List<Action>> hashmap, String key, String value){
        List<JSONObject> jsonList = new ArrayList();
        if(hashmap != null) {
            for (Map.Entry<Integer, List<Action>> entry : hashmap.entrySet()) {
                JSONObject jsonO = new JSONObject();
                jsonO.put(key, entry.getKey());
                jsonO.put(value, fromActionToString(entry.getValue()));
                jsonList.add(jsonO);
            }
        }
        return jsonList;
    }

    public JSONObject loginToJson(LoginInformation login)
    {
        JSONObject json = new JSONObject();
        json.put("token", login.getToken());
        json.put("userId", login.getUserId());
        json.put("userName", login.getUserName());
        json.put("isAdmin", login.getIsAdmin());
        json.put("hasQuestions", login.HasQuestions());
        json.put("notifications", login.getNotifications());
        json.put("storeNames", getStoreInfoHM(login.getStoreNames(), "storeId", "storeName"));
        json.put("storeRoles", getStoreRole(login.getStoreRoles(), "storeId", "storeRole"));
        json.put("storeImgs", getStoreInfoHM(login.getStoreImg(), "storeId", "storeImg"));
        json.put("permissions", getStorePermissions(login.getPermissions(), "storeId", "actions"));
        return json;
    }

    //member functions
    public Pair<Boolean, JSONObject> login(String email , String pass){
        Response<LoginInformation> res = market.login(email, pass);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", loginToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }
    public Pair<Boolean, JSONObject> getClient(int userId, String token) {
        Response<LoginInformation> res = market.getMember(userId, token);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", loginToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }
    public Pair<Boolean,JSONObject> logout(int userId){
        Response<String> res = market.logout(userId);
        return fromResToPair(res);
    }

    //TODO: add to front and server security questions
    public Pair<Boolean, JSONObject> checkSecurityQuestions(int userId, String token, List<String> answers){
        Response<String> res = market.checkSecurityQuestions(userId, token, answers);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> addSecurityQuestion(int userId, String token, String question, String answer){
        Response<String> res = market.addSecurityQuestion(userId, token, question, answer);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeAnswerForLoginQuestion(int userId, String token, String question, String answer){
        Response<String> res = market.changeAnswerForLoginQuestion(userId, token, question, answer);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> removeSecurityQuestion(int userId, String token, String question){
        Response<String> res = market.removeSecurityQuestion(userId, token, question);
        return fromResToPair(res);
    }

    //TODO: check that works in getClient and login
    public Pair<Boolean, JSONObject> displayNotifications(int userId, String token){
        Response<List<String>> res = market.displayNotifications(userId, token);
        return fromResToPair(res);
    }


    //TODO: add to front a way to change user info
    public Pair<Boolean, JSONObject> changePassword(int userId, String token, String oldPass, String newPass){
        Response<String> res = market.changePassword(userId, token, oldPass, newPass);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeName(int userId, String token, String newUserName){
        Response<String> res = market.changeName(userId, token, newUserName);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeEmail(int userId, String token, String newEmail){
        Response<String> res = market.changeEmail(userId, token, newEmail);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> openStore(int userId, String token, String name, String storeDescription, String img){
        Response<Integer> res = market.openStore(userId, token, name, storeDescription, img);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getMemberInformation(int userId, String token){
        Response<Info> res = market.getMemberInformation(userId, token);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getUserPurchaseHistory(int userId, String token, int buyerId){
        //               orderId,         storeId,       productId, quantity
        Response<HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>> res = market.getUserPurchaseHistory(userId, token, buyerId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> writeReviewToStore(int userId, String token, int orderId, int storeId, String content, int grading){
        Response<String> res = market.writeReviewToStore(userId, token, orderId, storeId, content, grading);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> writeReviewToProduct(int userId, String token, int orderId, int storeId,int productId, String content, int grading){
        Response<String> res = market.writeReviewToProduct(userId, token, orderId, storeId, productId, content, grading);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> checkReviews(int userId, String token, int storeId){
        Response<HashMap<Integer, Message>> res = market.checkReviews(userId, token, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getProductInformation(int storeId, int productId){
        Response<ProductInfo> res = market.getProductInformation(storeId, productId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getStoreInformation(int storeId){
        Response<StoreInfo> res = market.getStoreInformation(storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getStoreProducts(int storeId){
        Response<List<ProductInfo>> res = market.getStoreProducts(storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> sendQuestion(int userId, String token, int storeId,String msg){
        Response<String> res = market.sendQuestion(userId, token, storeId, msg);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> sendComplaint(int userId, String token, int orderId, int storeId, String msg){
        Response<String> res = market.sendComplaint(userId, token, orderId, storeId, msg);
        return fromResToPair(res);
    }


    public Pair<Boolean, JSONObject> appointManager(int userId, String token, String managerToAppoint, int storeId){
        Response<String> res = market.appointManager(userId, token, managerToAppoint, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> appointManager(int userId, String token, int managerIdToAppoint, int storeId){
        Response<String> res = market.appointManager(userId, token, managerIdToAppoint, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeStoreDescription(int userId, String token, int storeId, String description){
        Response<String> res = market.changeStoreDescription(userId, token, storeId, description);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> changeStoreImg(int userId, String token, int storeId, String img) {
        Response<String> res = market.changeStoreImg(userId, token, storeId, img);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeStoreName(int userId, String token, int storeId, String name) {
        Response<String> res = market.changeStoreName(userId, token, storeId, name);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changePurchasePolicy(int userId, String token, int storeId, String policy){
        Response<String> res = market.changePurchasePolicy(userId, token, storeId, policy);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeDiscountPolicy(int userId, String token, int storeId, String policy){
        Response<String> res = market.changePurchasePolicy(userId, token, storeId, policy);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> addPurchaseConstraint(int userId, String token, int storeId, String policy){
        Response<String> res = market.addPurchaseConstraint(userId, token, storeId, policy);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> fireManager(int userId, String token, int managerToFire, int storeId){
        Response<String> res = market.fireManager(userId, token, managerToFire, storeId);
        return fromResToPair(res);
    }

    private JSONObject infoToJson(Info info)
    {
        JSONObject json = new JSONObject();
        json.put("userId", info.getId());
        json.put("userName", info.getName());
        json.put("email", info.getEmail());
        json.put("birthday", info.getBirthday());
        json.put("age", info.getAge());
        json.put("managerPermissions", info.getManagerActions());
        return json;
    }

    public Pair<Boolean, JSONObject> checkWorkerStatus(int userId, String token, int workerId, int storeId) {
        Response<Info> res = market.checkWorkerStatus(userId, token, workerId, storeId);
        JSONObject json = new JSONObject();
        if (res.errorOccurred()) {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        } else {
            json.put("value", infoToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    private String infosToJson(List<Info> infos)
    {
        return infos.stream()
                .map(info -> infoToJson(info).toString())
                .collect(Collectors.joining(",", "[", "]"));
    }


    public Pair<Boolean, JSONObject> checkWorkersStatus(int userId, String token, int workerId){
        Response<List<Info>> res = market.checkWorkersStatus(userId, token, workerId);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", infosToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    public Pair<Boolean, JSONObject> closeStore(int userId, String token, int storeId){
        Response<String> res = market.closeStore(userId, token, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> reopenStore(int userId, String token, int storeId){
        Response<String> res = market.reopenStore(userId, token, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> addProduct(int userId, String token, int storeId, List<String> categories, String name , String description,
                                                int price , int quantity, String img){
        Response<Integer> res = market.addProduct(userId, token, storeId, categories, name, description, price, quantity, img);
        return fromResToPair(res);
    }

    private JSONObject productToJson(ProductInfo product)
    {
        JSONObject json = new JSONObject();
        json.put("storeId", product.getId());
        json.put("productId", product.getId());
        json.put("name", product.getName());
        json.put("description", product.getDescription());
        json.put("price", product.getPrice());
        json.put("quantity", product.getQuantity());
        json.put("categories", product.getCategories());
        json.put("rating", product.getRating());
        json.put("reviews", reviewsToJson(product.getReviews(), "messageId", "review"));
        json.put("img", product.getImg());
        return json;
    }

    private List<JSONObject> productsToJson(List<ProductInfo> products)
    {
        List<JSONObject> ans = new ArrayList<>();
        for(ProductInfo product : products)
            ans.add(productToJson(product));
        return ans;
    }

    public Pair<Boolean, JSONObject> getProducts(){
        Response<List<ProductInfo>> res = market.getProducts();
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", productsToJson(res.getValue()));
            return new Pair<>(true, json);
        }

    }

    public Pair<Boolean, JSONObject> closeStorePermanently(int adminId, String token, int storeId){
        Response<String> res = market.closeStorePermanently(adminId, token, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> appointOwner(int userId, String token, int ownerId, int storeId)
    {
        Response<String> res = market.appointOwner(userId, token, ownerId, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> appointOwner(int userId, String token, String ownerMail, int storeId)
    {
        Response<String> res = market.appointOwner(userId, token, ownerMail, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> fireOwner(int userId, String token, int ownerId, int storeId)
    {
        Response<String> res = market.fireOwner(userId, token, ownerId, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> addManagerPermission(int ownerId, String token, int userId,int storeId, int permissionsId)
    {
        Response<String> res = market.addManagerPermission(ownerId, token, userId, storeId, permissionsId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> addManagerPermissions(int ownerId, String token, int userId,int storeId, List<Integer> permissionsIds)
    {
        Response<String> res = market.addManagerPermissions(ownerId, token, userId, storeId, permissionsIds);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> removeManagerPermissions(int ownerId, String token, int userId,int storeId, List<Integer> permissionsIds)
    {
        Response<String> res = market.removeManagerPermissions(ownerId, token, userId, storeId, permissionsIds);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> removeManagerPermission(int ownerId, String token, int userId,int storeId, int permissionsId)
    {
        Response<String> res = market.removeManagerPermission(ownerId, token, userId, storeId, permissionsId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> answerQuestion(int userId, String token, int storeId ,int questionId, String answer)
    {
        Response<String> res = market.answerQuestion(userId, token, storeId, questionId, answer);
        return fromResToPair(res);
    }

    private JSONObject getBasket(Map.Entry<Integer, HashMap<Integer, Integer>> basketEntry){
        JSONObject basketJson = new JSONObject();
        basketJson.put("storeId", basketEntry.getKey());
        List<JSONObject> bucketList = new ArrayList();
        for (Map.Entry<Integer, Integer> productEntry : basketEntry.getValue().entrySet()) {
            JSONObject productJson = new JSONObject();
            productJson.put("productId", productEntry.getKey());
            productJson.put("quantity", productEntry.getValue());
            bucketList.add(productJson);
        }
        basketJson.put("products", bucketList);
        return basketJson;
    }

    private JSONObject orderToJson(OrderInfo order) {
        JSONObject json = new JSONObject();
        json.put("orderId", order.getOrderId());
        json.put("userId", order.getUserId());
        json.put("totalPrice", order.getTotalPrice());
        json.put("productsInStores", getBaskets(order.getProductsInStores()));
        return json;
    }

    private List<JSONObject> ordersToJson(List<OrderInfo> orders) {
        List<JSONObject> ans = new ArrayList<>();
        for(OrderInfo order : orders)
            ans.add(orderToJson(order));
        return ans;
    }

    public Pair<Boolean, JSONObject> seeStoreHistory(int userId, String token, int storeId)
    {
        Response<List<OrderInfo>> res = market.seeStoreHistory(userId, token, storeId);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", ordersToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    public Pair<Boolean, JSONObject> deleteProduct(int userId, String token, int storeId, int productId)
    {
        Response<String> res = market.deleteProduct(userId, token, storeId, productId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> updateProduct(int userId, String token, int storeId, int productId, List<String> categories, String name , String description ,
                                                   int price , int quantity, String img)
    {
        Response<String> res = market.updateProduct(userId, token, storeId, productId, categories, name, description, price, quantity, img);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> adminLogin(String email, String password)
    {
        Response<LoginInformation> res = market.adminLogin(email, password);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> adminLogout(int adminId)
    {
        Response<String> res = market.adminLogout(adminId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getAdmins(int adminId, String token)
    {
        Response<HashMap<Integer, Admin>> res = market.getAdmins(adminId, token);
        //TODO: fix that

        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> addAdmin(int adminId, String token, String email , String pass)
    {
        Response<String> res = market.addAdmin(adminId, token, email, pass);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> removeAdmin(int adminId, String token)
    {
        Response<String> res = market.removeAdmin(adminId, token);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> answerComplaint(int adminId, String token, int complaintId, String ans)
    {
        Response<String> res = market.answerComplaint(adminId, token, complaintId, ans);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> cancelMembership(int adminId, String token, int userToRemove)
    {
        Response<String> res = market.cancelMembership(adminId, token, userToRemove);
        return fromResToPair(res);
    }

    private String logsToString(HashMap<Logger.logStatus, List<String>> logs){
        List<String> logsList = new ArrayList();
        for (Map.Entry<Logger.logStatus, List<String>> logEntry : logs.entrySet()) {
            JSONObject productJson = new JSONObject();
            productJson.put("status", logEntry.getKey());
            productJson.put("messages", logEntry.getValue());
            logsList.add(productJson.toString());
        }
        String logsMessages = logsList.stream()
                .collect(Collectors.joining(",", "[", "]"));
        return logsMessages;
    }

    public Pair<Boolean, JSONObject> watchEventLog(int adminId, String token)
    {
        Response<List<String>> res1 = market.watchEventLog(adminId, token);
        Response<List<String>> res2 = market.watchFailLog(adminId, token);
        //[{"status": ["log1...", "log2...", ......]}, ...]
        JSONObject json = new JSONObject();
        if(res1.errorOccurred())
        {
            json.put("errorMsg", res1.getErrorMessage());
            return new Pair<>(false, json);
        }
        else if(res2.errorOccurred())
        {
            json.put("errorMsg", res2.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            List<String> event = res1.getValue();
            List<String> fail = res2.getValue();
            HashMap<Logger.logStatus, List<String>> ans = new HashMap<>();
            ans.put(Logger.logStatus.Fail, fail);
            ans.put(Logger.logStatus.Success, event);
            json.put("value", logsToString(ans));
            return new Pair<>(true, json);
        }
    }

    public Pair<Boolean, JSONObject> viewQuestions(int userId, String token, int storeId)
    {
        Response<HashMap<Integer, Message>> res = market.viewQuestions(userId, token, storeId);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", questionsToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    private String questionsToJson(HashMap<Integer, Message> questions) {
        List<String> questionsList = new ArrayList<>();
        for (Map.Entry<Integer, Message> question: questions.entrySet())
        {
            JSONObject questionJson = new JSONObject();
            questionJson.put("questionId", question.getKey());
            questionJson.put("message", question.getValue());
            questionsList.add(questionJson.toString());
        }
        return questionsList.stream()
                .collect(Collectors.joining(",", "[", "]"));
    }

    public Pair<Boolean, JSONObject> getUsersPurchaseHistory(int buyerId, String token)
    {
        Response<List<HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>>> res = market.getUsersPurchaseHistory(buyerId, token);
        // TODO: cast this to json
        return fromResToPair(res);
    }

    private JSONObject storeToJson(StoreInfo store)
    {
        JSONObject json = new JSONObject();
        json.put("storeId", store.getStoreId());
        json.put("name", store.getName());
        json.put("description", store.getDescription());
        json.put("isActive", store.getIsActive());
        json.put("creatorId", store.getCreatorId());
        json.put("rating", store.getRating());
        json.put("img", store.getUrl());
        return json;
    }

    private String storesToJson(List<StoreInfo> stores)
    {
        return stores.stream()
                .map(info -> storeToJson(info).toString())
                .collect(Collectors.joining(",", "[", "]"));
    }
    public Pair<Boolean, JSONObject> getStores()
    {
        Response<List<StoreInfo>> res = market.getStoresInformation();
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", storesToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    private List<JSONObject> reviewsToJson(HashMap<Integer, Message> hashMap, String key, String value){
        List<JSONObject> jsonList = new ArrayList();
        if(hashMap != null) {
            for (Map.Entry<Integer, Message> entry : hashMap.entrySet()) {
                JSONObject jsonO = new JSONObject();
                jsonO.put(key, entry.getKey());
                jsonO.put(value, messageToJson(entry.getValue()));
                jsonList.add(jsonO);
            }
        }
        return jsonList;

    }

    private JSONObject messageToJson(Message m){
        JSONObject json = new JSONObject();
        json.put("messageId", m.getMessageId());
        json.put("content", m.getContent());
        json.put("rating", m.getRating());
        json.put("state", m.getMessageState());
        json.put("orderId", m.getOrderId());
        json.put("storeId", m.getStoreId());
        json.put("productId", m.getProductId());
        json.put("gotFeedback", m.gotFeedback());
        json.put("seen", m.getSeen());
        return json;
    }
    private JSONObject allStoreToJson(Store store)
    {
        JSONObject json = new JSONObject();
        json.put("storeId", store.getStoreId());
        json.put("storeName", store.getName());
        json.put("description", store.getStoreDescription());
        json.put("isActive", store.isActive());
        json.put("creatorId", store.getCreatorId());
        json.put("appHistory", store.getApp());
        json.put("inventory", productsToJson(store.getProducts()));
        json.put("storeOrders", ordersToJson(store.getOrdersHistory()));
        json.put("reviews", reviewsToJson(store.getStoreReviews(), "messageId", "review"));
        json.put("questions", reviewsToJson(store.getStoreQuestions(), "messageId", "question"));
        json.put("img", store.getImgUrl());
        return json;
    }
    public Pair<Boolean, JSONObject> getStore(int userId, String token, int storeId) {
        Response<Store> res =  market.getStore(userId, token, storeId);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", allStoreToJson(res.getValue()));
            System.out.println(json);
            return new Pair<>(true, json);
        }
    }

    public Pair<Boolean, JSONObject> getAppointments(int userId, String token, int storeId)
    {
        Response<AppHistory> res = market.getAppointments(userId, token, storeId);
        // TODO: cast this to json
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> watchMarketStatus(int adminId, String token)
    {
        Response<MarketInfo> res = market.watchMarketStatus(adminId, token);
        // TODO: cast this to json
        return fromResToPair(res);
    }

    public void mockData(){
        market.register("eli@gmail.com", "123Aaa", "24/02/2002");
        market.register("ziv@gmail.com", "456Bbb", "01/01/2002");
        market.register("nave@gmail.com", "789Ccc", "01/01/1996");
        Response<LoginInformation> res = market.login("eli@gmail.com", "123Aaa");
        int id1 = res.getValue().getUserId();
        String token1 = res.getValue().getToken();
        res = market.login("ziv@gmail.com", "456Bbb");
        int id2 = res.getValue().getUserId();
        String token2 = res.getValue().getToken();
        Response<Integer> res2 = market.openStore(id1, token1, "nike", "shoe store", "https://images.pexels.com/photos/786003/pexels-photo-786003.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1");
        int sid1 = res2.getValue();
        res2 = market.openStore(id2, token2, "rollups", "candy store", "https://images.pexels.com/photos/65547/pexels-photo-65547.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1");
        int sid2 = res2.getValue();
        List<String> categories = new LinkedList<>();
        categories.add("shoes");
        categories.add("new");
        categories.add("fresh");
        res2 = market.addProduct(id1, token1, sid1, categories, "air1", "comfy", 100, 4, "https://images.pexels.com/photos/13691727/pexels-photo-13691727.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1");
        int pid1 = res2.getValue();
        res2 = market.addProduct(id1, token1, sid1, categories, "air2", "more comfy", 300, 1,"https://www.pexels.com/photo/person-wearing-air-jordan-1-4215840/");
        int pid2 = res2.getValue();
        market.addProductToCart(id1, sid1, pid1, 1);
        Response<Receipt> res3 = market.makePurchase(id1, "9999999");
        market.writeReviewToStore(id1, token1, res3.getValue().getOrderId(), sid1, "bad store", 2);
        market.sendQuestion(id1, token1, sid1, "why bad?");
        market.appointManager(id1, token1, id2, sid1);
        market.logout(id1);
        market.logout(id2);
    }

    public List<String> fromActionToString(List<Action> actions){
        List<String> ans = new LinkedList<>();
        for(Action a : actions){
            String as = a.toString();
            for(int i = 0; i<as.length(); i++) {
                if (40 < as.charAt(i) && as.charAt(i) < 91) {
                    char add = (char)(as.charAt(i) + 32);
                    as = as.substring(0, i) + " " + add + as.substring(i + 1);
                }
            }
            ans.add(as);
        }
       return ans;
    }

}
