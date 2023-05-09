package service;

import domain.states.StoreManager;
import domain.states.StoreOwner;
import domain.store.storeManagement.Store;
import domain.user.Guest;
import domain.user.Member;

import com.google.gson.Gson;
import utils.messageRelated.Message;
import utils.messageRelated.Notification;
import utils.stateRelated.Action;
import utils.stateRelated.Role;
import utils.userInfoRelated.Info;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


public class UserController {

    ConcurrentHashMap<Integer, Guest> guestList;
    int guestIds;
    ConcurrentHashMap<String, Member> activeMemberList;
    ConcurrentHashMap<String, Member> inActiveMemberList;
    ConcurrentHashMap<Integer, String> idToEmail;
    int memberIds;
    int messageIds;
    private transient Gson gson ;


    public UserController(){
        guestList = new ConcurrentHashMap<>();
        guestIds = 0;
        activeMemberList = new ConcurrentHashMap<>();
        inActiveMemberList = new ConcurrentHashMap<>();
        idToEmail = new ConcurrentHashMap<>();
        memberIds = 1;
        messageIds = 0;
        gson = new Gson();
    }



    public synchronized int enterGuest(){
        Guest g = new Guest(guestIds);
        guestList.put(guestIds, g);
        guestIds+=2;
        return g.getId();
    }



    public void exitGuest(int id) throws Exception {
        if(id % 2 ==0) {
            if(guestList.containsKey(id)) {
                Guest g = guestList.get(id);
                g.emptyCart();
                guestList.remove(id);

            }
            else
                throw new Exception("id given does not belong to any guest");
        }
        else
            throw new Exception("id given is not of guest");
    }

    public synchronized void register(String email, String password, String birthday) throws Exception{
        if(!checkEmail(email))
            throw new Exception("invalid email");
        for (Member m : activeMemberList.values())
            if(m.getEmail().equals(email))
                throw new Exception("the email is already taken");
        for (Member m : inActiveMemberList.values())
            if(m.getEmail().equals(email))
                throw new Exception("the email is already taken");
        if(!checkPassword(password))
            throw new Exception("password not meeting requirements");
        if(!checkBirthday(birthday))
            throw new Exception("birthday not legal");

        Member m = new Member(memberIds, email, password, birthday);
        inActiveMemberList.put(email, m);
        idToEmail.put(memberIds, email);
        memberIds+=2;
    }

