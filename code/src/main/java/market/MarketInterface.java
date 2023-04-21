package market;

import domain.store.storeManagement.Store;
import domain.user.Member;
import utils.Response;

import java.util.List;

//TODO: can remove admins but at least one has to be in the system.
//TODO: need to add those different functions: gettingInformationOnStore(int storeId), searchProduct(String name, ...),
//TODO: getProduct(int storeId, int productId), checkProductAvailability(int storeId, int productId, int quantity),
//TODO: addProduct(int userId, int storeId, int productId, int quantity), changeQuantity(int userId, int storeId, int productId, int quantity),
//TODO: gettingInformationOnProduct(int storeId, int productId), removeFromCart(int userId, int storeId, int productId),
//TODO: getCartContent(int userId), purchaseCart(int userId), openStore(int userId),
public interface MarketInterface {
    //assumption :
        // guest will be recognized by id
        // member will be recognized by email & id

    //guest methods
    public Response<String> register(String name,String mail ,String pass ,String birthday );
    public Response<String> addProductToCart(int userId,int storeId ,int productId, int quantity);
    public Response<String> removeProductFromCart(int userId,int storeId, int productId);
    public Response<String> changeQuantityInCart(int userId,int storeId, int productId, int change);
    public Response<String> getCart(int userId);
    public Response<String> buy(int userId);

    //member methods
    public Response<String> login(String email , String pass);
    public Response<String> logout(int userId);
    public Response<String> changeDetails(int userId,String name ,String email , String birthday );
    public Response<String> changePassword(int userId,String oldPass ,String newPass);
    public Response<String> openStore(int userId);
    public Response<String> getMember(int userId);
    public Response<String> getUserOrders(int userId);
    public Response<String> writeReviewToStore(int orderId, int storeId, String content, int grading, int userId);
    public Response<String> writeReviewToProduct(int orderId, int storeId,int productId, String content, int grading, int userId);
    public Response<String> getProductInformation(int userId,int storeId , int producId);
    public Response<String> getStoreInformation(int userId , int storeId);
    public Response<String> rateStore(int userId,int storeId,int rating);
    public Response<String> rateProduct(int userId,int storeId,int productId,int rating);
    public Response<String> sendQuestion(int userId,int storeId,String msg);
    public Response<String> sendComplaint(int userId,int storeId,String msg);

    //seller methods
    //todo : is that all what a seller can do?
    public Response<String> sell(int userId,int storeId,int orderId);

    // manager methods
    //todo: miki what purchase and discount policy and constraint should get
    //todo : check if need to make "add constraint" method and "add policy" ...
    public Response<String> appointManager(int userId, int storeId, int managerIdToAppoint);
    public Response<String> changeStoreDescription(int userId,int storeId,String description);
    public Response<String> changePurchasePolicy(int userId,int storeId,String policy);
    public Response<String> changeDiscountPolicy(int userId,int storeId,String policy);
    public Response<String> addPurchaseConstraint(int userId,int storeId,String constraint);
    public Response<String> fireManager(int userId,int storeId,int managerToFire);
    public Response<String> checkWorkersStatus(int userId,int storeId,int workerId);
    public Response<String> viewQuestions(int userId,int storeId);
    public Response<String> answerQuestion(int userId,int storeId ,int questionId,String answer);
    public Response<String> seeStoreHistory(int userId,int storeId);
    public Response<String> addProduct(int useIid, List<String> categories, String name , String description , int price , int quantity);
    public Response<String> deleteProduct(int userId,int storeId,int productId);
    public Response<String> updateProduct(int userId, int storeId,int productId, List<String> categories, String name , String description , int price , int quantity);
    public Response<String> getStoreOrders(int userId , int storeId);

    //store owner methods
    public Response<String> addOwner(int userId , int storeId,int ownerId);
    public Response<String> fireOwner(int userId , int storeId, int ownerId);
    public Response<String> changeManagerPermission (int userId,int storeId, List<Integer> permissionsIds);
    public Response<String> getAppointments(int userId, int storeId);

    //store creator methods
    public Response<String> closeStore(int userId,int storeId);
    public Response<String> reopenStore(int userId,int storeId);

    //store methods
    //todo: decide if getStore will bring every thing togheter , prosucts , orders , ..statistics
    public Response<String> getStore(int storeId);
    public Response<String> getProducts(int storeId);

    // admin methods
    public Response<String> getAdmins();
    public Response<String> getStores();
    public Response<String> addAdmin(int userId, String name , String pass);
    public Response<String> removeAdmin(int userId , int adminId);



}
