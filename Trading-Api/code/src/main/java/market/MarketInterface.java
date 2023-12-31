package market;

import domain.store.storeManagement.Store;
import domain.user.PurchaseHistory;
import org.hibernate.Session;
import org.json.JSONObject;
import service.ExternalService.Payment.PaymentAdapter;
import utils.infoRelated.Information;
import utils.infoRelated.LoginInformation;
import utils.Response;
import utils.infoRelated.Receipt;
import utils.messageRelated.NotificationOpcode;

import java.util.HashMap;
import java.util.List;

public interface MarketInterface {
    //guest methods
    public Response enterGuest();
    public Response exitGuest(int guestId);
    public Response register(String email ,String pass ,String birthday );
    public Response addProductToCart(int userId,int storeId ,int productId, int quantity);
    public Response removeProductFromCart(int userId,  int storeId, int productId);
    public Response changeQuantityInCart(int userId, int storeId, int productId, int change);
    Response<String> removeCart(int userId);


    public Response showFilterOptions();
    public Response filterBy(HashMap<String,String> filterOptions);


    //member methods
    public Response login(String email , String pass);
    Response<LoginInformation> getMember(int userId, String token);
    public Response displayNotifications(int userId, String token);
    public Response logout(int userId);

    Response<Receipt> makePurchase(int userId, JSONObject payment, JSONObject supplier);

    public Response changeMemberAttributes(int userId, String token, String newEmail, String newBirthday);

    Response<String> changeMemberPassword(int userId, String token, String newEmail, String newBirthday);

    public Response openStore(int userId, String token, String storeName, String des, String img);

    Response<PurchaseHistory> getUserPurchaseHistory(int userId, String token, int buyerId);

    public Response writeReviewToStore(int userId, String token, int orderId, String storeName, String content, int grading);

    Response<String> sendNotification(int userId, String token, NotificationOpcode opcode, String receiverEmail, String notify);

    public Response writeReviewToProduct(int userId, String token, int orderId, int storeId, int productId, String content, int grading);
    public Response checkReviews(int userId, String token, int storeId);
    public Response getProductInformation(int storeId , int productId);
    public Response getStoreInformation(int storeId);
    Response<Store> getStore(int userId, String token, int storeId);
    public Response getStoreProducts(int storeId);

    public Response sendQuestion(int userId, String token, int storeId, String msg);
    public Response sendComplaint(int userId, String token, int orderId, String msg);

    // manager methods
    public Response appointManager(int userId, String token, String managerToAppoint, int storeId);

    public Response changeStoreInfo(int userId, String token, int storeId, String name, String description, String img,
                                    String isActive);
    public String changeStoreAttributes(int userId, int storeId, String name, String description, String img, Session session) throws Exception;
//    public Response changePurchasePolicy(int userId, String token, int storeId, String policy);
    //public Response addDiscountPolicy(int userId, String token, int storeId, String policy);
    public Response addPurchaseConstraint(int userId, String token, int storeId, String constraint);
    public Response fireManager(int userId, String token, int managerToFire, int storeId);
    public Response checkWorkerStatus(int userId, String token, int workerId, int storeId);
    public Response checkWorkersStatus(int userId, String token, int storeId);
    public Response viewQuestions(int userId, String token, int storeId);
    public Response answerQuestion(int userId, String token, int storeId, int questionId, String answer);
    public Response seeStoreHistory(int userId, String token, int storeId);
    public Response<Integer> addProduct(int userId, String token, int storeId, List<String> categories, String name, String description,
                                        int price, int quantity, String img);
    public Response deleteProduct(int userId, String token, int storeId, int productId);
    //store owner methods

    //TODO: need to use supplierProxy for changing the quantity
    Response<String> updateProduct(int userId, String token, int storeId, int productId, List<String> categories, String name, String description,
                                   int price, int quantity, String img);

    public Response<String> appointOwner(int userId, String token, String owner, int storeId);
    public Response fireOwner(int userId , String token, int ownerId, int storeId);
    Response<String> addManagerPermissions(int ownerId, String token, int userId, int storeId, List<Integer> permissionsIds);

    Response<String> removeManagerPermissions(int ownerId, String token, int userId, int storeId, List<Integer> permissionsIds);

    public Response getAppointments(int userId, String token, int storeId);

    //store creator methods
    public String changeStoreActive(int userId, int storeId, String isActive, Session session) throws Exception;

    public Response closeStorePermanently(int adminId, String token, int storeId);

    //store methods
    public Response getProducts(int storeId);
    public Response getProducts();

    // admin methods
    public Response getAdmins(int adminId, String token);
    public Response addAdmin(int userId, String token, String email , String pass);
    public Response removeAdmin(int adminId, String token);
    public Response getUsersPurchaseHistory(int buyerId, String token);
    public Response answerComplaint(int adminId, String token, int complaintId, String ans);
    public Response<String> cancelMembership(int adminId, String token, String userToRemove);
    Response<String> removeUser(String userName);
    public Response watchEventLog(int adminId, String token);
    public Response watchMarketStatus(int adminId, String token);

    public Response setPaymentService(int adminId, String token, String paymentService);
    public Response getPaymentServicePossible(int adminId, String token);

    //TODO: fix template for this function
    Response getPaymentServiceAvailable();

    public Response addPaymentService(int adminId, String token, String paymentService);

    Response<String> addPaymentService(int adminId, String token, String esPayment, PaymentAdapter paymentAdapter);

    public Response removePaymentService(int adminId, String token, String paymentService);

    public Response setSupplierService(int adminId, String token, String supplierService);
    public Response getSupplierServicePossible(int adminId, String token);

    //TODO: fix template for this function
    Response getSupplierServiceAvailable();

    public Response addSupplierService(int adminId, String token, String supplierService);
    public Response removeSupplierService(int adminId, String token, String supplierService);

    Response<List<? extends Information>> getComplaints(int userId, String token);

    public Response changeRegularDiscount(int userId, String token, int storeId, int prodId, int percentage, String discountType, String discountedCategory, List<JSONObject> predicatesLst,String content);

    public Response placeBid(String token, int userId, int storeId, int prodId, double price,int quantity);

    public Response answerBid(String token, int userId, int storeId, boolean ans, int prodId, int bidId);

    public Response counterBid(String token, int userId, int storeId, double counterOffer, int prodId, int bidId);

    public Response editBid(String token, int userId, int bidId, int storeId, double price, int quantity);

    Response<String> addShoppingRule(int userId, String token, int storeId, String purchasePolicy,String content);

    Response<String> deletePurchasePolicy(String token, int userId, int storeId, int purchasePolicyId);

    public Response purchaseBid(String token, int userId, int storeId, int bidId, JSONObject paymentDetails, JSONObject supplierDetails);

    public Response clientAcceptCounter(String token, int bidId, int storeId);

    public Response addCompositeDiscount(String token, String body) throws Exception;
    public Response<String> removeDiscount(String token,int userId, int storeId, int discountId) throws Exception;

        Response<String> answerAppointment(int userId, String token, int storeId, String fatherName, String childName, String ans);
}