    //the answers given are for the security questions, if there are no security questions then put an empty list
    public synchronized int login(String email, String password) throws Exception{
        if(!checkEmail(email))
            throw new Exception("invalid email");
        if(!checkPassword(password))
            throw new Exception("invalid password");
        Member m = inActiveMemberList.get(email);
        if(m == null)
            throw new Exception("no such email");
        try {
            if(m.login(password)) {
                activeMemberList.put(email, inActiveMemberList.remove(email));
                return m.getId();
            }
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
        return -1;
    }

    public void checkSecurityQuestions(int userId, List<String> answers) throws Exception{
        String email = idToEmail.get(userId);
        if(email != null) {
            Member m = inActiveMemberList.get(email);
            if(m != null){
                m.checkSecurityAnswers(answers);
            }
            else
                throw new Exception("no member has this email");
        }
        else
            throw new Exception("no member has this id");
    }

    public boolean hasSecQuestions(int userId) throws Exception{
        Member m = getMember(userId);
        if(m != null){
            return m.hasSecQuestions();
        }
        else
            throw new Exception("no member has this id");
    }

    public HashMap<Integer, Role> getUserRoles(int userId) throws Exception{
        Member m = getMember(userId);
        if(m != null){
            return m.getRoles();
        }
        else
            throw new Exception("no member has this id");
    }

    //when logging out returns to main menu as guest
    public synchronized void logout(int memberId) throws Exception{
        String email = idToEmail.get(memberId);
        if(email != null){
             logout(email);
        }
        else
            throw new Exception("no such id for a member");
    }

    public synchronized void logout(String email) throws Exception{
        Member m =activeMemberList.get(email);
        if (m != null) {
            {
                m.disconnect();
                inActiveMemberList.put(email, activeMemberList.remove(email));
            }
        }
        else
            throw new Exception("member not found");
    }

    //adding the productId to the user's cart with the given quantity
    public synchronized void addProductToCart(int userId, int storeId, int productId, int quantity) throws Exception{
        if(userId % 2 == 0) {
            Guest g = guestList.get(userId);
            g.addProductToCart(storeId, productId, quantity);
        }
        else {
            String email = idToEmail.get(userId);
            if (email != null)
                addProductToCart(email, storeId, productId, quantity);
            else
                throw new Exception("no such member exists");
        }

    }

    public synchronized void addProductToCart(String email, int storeId, int productId, int quantity) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.addProductToCart(storeId, productId, quantity);
        }
        else
            throw new Exception("no such member exists");
    }

    //removing the productId from the user's cart
    public synchronized void removeProductFromCart(int userId, int storeId, int productId) throws Exception{
        if(userId % 2 == 0) {
            Guest g = guestList.get(userId);
            if(g != null)
                g.removeProductFromCart(storeId, productId);
            else
                throw new Exception("no such guest exists");
        }
        else {
            String email = idToEmail.get(userId);
            if (email != null)
                removeProductFromCart(email, storeId, productId);
            else
                throw new Exception("no such member exists");
        }
    }

    public synchronized void removeProductFromCart(String email, int storeId, int productId) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.removeProductFromCart(storeId, productId);
        }
        else
            throw new Exception("no such member exists");
    }


    //adding the change quantity to the product's quantity in the user's cart
    public synchronized void changeQuantityInCart(int userId, int storeId, int productId, int change) throws Exception{
        if(userId % 2 == 0) {
            Guest g = guestList.get(userId);
            if(g != null)
                g.changeQuantityInCart(storeId, productId, change);
            else
                throw new Exception("no such guest exists");
        }
        else {
            String email = idToEmail.get(userId);
            if (email != null)
                changeQuantityInCart(email, storeId, productId, change);
            else
                throw new Exception("no such member exists");
        }
    }
    public synchronized void changeQuantityInCart(String email, int storeId, int productId, int change) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.changeQuantityInCart(storeId, productId, change);
        }
        else
            throw new Exception("no such member exists");
    }

    /**
     * the return of the function is a hashmap between storeId to hashmap of productId to quantity. meaning that it displays the
     * product and quantity for each store.
     * @param userId
     * @return
     * @throws Exception
     */
    public synchronized HashMap<Integer, HashMap<Integer, Integer>>  getUserCart(int userId) throws Exception{
        if(userId % 2 == 0) {
            Guest g = guestList.get(userId);
            if(g != null)
                return  g.getCartContent();
            else
                throw new Exception("no such guest exists");
        }
        else {
            String email = idToEmail.get(userId);
            if (email != null)
                return getUserCart(email);
            else
                throw new Exception("no such member exists");
        }
    }

    public synchronized HashMap<Integer, HashMap<Integer, Integer>>  getUserCart(String email) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
                return m.getCartContent();
        }
        else
            throw new Exception("no such member exists");
    }

    public synchronized void purchaseMade(int userId, int orderId, int totalPrice) throws Exception{
        if (userId % 2 == 1) {
            String email = idToEmail.get(userId);
            if(email != null)
                purchaseMade(orderId, email, totalPrice);
            else
                throw new Exception("no member has such id");
        }
        else{
            Guest g = guestList.get(userId);
            if(g != null)
                g.emptyCart();
            else
                throw new Exception("userId given does not belong to any active user");
        }
    }

    public synchronized void purchaseMade(int orderId, String email, int totalPrice) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.purchaseMade(orderId, totalPrice);
        }
        else
            throw new Exception("no member has that email");
    }


    /**
     * opening a new store in the market
     * @param userId
     * @return
     */
    public synchronized boolean canOpenStore(int userId){

        String email = idToEmail.get(userId);
        if(email == null)
            return false;
        return activeMemberList.get(email) != null;
    }


    public synchronized void openStore(int userId, Store store) throws Exception{
        if(userId % 2 == 0){
            throw new Exception("a guest can't open a store");
        }
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                openStore(email, store);
            else
                throw new Exception("the id does not match any member");
        }
    }

    public synchronized void openStore(String email, Store store) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.openStore(store);
        }
        else
            throw new Exception("the member does not exist");
    }


    /**
     * write a review for a store (can be part of a bigger order).
     * @param orderId
     * @param storeId
     * @param content
     * @param grading
     * @param userId
     * @return
     * @throws Exception
     */
    public synchronized Message writeReviewForStore(int orderId, int storeId, String content, int grading, int userId) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("a guest can't write reviews");
        else{
            String email = idToEmail.get(userId);
            if(email != null) {
                return writeReviewForStore(orderId, storeId, content, grading, email);
            }
            else
                throw new Exception("no member has such id");
        }

    }

    public synchronized Message writeReviewForStore(int orderId, int storeId, String content, int grading, String email) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
            {
                int tmp = memberIds;
                messageIds += 2;
                return m.writeReview(tmp, storeId, orderId, content, grading);
            }
        }
        else
            throw new Exception("no member has this email");

    }

    /**
     * write a review for a product in a store.
     * @param orderId
     * @param storeId
     * @param productId
     * @param comment
     * @param grading
     * @param userId
     * @return
     * @throws Exception
     */
    public synchronized Message writeReviewForProduct(int orderId, int storeId, int productId, String comment, int grading, int userId) throws Exception {
        if (userId % 2 == 0)
            throw new Exception("a guest can't write reviews");
        else {
            String email = idToEmail.get(userId);
            if (email != null)
                return writeReviewForProduct(orderId, storeId, productId, comment, grading, email);
            else
                throw new Exception("no member has such id");
        }
    }

    public synchronized Message writeReviewForProduct(int orderId, int storeId, int productId, String comment, int grading, String email) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
            {
                int tmp = messageIds;
                messageIds += 2;
                return m.writeReview(tmp, storeId, productId, orderId, comment, grading);
            }
        }
        else
            throw new Exception("no member has this email");
    }


    /**
     * write a complaint to a store after a purchase
     * @param orderId
     * @param storeId
     * @param comment
     * @param userId
     * @return
     */
    public synchronized Message writeComplaintToMarket(int orderId, int storeId, String comment,int userId)throws Exception{
        if(userId % 2 == 0)
            throw new Exception("guest can't write complaints");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                return writeComplaintToMarket(orderId, storeId, comment, email);
            else
                throw new Exception("no member has this id");
        }
    }


    public synchronized Message writeComplaintToMarket(int orderId, int storeId, String comment,String email) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
            {
                int tmp = messageIds;
                messageIds += 2;
                return m.writeComplaint(tmp, orderId, storeId, comment);
            }
        }
        else
            throw new Exception("no member has this email");
    }


    public Message sendQuestionToStore(int storeId, String question, int userId) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest can't write a question to a store");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                return sendQuestionToStore(storeId, question, email);
            else
                throw new Exception("no member has this id");
        }
    }


    //TODO:need to check before that the storeId is legal
    public Message sendQuestionToStore(int storeId, String question, String email) throws Exception {
        Member m = activeMemberList.get(email);
        if(m != null) {
            {
                int tmp = messageIds;
                messageIds += 2;
                return m.sendQuestion(tmp, storeId, question);
            }
        }
        else
            throw new Exception("no member has this email");
    }

    /**
     * add notification for a user.
     * @param userId
     * @param notification
     * @throws Exception
     */
    public synchronized void addNotification(int userId, Notification notification) throws Exception{
        if(userId % 2 == 0)
            throw new Exception("guest can't get Notification");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                addNotification(email, notification);
            else
                throw new Exception("no member has this id");
        }
    }

    public synchronized void addNotification(String email, Notification notification) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null) {
            m.addNotification(notification);
        }
        else{
            m = inActiveMemberList.get(email);
            if(m != null)
                m.addNotification(notification);
            else
                throw new Exception("no member has this email");
        }
    }


    /**
     * displays the user's notifications
     * @param userId
     * @return
     * @throws Exception
     */
    public synchronized List<String> displayNotifications(int userId) throws Exception{
        if(userId % 2 == 0)
            throw new Exception("guest doesn't have notifications");
        else{
            String email = idToEmail.get(userId);
            if(email != null){
                return displayNotifications(email);
            }
            else
                throw new Exception("no member has this id");
        }
    }

    public synchronized List<String> displayNotifications(String email) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null){
                return m.displayNotifications();
        }
        else
            throw new Exception("no member has such email");
    }


    /**
     * @param userId the one who asks
     */
    public synchronized HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> getUserPurchaseHistory(int userId, int buyerId) throws Exception {
        if(userId % 2 ==0)
            throw new Exception("guest can't access the user history");
        else if (userId < 0) {
            return adminGetUserPurchaseHistory(buyerId);
        } else{
            String email = idToEmail.get(userId);
            if(email != null)
                return getUserPurchaseHistory(email);
            else
                throw new Exception("no member has this id");
        }
    }

    public synchronized HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> adminGetUserPurchaseHistory(int userId) throws Exception
    {
        Member m = getMember(userId);
        return m.getUserPurchaseHistory();
    }


    public synchronized HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> getUserPurchaseHistory(String email) throws Exception{
        Member m = activeMemberList.get(email);
        if(m!= null)
            return m.getUserPurchaseHistory();
        else {
            m = inActiveMemberList.get(email);
            if(m != null)
                return m.getUserPurchaseHistory();
            else
                throw new Exception("no member has this email");
        }
    }

    //TODO:remember to add into login the option to answer the security questions(if needed)

    /**
     * returns all the user's information
     * @param userId
     * @return
     * @throws Exception
     */
    public synchronized Info getUserPrivateInformation(int userId) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest does not have an email");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                return getUserPrivateInformation(email);
            else
                throw new Exception("no member has this id");
        }

    }

    public synchronized Info getUserPrivateInformation(String email) throws Exception {
        Member m = activeMemberList.get(email);
        if(m != null)
                return m.getPrivateInformation();
        else
            throw new Exception("no member has this email");
    }

    /**
     * function to change the user's email
     * @param userId
     * @param newEmail
     */
    public synchronized void changeUserEmail(int userId, String newEmail) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest does not have an email");
        else{
            String email = idToEmail.get(userId);
            if(email != null && checkEmail(newEmail))
                changeUserEmail(email, newEmail);
            else
                throw new Exception("no member has this id");
        }
    }
    public synchronized void changeUserEmail(String email, String newEmail) throws Exception {
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.setNewEmail(newEmail);
        }
        else
            throw new Exception("no member has this email");
    }

    /**
     * function to change the user's name
     * @param userId
     * @param newName
     */
    public synchronized void changeUserName(int userId, String newName) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest does not have a name");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                changeUserName(email, newName);
            else
                throw new Exception("no member has this id");
        }
    }
    public synchronized void changeUserName(String email, String newName) throws Exception {
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.setNewName(newName);
        }
        else
            throw new Exception("no member has this email");
    }


    /**
     * function to change the user's password
     * @param userId
     * @param oldPassword
     * @param newPassword
     */
    public synchronized void changeUserPassword(int userId, String oldPassword, String newPassword) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest does not have a name");
        else{
            String email = idToEmail.get(userId);
            if(email != null && checkPassword(newPassword))
                changeUserPassword(email, oldPassword, newPassword);
            else
                throw new Exception("no member has this id");
        }
    }
    public synchronized void changeUserPassword(String email, String oldPassword, String newPassword) throws Exception {
        Member m = activeMemberList.get(email);
        if(m != null) {
                m.setNewPassword(oldPassword, newPassword);
        }
        else
            throw new Exception("no member has this email");
    }

    /**
     * member can add security questions for login
     * @param userId
     * @param question
     * @param answer
     * @throws Exception
     */
    public synchronized void addSecurityQuestion(int userId, String question, String answer) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest does not login");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                addSecurityQuestion(email, question, answer);
            else
                throw new Exception("no member has this email");
        }
    }


    public synchronized void addSecurityQuestion(String email, String question, String answer) throws Exception{
        Member m = activeMemberList.get(email);
        if(m != null){
                m.addQuestionForLogin(question, answer);
        }
        else
            throw new Exception("no member has this email");

    }

    public synchronized void changeAnswerForLoginQuestion(int userId, String question, String newAnswer) throws Exception {
        Member m = getMember(userId);
        if(m.getIsConnected())
            m.changeAnswerForQuestion(question, newAnswer);
        else
            throw new Exception("the member is not connected");
    }

    public synchronized void removeSecurityQuestion(int userId, String question) throws Exception {
        Member m = getMember(userId);
        if(m.getIsConnected())
            m.removeSecurityQuestion(question);
        else
            throw new Exception("the member is not connected");
    }


    //starting the functions connecting to the store

    /**
     * appointing a new owner to a store
     * @param ownerId
     * @param appointedId
     * @param storeId
     * @throws Exception
     */
    public synchronized void appointOwner(int ownerId, int appointedId, int storeId) throws Exception {
        if(ownerId % 2 == 0)
            throw new Exception("guest cannot appoint people to a role in a store");
        else{
            String ownerEmail = idToEmail.get(ownerId);
            String appointedEmail = idToEmail.get(appointedId);
            if(ownerEmail != null){
                if(appointedEmail != null){
                    appointOwner(ownerEmail, appointedEmail, storeId);
                }
                else
                    throw new Exception("the appointedId given does not belong to any member");
            }
            else
                throw new Exception("the ownerId given does not belong to any member");
        }
    }

    public synchronized void appointOwner(String ownerEmail, String appointedEmail, int storeId) throws Exception{
        Member owner = activeMemberList.get(ownerEmail);
        Member appointed = activeMemberList.get(appointedEmail);
        if(appointed == null)
        {
            appointed = inActiveMemberList.get(appointedEmail);
        }
        if(owner != null){
            if(appointed != null){
                if(owner.getIsConnected()){
                    if(appointed.checkRoleInStore(storeId) != Role.Owner) {
                        Store store = owner.appointToOwner(appointed.getId(), storeId);
                        Notification<String> notify = new Notification<>("you have been appointed to owner in store: " + storeId);
                        appointed.addNotification(notify);
                        appointed.changeRoleInStore(storeId, new StoreOwner(), store);
                    }
                    else
                        throw new Exception("the member already is a owner in this store");
                }
                else
                    throw new Exception("the member is not connected so he can't appoint");
            }
            else
                throw new Exception("no member has this email: "+appointedEmail);
        }
        else
            throw new Exception("no member has this email: "+ownerEmail);
    }

    /**
     * firing an owner from the store
     * @param ownerId
     * @param appointedId
     * @param storeId
     * @throws Exception
     */
    public synchronized void fireOwner(int ownerId, int appointedId, int storeId) throws Exception {
        if(ownerId % 2 == 0)
            throw new Exception("guest cannot fire people from a role in a store");
        else{
            String ownerEmail = idToEmail.get(ownerId);
            String appointedEmail = idToEmail.get(appointedId);
            if(ownerEmail != null){
                if(appointedEmail != null){
                    fireOwner(ownerEmail, appointedEmail, storeId);
                }
                else
                    throw new Exception("the appointedId given does not belong to any member");
            }
            else
                throw new Exception("the ownerId given does not belong to any member");
        }
    }

    public synchronized void fireOwner(String ownerEmail, String appointedEmail, int storeId) throws Exception {
        Member owner = activeMemberList.get(ownerEmail);
        Member appointed = activeMemberList.get(appointedEmail);
        if(appointed == null)
        {
            appointed = inActiveMemberList.get(appointedEmail);
        }
        if(owner != null){
            if(appointed != null){
                if(owner.getIsConnected()){
                    if(appointed.checkRoleInStore(storeId) == Role.Owner) {
                            Set<Integer> firedIds = owner.fireOwner(appointed.getId(), storeId);
                            firedIds.remove(appointed.getId());
                            for (int firedId : firedIds) {
                                Member fired = getMember(firedId);
                                if (fired.checkRoleInStore(storeId) == Role.Owner) {
                                    Notification<String> notify = new Notification<>("you have been fired from owner in store: " + storeId);
                                    fired.addNotification(notify);
                                    fired.removeRoleInStore(storeId);
                                } else {
                                    Notification<String> notify = new Notification<>("you have been fired from manager in store: " + storeId);
                                    fired.addNotification(notify);
                                    fired.removeRoleInStore(storeId);
                                }
                            }
                            Notification<String> notify = new Notification<>("you have been fired from owner in store: " + storeId);
                            appointed.addNotification(notify);
                            appointed.removeRoleInStore(storeId);
                    }
                    else
                        throw new Exception("the member is not a owner in this store");
                }
                else
                    throw new Exception("the member is not connected so he can't appoint");
            }
            else
                throw new Exception("no member has this email: "+appointedEmail);
        }
        else
            throw new Exception("no member has this email: "+ownerEmail);
    }

    /**
     * appointing a new manager to a store
     * @param ownerId
     * @param appointedId
     * @param storeId
     * @throws Exception
     */
    public synchronized void appointManager(int ownerId, int appointedId, int storeId) throws Exception {
        if(ownerId % 2 == 0)
            throw new Exception("guest cannot appoint people to a role in a store");
        else{
            String ownerEmail = idToEmail.get(ownerId);
            String appointedEmail = idToEmail.get(appointedId);
            if(ownerEmail != null){
                if(appointedEmail != null){
                    appointManager(ownerEmail, appointedEmail, storeId);
                }
                else
                    throw new Exception("the appointedId given does not belong to any member");
            }
            else
                throw new Exception("the ownerId given does not belong to any member");
        }

    }

    public synchronized void appointManager(String ownerEmail, String appointedEmail, int storeId) throws Exception{
        Member owner = activeMemberList.get(ownerEmail);
        Member appointed = activeMemberList.get(appointedEmail);
        if (appointed  == null)
        {
            appointed = inActiveMemberList.get(appointedEmail);
        }
        if(owner != null){
            if(appointed != null){
                if(owner.getIsConnected()) {
                    if (appointed.checkRoleInStore(storeId) != Role.Manager && appointed.checkRoleInStore(storeId) != Role.Owner) {
                        Store store = owner.appointToManager(appointed.getId(), storeId);
                        Notification<String> notify = new Notification<>("you have been appointed to manager in store: " + storeId);
                        appointed.addNotification(notify);
                        appointed.changeRoleInStore(storeId, new StoreManager(), store);
                    }
                    else
                        throw new Exception("the member already is a manager in this store");
                }
                else
                    throw new Exception("the owner is not connected so he can't appoint");
            }
            else
                throw new Exception("no member has this email: "+appointedEmail);
        }
        else
            throw new Exception("no member has this email: "+ownerEmail);
    }

    /**
     * firing a manager from the store
     * @param ownerId
     * @param appointedId
     * @param storeId
     * @throws Exception
     */
    public synchronized void fireManager(int ownerId, int appointedId, int storeId) throws Exception {
        if(ownerId % 2 == 0)
            throw new Exception("guest cannot fire people from a role in a store");
        else{
            String ownerEmail = idToEmail.get(ownerId);
            String appointedEmail = idToEmail.get(appointedId);
            if(ownerEmail != null){
                if(appointedEmail != null){
                    fireManager(ownerEmail, appointedEmail, storeId);
                }
                else
                    throw new Exception("the appointedId given does not belong to any member");
            }
            else
                throw new Exception("the ownerId given does not belong to any member");
        }
    }

    public synchronized void fireManager(String ownerEmail, String appointedEmail, int storeId) throws Exception {
        Member owner = activeMemberList.get(ownerEmail);
        Member appointed = activeMemberList.get(appointedEmail);
        if(appointed == null)
        {
            appointed = inActiveMemberList.get(appointedEmail);
        }
        if(owner != null){
            if(appointed != null){
                if(owner.getIsConnected()){
                    if(appointed.checkRoleInStore(storeId) == Role.Manager) {
                        owner.fireManager(appointed.getId(), storeId);
                        Notification<String> notify = new Notification<>("you have been fired from manager in store: " + storeId);
                        appointed.addNotification(notify);
                        appointed.removeRoleInStore(storeId);
                    }
                    else
                        throw new Exception("the member is not a manager in this store");
                }
                else
                    throw new Exception("the member is not connected so he can't appoint");
            }
            else
                throw new Exception("no member has this email: "+appointedEmail);
        }
        else
            throw new Exception("no member has this email: "+ownerEmail);
    }


    /**
     * adding a new permission to manager
     * @param ownerId
     * @param managerId
     * @param a
     * @param storeId
     * @throws Exception
     */
    public synchronized void addManagerAction(int ownerId, int managerId, Action a, int storeId) throws Exception {
        String managerEmail = idToEmail.get(managerId);
        String ownerEmail = idToEmail.get(ownerId);
        if(ownerEmail != null) {
            if (managerEmail != null){
                addManagerAction(ownerEmail, managerEmail, a, storeId);
            }
            else
                throw new Exception("the managerId: " + managerId + " given goes not belong to any member");
        }
        else
            throw new Exception("the ownerId: " + ownerId + " given goes not belong to any member");
    }

    public synchronized void addManagerAction(String ownerEmail, String managerEmail, Action a, int storeId) throws Exception {
        Member owner = activeMemberList.get(ownerEmail);
        Member manager = activeMemberList.get(managerEmail);
        if(manager == null)
        {
            manager = inActiveMemberList.get(managerEmail);
        }
        if(owner != null){
            if(manager != null){
                if(owner.getIsConnected()){
                    if(manager.checkRoleInStore(storeId) == Role.Manager) {
                        manager.addAction(a, storeId);
                        Notification<String> notify = new Notification<>("the following action: "  + a.toString() + "\n" +
                                "has been added for you for store: " + storeId);
                        manager.addNotification(notify);
                    }
                    else
                        throw new Exception("the member is not a manager in this store");
                }
                else
                    throw new Exception("the member is not connected so he can't appoint");
            }
            else
                throw new Exception("no member has this email: "+managerEmail);
        }
        else
            throw new Exception("no member has this email: "+ownerEmail);
    }

    /**
     * remove an action for the manager
     * @param ownerId
     * @param managerId
     * @param a
     * @param storeId
     * @throws Exception
     */
    public synchronized void removeManagerAction(int ownerId, int managerId, Action a, int storeId) throws Exception {
        String managerEmail = idToEmail.get(managerId);
        String ownerEmail = idToEmail.get(ownerId);
        if(ownerEmail != null) {
            if (managerEmail != null){
                removeManagerAction(ownerEmail, managerEmail, a, storeId);
            }
            else
                throw new Exception("the managerId: " + managerId + " given goes not belong to any member");
        }
        else
            throw new Exception("the ownerId: " + ownerId + " given goes not belong to any member");
    }

    public synchronized void removeManagerAction(String ownerEmail, String managerEmail, Action a, int storeId) throws Exception {
        Member owner = activeMemberList.get(ownerEmail);
        Member manager = activeMemberList.get(managerEmail);
        if(manager == null)
        {
            manager = inActiveMemberList.get(managerEmail);
        }
        if(owner != null){
            if(manager != null){
                if(owner.getIsConnected()){
                    if(manager.checkRoleInStore(storeId) == Role.Manager) {
                        manager.removeAction(a, storeId);
                        Notification<String> notify = new Notification<>("the following action: "  + a.toString() + "\n" +
                                "has been removed for you for store: " + storeId);
                        manager.addNotification(notify);
                    }
                    else
                        throw new Exception("the member is not a manager in this store");
                }
                else
                    throw new Exception("the member is not connected so he can't appoint");
            }
            else
                throw new Exception("no member has this email: "+managerEmail);
        }
        else
            throw new Exception("no member has this email: "+ownerEmail);
    }


    /**
     * closing a store temporarily
     * @param userId
     * @param storeId
     * @throws Exception
     */
    public synchronized void closeStore(int userId, int storeId) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest can't close stores");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                closeStore(email, storeId);
            else
                throw new Exception("no member has this id: " + userId);
        }

    }

    public synchronized void closeStore(String userEmail, int storeId) throws Exception {
        Member m = activeMemberList.get(userEmail);
        if(m != null) {
            Set<Integer> workerIds = m.closeStore(storeId);
            //workerIds.remove(m.getId());
            for(int workerId : workerIds){
                String email = idToEmail.get(workerId);
                if(email != null) {
                    Member worker = activeMemberList.get(email);
                    if(worker == null)
                        worker = inActiveMemberList.get(email);
                    if(worker != null) {
                        worker.changeToInactive(storeId);
                        String notify = "the store: " + storeId + " has been temporarily closed";
                        Notification<String> notification = new Notification<>(notify);
                        addNotification(workerId, notification);
                    }
                    else
                        throw new Exception("the set for closeStore contains an id that its corresponding email does not belong" +
                                " to any member");
                }
                else
                    throw new Exception("the set for closeStore contains an id of a non existent member");
            }
        }
        else
            throw new Exception("no member has this email: " + userEmail);
    }


    /**
     * reopening a store
     * @param userId
     * @param storeId
     * @throws Exception
     */
    public synchronized void reOpenStore(int userId, int storeId) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("guest can't close stores");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                reOpenStore(email, storeId);
            else
                throw new Exception("no member has this id: " + userId);
        }

    }

    public synchronized void reOpenStore(String userEmail, int storeId) throws Exception {
        Member m = activeMemberList.get(userEmail);
        if(m != null) {
            Set<Integer> workerIds = m.reOpenStore(storeId);
            //workerIds.remove(m.getId());
            for(int workerId : workerIds){
                String email = idToEmail.get(workerId);
                if(email != null) {
                    Member worker = activeMemberList.get(email);
                    if(worker == null)
                        worker = inActiveMemberList.get(email);
                    if(worker != null) {
                        worker.changeToActive(storeId);
                        String notify = "the store: " + storeId + " has been reOpened";
                        Notification<String> notification = new Notification<>(notify);
                        addNotification(workerId, notification);
                    }
                    else
                        throw new Exception("the set for closeStore contains an id that its corresponding email does not belong" +
                                " to any member");
                }
                else
                    throw new Exception("the set for closeStore contains an id of a non existent member");
            }
        }
        else
            throw new Exception("no member has this email: " + userEmail);
    }


    /**
     * checks if the user can do a specific action in a store
     * @param userId
     * @param action
     * @param storeId
     * @return
     * @throws Exception
     */
    public synchronized boolean checkPermission(int userId, Action action, int storeId) throws Exception {
        if(userId % 2 == 0)
            return false;
        else if(userId < 0)
            return true;
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                return checkPermission(email, action, storeId);
            else
                throw new Exception("no member has this id: " + userId);
        }

    }

    public synchronized boolean checkPermission(String userEmail, Action action, int storeId) throws Exception {
        Member m = activeMemberList.get(userEmail);
        if(m != null)
            return m.checkPermission(action, storeId);
        else
            throw new Exception("no member has this email: " + userEmail);
    }


    /**
     * getting information about the workers in a store
     * @param userId
     * @param storeId
     * @return
     */

    public synchronized  Info getWorkerInformation(int userId, int workerId, int storeId) throws Exception{
        if(userId % 2 == 0)
            throw new Exception("guest can't see workers information");
        else{
            String userEmail = idToEmail.get(userId);
            String workerEmail = idToEmail.get(workerId);
            if(userEmail != null && workerEmail != null)
                return getWorkerInformation(userEmail, workerId, storeId);
            else
                throw new Exception("no member has this id");
        }
    }

    public synchronized Info getWorkerInformation(String userEmail, int workerId, int storeId) throws Exception{
        Member m = activeMemberList.get(userEmail);
        if(m != null) {
            if (m.checkPermission(Action.checkWorkersStatus, storeId)) {
                if(m.getWorkerIds(storeId).contains(workerId)){
                    Member worker = getMember(workerId);
                    return worker.getInformation(storeId);
                }
                else
                    throw new Exception("the workerId given is not of a worker in the store");
            }
            else
                throw new Exception("the member can't see workers status in this store");
        }
        else
            throw new Exception("no member has this email: " + userEmail);

    }
    public synchronized List<Info> getWorkersInformation(int userId, int storeId) throws Exception{
        if(userId % 2 == 0)
            throw new Exception("guest can't see workers information");
        else{
            String email = idToEmail.get(userId);
            if(email != null)
                return getWorkersInformation(email, storeId);
            else
                throw new Exception("no member has this id");
        }
    }


    public synchronized List<Info> getWorkersInformation(String email, int storeId) throws Exception {
        Member m = activeMemberList.get(email);
        if(m != null){
            if(m.checkPermission(Action.checkWorkersStatus, storeId)){
                List<Info> information = new LinkedList<>();
                Set<Integer> workerIds = m.getWorkerIds(storeId);
                for (int workerId : workerIds) {
                    Member worker = getMember(workerId);
                    Info info = worker.getInformation(storeId);
                    information.add(info);
                }
                return information;
            }
            else
                throw new Exception("the member can't see workers status in this store");
        }
        else
            throw new Exception("no member has this email: " + email);
    }


    /**
     * function to make things easier, not being used outside userController
     * @param userId
     * @return
     * @throws Exception
     */
    private synchronized Member getMember(int userId) throws Exception {
        if(userId % 2 == 0)
            throw new Exception("the id given is of a guest");
        else{
           String email = idToEmail.get(userId);
           if(email != null){
               Member m = activeMemberList.get(email);
               if( m != null)
                   return m;
               else {
                   if ((m = inActiveMemberList.get(email)) != null)
                   {
                       return m;
                   }
                   throw new Exception("no member has this email");
               }
           }
           else
               throw new Exception("no member has this id");

        }
    }

    public synchronized String getUserEmail(int userId) throws Exception {
        Member m = getMember(userId);
        return m.getEmail();
    }


    //check that the format user@domain.com exists
    public boolean checkEmail(String email){
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z0-9-]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
    }


    //the password length is between 6 and 20, need 1 small letter, 1 big letter and 1 number at least.
    // all other characters are not allowed
    public boolean checkPassword(String password){
        int countSmall = 0;
        int countBig = 0;
        int countNum = 0;
        if(password.length()<6 || password.length() > 20)
            return false;
        for(int i = 0; i<password.length(); i++){
            if(password.charAt(i)>=48 && password.charAt(i)<=57)
                countNum++;
            else if(password.charAt(i)>=65 && password.charAt(i)<=90)
                countBig++;
            else if(password.charAt(i)>=97 && password.charAt(i)<=122)
                countSmall++;
            else
                return false;
        }
        return countNum != 0 && countBig != 0 && countSmall != 0;
    }

    //birthdays are written in the format: dd/mm/yyyy. only integers and '/'
    //check for 1<=month<=12 and that the day is good accordingly
    //checks for an age that makes sense (0<=age<=120).
    public boolean checkBirthday(String birthday){
        int[] validDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        for(int i = 0; i<birthday.length(); i++)
            if(birthday.charAt(i) < 47 || birthday.charAt(i) > 57)
                return false;

        String[] splitBDay = birthday.split("/");
        int[] bDay = {Integer.parseInt(splitBDay[0]), Integer.parseInt(splitBDay[1]), Integer.parseInt(splitBDay[2])};
        if(bDay[1] <1 || bDay[1] > 12)
            return false;
        if(validDay[bDay[1] - 1] < bDay[0] || bDay[0] < 1)
            return false;

        String currentDate = String.valueOf(java.time.LocalDate.now());
        String[] splitCurrentDay = currentDate.split("-");
        int[] curDay = {Integer.parseInt(splitCurrentDay[0]), Integer.parseInt(splitCurrentDay[1]),
                Integer.parseInt(splitCurrentDay[2])};
        int tmp = curDay[0];
        curDay[0] = curDay[2];
        curDay[2] = tmp;

        if(bDay[2] > curDay[2] || (bDay[2] == curDay[2] && bDay[1] > curDay[1]) ||
                (bDay[2] == curDay[2] && bDay[1] == curDay[1] && bDay[0] > curDay[0]))
                return false;
        if(bDay[2] < (curDay[2] - 120))
            return false;

        return true;
    }

    public String getUserName(int id) throws Exception
    {
        if (id%2==0 && guestList.containsKey(id))
        {
            if (guestList.containsKey(id))
            {
                return String.valueOf(guestList.get(id).getId());
            }
            throw new Exception("guest doesn't exist");
        }
        else {
            String email = idToEmail.get(id);
            if (email != null)
            {
                Member m = activeMemberList.get(email);
                if(m != null && m.getIsConnected())
                    return activeMemberList.get(email).getName();
            }
            throw new Exception("user doesnt exist");

        }
    }

    /**
     * removes the role because the store has been permanently closed
     * @param adminId
     * @param userId
     * @param storeId
     */
    public void removeStoreRole(int adminId, int userId, int storeId) throws Exception {
        if(adminId < 0){
            Member m = getMember(userId);
            m.removeRoleInStore(storeId);
        }
        else{
            throw new Exception("the id given is not of an admin");
        }
    }

    public List<Integer> cancelMembership(int userToRemove) throws Exception{
        Member m = getMember(userToRemove);
        Set<Integer> storeIds = m.getAllStoreIds();
        List<Integer> creatorStoreIds = new LinkedList<>();
        for(int storeId : storeIds){
            if(m.checkRoleInStore(storeId) == Role.Manager)
                fireManager(userToRemove, userToRemove, storeId);
            else if(m.checkRoleInStore(storeId) == Role.Owner)
                fireOwner(userToRemove, userToRemove, storeId);
            else
                creatorStoreIds.add(storeId);
        }
        activeMemberList.remove(idToEmail.get(userToRemove));
        inActiveMemberList.remove(idToEmail.get(userToRemove));
        idToEmail.remove(userToRemove);
        return creatorStoreIds;
    }

    public List<HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>> getUsersInformation() {
        List<HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>> membersInformation = new LinkedList<>();
        for(Member m : activeMemberList.values()) {
            HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> history = m.getUserPurchaseHistory();
            membersInformation.add(history);
        }
        for(Member m : inActiveMemberList.values()){
            HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> history = m.getUserPurchaseHistory();
            membersInformation.add(history);
        }
        return membersInformation;
    }

}