package server;

import domain.store.storeManagement.Store;
import market.Market;
import org.json.JSONObject;
import server.Config.ConfigParser;
import utils.*;

import java.util.*;

import utils.infoRelated.*;
import utils.Response;
import utils.infoRelated.Receipt;
import utils.messageRelated.Notification;
import utils.messageRelated.NotificationOpcode;
import utils.stateRelated.Action;

public class API {
    public Market market;
    private HashMap<String, Integer> actionStrings;

    public API(ConfigParser configs){
        market = new Market(configs.getInitialAdmin(), configs.getPaymentConfig(), configs.getSupplyConfig());
        actionStrings = new HashMap<>();
        getActionStrings();
    }

    public Pair<Boolean, JSONObject> fromResToPair(Response res){
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", res.getValue());
            return new Pair<>(true, json);
        }
    }

    private Pair<Boolean, JSONObject> fromResToPairPurchase(Response<Receipt> res) {
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", "purchased cart successfully with total price: " + res.getValue().getTotalPrice());
            return new Pair<>(true, json);
        }
    }

    public Pair<Boolean, JSONObject> fromResToPairInfo(Response<? extends Information> res){
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", res.getValue().toJson());
            return new Pair<>(true, json);
        }
    }

    public static Pair<Boolean, JSONObject> fromResToPairList(Response<List<? extends Information>> res){
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", Information.infosToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    public static Pair<Boolean, JSONObject> fromResToPairListPre(Response<List<Object>> res){
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            List<String> ans = new ArrayList<>();
            for(Object o : res.getValue())
                ans.add(o.toString());
            json.put("value", ans);
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
    public Pair<Boolean, JSONObject> removeCart(int userId) {
        Response<String> res = market.removeCart(userId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getCart(int id){
        Response<List<? extends Information>> res = market.getCart(id);
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> makePurchase(int userId , JSONObject payment, JSONObject supplier){
        Response<Receipt> res = market.makePurchase(userId, payment, supplier);
        return fromResToPairPurchase(res);
    }


    //member functions
    public Pair<Boolean, JSONObject> login(String email , String pass){
        Response<LoginInformation> res = market.login(email, pass);
        return fromResToPairInfo(res);
    }
    public Pair<Boolean, JSONObject> getMemberNotifications(int userId, String token) {
        Response<List<Notification>> res = market.getMemberNotifications(userId, token);
        JSONObject json = new JSONObject();
        if(res.errorOccurred())
        {
            json.put("errorMsg", res.getErrorMessage());
            return new Pair<>(false, json);
        }
        else {
            json.put("value", NotificationsToJson(res.getValue()));
            return new Pair<>(true, json);
        }
    }

    public JSONObject NotificationsToJson(List<Notification> list)
    {
        List<String> ans = new ArrayList<>();
        for(Notification n : list)
            ans.add(n.toString());
        JSONObject json = new JSONObject();
        json.put("notifications", ans);
        return json;
    }

    public Pair<Boolean, JSONObject> getClient(int userId, String token) {
        Response<LoginInformation> res = market.getMember(userId, token);
        return fromResToPairInfo(res);
    }
    public Pair<Boolean,JSONObject> logout(int userId){
        Response<String> res = market.logout(userId);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> changeMemberAttributes(int userId, String token, String newEmail, String newBirthday) {
        Response<String> res = market.changeMemberAttributes(userId, token, newEmail, newBirthday);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeMemberPassword(int userId, String token, String oldPass, String newPass) {
        Response<String> res = market.changeMemberPassword(userId, token, oldPass, newPass);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> openStore(int userId, String token, String name, String storeDescription, String img){
        Response<Integer> res = market.openStore(userId, token, name, storeDescription, img);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> writeReviewToStore(int userId, String token, int orderId, String storeName, String content, int grading){
        Response<String> res = market.writeReviewToStore(userId, token, orderId, storeName, content, grading);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> writeReviewToProduct(int userId, String token, int orderId, int storeId,int productId, String content, int grading){
        Response<String> res = market.writeReviewToProduct(userId, token, orderId, storeId, productId, content, grading);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> getStoreProducts(int storeId){
        Response<List<? extends  Information>> res = market.getStoreProducts(storeId);
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> getFilterOptions(){
        Response<List<Object>> options = market.showFilterOptions();
        return fromResToPairListPre(options);
    }

    public List<String> getFilterOptionsString(){
        Response<List<Object>> options = market.showFilterOptions();
        List<String> ans = new ArrayList<>();
        for(Object o : options.getValue())
            ans.add(o.toString());
        return ans;
    }

    public Pair<Boolean, JSONObject> filterBy(HashMap<String, String> filters){
        Response<List<? extends Information>> options = market.filterBy(filters);
        return fromResToPairList(options);
    }

    public Pair<Boolean, JSONObject> viewReviews(int userId, String token, int storeId){
        Response<List<? extends Information>> res = market.checkReviews(userId, token, storeId);
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> viewQuestions(int userId, String token, int storeId){
        Response<List<? extends Information>> res = market.viewQuestions(userId, token, storeId);
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> sendQuestion(int userId, String token, int storeId,String msg){
        Response<String> res = market.sendQuestion(userId, token, storeId, msg);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> sendComplaint(int userId, String token, int orderId, String msg){
        Response<String> res = market.sendComplaint(userId, token, orderId, msg);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getComplaints(int userId, String token){
        Response<List<? extends Information>> res = market.getComplaints(userId, token);
        return fromResToPairList(res);
    }


    public Pair<Boolean, JSONObject> appointManager(int userId, String token, String managerToAppoint, int storeId){
        Response<String> res = market.appointManager(userId, token, managerToAppoint, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> changeStoreInfo(int userId, String token, int storeId, String name, String desc,
                                                     String img, String isActive){
        Response<String> res = market.changeStoreInfo(userId, token, storeId, name, desc, img, isActive);
        return fromResToPair(res);

    }
//    public Pair<Boolean, JSONObject> changePurchasePolicy(int userId, String token, int storeId, String policy){
//        Response<String> res = market.changePurchasePolicy(userId, token, storeId, policy);
//        return fromResToPair(res);
//    }
//
//    public Pair<Boolean, JSONObject> changeDiscountPolicy(int userId, String token, int storeId, String policy){
//        Response<String> res = market.changePurchasePolicy(userId, token, storeId, policy);
//        return fromResToPair(res);
//    }

    public Pair<Boolean, JSONObject> addPurchaseConstraint(int userId, String token, int storeId, String policy){
        Response<String> res = market.addPurchaseConstraint(userId, token, storeId, policy);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> fireManager(int userId, String token, int managerToFire, int storeId){
        Response<String> res = market.fireManager(userId, token, managerToFire, storeId);
        return fromResToPair(res);
    }


    public Pair<Boolean, JSONObject> checkWorkerStatus(int userId, String token, int workerId, int storeId) {
        Response<Info> res = market.checkWorkerStatus(userId, token, workerId, storeId);
        return fromResToPairInfo(res);
    }


    public Pair<Boolean, JSONObject> checkWorkersStatus(int userId, String token, int workerId){
        Response<List<? extends Information>> res = market.checkWorkersStatus(userId, token, workerId);
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> addProduct(int userId, String token, int storeId, List<String> categories, String name , String description,
                                                int price , int quantity, String img){
        Response<Integer> res = market.addProduct(userId, token, storeId, categories, name, description, price, quantity, img);
        return fromResToPair(res);
    }


    public Pair<Boolean, JSONObject> getProducts(){
        Response<List<? extends Information>> res = market.getProducts();
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> closeStorePermanently(int adminId, String token, int storeId){
        Response<String> res = market.closeStorePermanently(adminId, token, storeId);
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


    public Pair<Boolean, JSONObject> addManagerPermissions(int ownerId, String token, int userId,int storeId, List<String> permissionsIds)
    {
        List<String> actionStr = Information.fromStringToActionString(permissionsIds);
        List<Integer> permissions = new ArrayList<>();
        for(String str : actionStr)
            permissions.add(actionStrings.get(str));
        Response<String> res = market.addManagerPermissions(ownerId, token, userId, storeId, permissions);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> removeManagerPermissions(int ownerId, String token, int userId,int storeId, List<String> permissionsIds)
    {
        List<String> actionStr = Information.fromStringToActionString(permissionsIds);
        List<Integer> permissions = new ArrayList<>();
        for(String str : actionStr)
            permissions.add(actionStrings.get(str));
        Response<String> res = market.removeManagerPermissions(ownerId, token, userId, storeId, permissions);
        return fromResToPair(res);
    }


    public Pair<Boolean, JSONObject> answerQuestion(int userId, String token, int storeId ,int questionId, String answer)
    {
        Response<String> res = market.answerQuestion(userId, token, storeId, questionId, answer);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> seeStoreHistory(int userId, String token, int storeId)
    {
        Response<List<? extends Information>> res = market.seeStoreHistory(userId, token, storeId);
        return fromResToPairList(res);
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

    public Pair<Boolean, JSONObject> cancelMembership(int adminId, String token, String userToRemove)
    {
        Response<String> res = market.cancelMembership(adminId, token, userToRemove);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> removeUser(String userName) {
        Response<String> res = market.removeUser(userName);
        return fromResToPair(res);
    }

    //TODO: fix
    public Pair<Boolean, JSONObject> watchEventLog(int adminId, String token)
    {
        Response<List<? extends Information>> res = market.watchEventLog(adminId, token);
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> getStores()
    {
        Response<List<? extends Information>> res = market.getStoresInformation();
        return fromResToPairList(res);
    }

    public Pair<Boolean, JSONObject> getStore(int userId, String token, int storeId) {
        Response<Store> res =  market.getStore(userId, token, storeId);
        return fromResToPairInfo(res);
    }

    public Pair<Boolean, JSONObject> getNotification(int userId, String token) {
        Response<Notification> res =  market.getNotification(userId, token);
        return fromResToPairInfo(res);
    }

    public Pair<Boolean, JSONObject> watchMarketStatus(int adminId, String token)
    {
        Response<MarketInfo> res = market.watchMarketStatus(adminId, token);
        return fromResToPairInfo(res);
    }

    public Pair<Boolean, JSONObject> sendNotification(int userId, String token, String username, String notification) {
        Response<String> res = market.sendNotification(userId, token, NotificationOpcode.GET_CLIENT_DATA, username, notification);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> changeRegularDiscount(int userId, String token, int storeId, int prodId,
                                                           int percentage, String discountType, String discountedCategory,
                                                           List<JSONObject> predicatesLst,String content) {
        Response<String> res = market.changeRegularDiscount(userId, token, storeId, prodId, percentage, discountType,
                discountedCategory, predicatesLst,content);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> getPaymentPossibleServices(int adminId, String token) {
        Response<List<Object>> res = market.getPaymentServicePossible(adminId, token);
        return fromResToPairListPre(res);
    }

    public Pair<Boolean, JSONObject> getSupplierPossibleServices(int adminId, String token) {
        Response<List<Object>> res = market.getSupplierServicePossible(adminId, token);
        return fromResToPairListPre(res);
    }

    public Pair<Boolean, JSONObject> getSupplierAvailableServices() {
        Response<List<Object>> res = market.getSupplierServiceAvailable();
        return fromResToPairListPre(res);
    }

    public Pair<Boolean, JSONObject> getPaymentAvailableServices() {
        Response<List<Object>> res = market.getPaymentServiceAvailable();
        return fromResToPairListPre(res);
    }
    public Pair<Boolean, JSONObject> placeBid(String token, int storeId, int prodId, int userId, double price,int quantity) {
        Response<String> res = market.placeBid(token, userId, storeId, prodId, price,quantity);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> answerBid(String token, int storeId, int userId, boolean ans, int prodId, int bidId) {
        Response<String> res = market.answerBid(token, userId, storeId, ans, prodId, bidId);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> counterBid(String token, int storeId, int userId, double counterOffer, int prodId, int bidId) {
        Response<String> res = market.counterBid(token, userId, storeId, counterOffer, prodId, bidId);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> editBid(String token, int storeId , int userId, double price, int quantity, int bidId) {
        Response<String> res = market.editBid(token, userId, bidId, storeId, price,quantity);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> addShoppingRule(String token, int storeId, int userId, String purchasePolicy,String content) {
        Response<String> res =  market.addShoppingRule(userId, token, storeId, purchasePolicy,content);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> deletePurchasePolicy(String token, int userId, int storeId, int purchasePolicyId) {
        Response<String> res = market.deletePurchasePolicy(token, userId, storeId, purchasePolicyId);
        return fromResToPair(res);
    }
    public Pair<Boolean, JSONObject> purchaseBid(String token, int userId, int storeId, int bidId, JSONObject paymentDetails, JSONObject supplierDetails) {
        Response<Receipt> res = market.purchaseBid(token, userId, storeId, bidId, paymentDetails, supplierDetails);
        return fromResToPairPurchase(res);
    }
    public Pair<Boolean, JSONObject> clientAcceptCounter(String token, int bidId, int storeId) {
        Response<String> res = market.clientAcceptCounter(token, bidId, storeId);
        return fromResToPair(res);
    }

    public Pair<Boolean, JSONObject> addCompositeDiscount(String token, String body) throws Exception {
        return fromResToPair(market.addCompositeDiscount(token,body));
    }

    public Pair<Boolean, JSONObject> answerAppointment(int userId, String token, int storeId, String fatherName,
                                                       String childName, String ans) {
        Response<String> res = market.answerAppointment(userId, token, storeId, fatherName, childName, ans);
        return fromResToPair(res);

    }

    //for actions to actionString
    private void getActionStrings(){
        actionStrings.put(Action.addProduct.toString(), 0);
        actionStrings.put(Action.removeProduct.toString(), 1);
        actionStrings.put(Action.updateProduct.toString(), 2);
        actionStrings.put(Action.changeStoreDetails.toString(), 3);
        actionStrings.put(Action.deletePurchasePolicy.toString(),4);
        actionStrings.put(Action.deleteDiscountPolicy.toString(),5);
        actionStrings.put(Action.addPurchaseConstraint.toString(), 6);
        actionStrings.put(Action.addDiscountConstraint.toString(),7);
        actionStrings.put(Action.viewMessages.toString(),8);
        actionStrings.put(Action.answerMessage.toString(),9);
        actionStrings.put(Action.seeStoreHistory.toString(), 10);
        actionStrings.put(Action.seeStoreOrders.toString(), 11);
        actionStrings.put(Action.checkWorkersStatus.toString(), 12);
        actionStrings.put(Action.appointManager.toString(), 13);
        actionStrings.put(Action.fireManager.toString(), 14);
        actionStrings.put(Action.appointOwner.toString(),15);
        actionStrings.put(Action.fireOwner.toString(),16);
        actionStrings.put(Action.changeManagerPermission.toString(),17);
        actionStrings.put(Action.closeStore.toString(), 18);
        actionStrings.put(Action.reopenStore.toString(), 19);
    }

    private JSONObject createPaymentJson()
    {
        JSONObject payment = new JSONObject();
        payment.put("payment_service", "Mock");
        payment.put("Mock", "on");
        payment.put("cardNumber", "123456789");
        payment.put("month", "01");
        payment.put("year", "30");
        payment.put("holder", "Israel Visceral");
        payment.put("ccv", "000");
        payment.put("id", "123456789");
        return payment;
    }

    private static JSONObject createSupplierJson()
    {
        JSONObject supplier = new JSONObject();
        supplier.put("supply_service", "Mock");
        supplier.put("Mock", "on");
        supplier.put("name", "Israel Visceral");
        supplier.put("address", "Reger 17");
        supplier.put("city", "Beer Sheva");
        supplier.put("country", "Israel");
        supplier.put("zip", "700000");
        return supplier;
    }


    public void mockData() {
        JSONObject payment = createPaymentJson();
        JSONObject supplier = createSupplierJson();
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
        res2 = market.addProduct(id1, token1, sid1, categories, "air1", "comfy", 100, 20, "https://images.pexels.com/photos/13691727/pexels-photo-13691727.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1");
        int pid1 = res2.getValue();
        res2 = market.addProduct(id2, token2, sid2, categories, "air2", "more comfy", 300, 10, "https://images.pexels.com/photos/4215840/pexels-photo-4215840.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1");
        int pid2 = res2.getValue();
        market.addProductToCart(id1, sid1, pid1, 3);
        market.addProductToCart(id1, sid2, pid2, 5);
        market.addProductToCart(id2, sid1, pid1, 1);
        Response<Receipt> res3 = market.makePurchase(id1, payment, supplier);
        market.sendComplaint(id1, token1, res3.getValue().getOrderId(), "baaaaaad");
        market.writeReviewToStore(id1, token1, res3.getValue().getOrderId(), "rollups", "bad store", 2);
        market.writeReviewToProduct(id1, token1, res3.getValue().getOrderId(),sid2, pid2, "aaaaa", 3);
        res3 = market.makePurchase(id2, payment, supplier);
        market.writeReviewToStore(id2, token2, res3.getValue().getOrderId(), "nike", "good store", 4);
        market.sendQuestion(id1, token1, sid1, "why bad?");
        market.appointManager(id1, token1, "ziv@gmail.com", sid1);
        market.appointManager(id2, token2, "eli@gmail.com", sid2);
        market.logout(id1);
        market.logout(id2);
    }


    public String getTokenForTest() {
        return market.addTokenForTests();
    }

    public Pair<Boolean, JSONObject> removeDiscount(String token, int userId, int storeId, int discountId) throws Exception {
        Response<String> res = market.removeDiscount(token,userId,storeId,discountId);
        return fromResToPair(res);
    }
}
