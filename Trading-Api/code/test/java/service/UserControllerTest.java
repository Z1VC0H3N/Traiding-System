package service;

import database.dtos.MemberDto;
import database.dtos.NotificationDto;
import database.dtos.ReceiptDto;
import database.dtos.UserHistoryDto;
import domain.store.product.Product;
import domain.store.storeManagement.Store;
import domain.user.Member;
import domain.user.StringChecks;
import market.Admin;
import market.Market;
import org.junit.jupiter.api.Test;
import utils.infoRelated.LoginInformation;
import utils.infoRelated.ProductInfo;
import utils.messageRelated.Notification;
import utils.messageRelated.NotificationOpcode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void enterGuest() {
    }

    @Test
    void exitGuest() {
    }

    @Test
    void register() {
    }

    @Test
    void login() {
    }

    @Test
    void checkPassword() {
    }

    @Test
    void checkDatabase(){
        UserController us = new UserController();
        Admin a = new Admin(1, "elibs@gmail.com", "123Aaa");
        Market market = new Market(a);
        try {
            market.register("eli@gmail.com", "123Aaa",  "24/02/2002");
            market.register("eli2@gmail.com", "123Aaa", "24/02/2002");
            LoginInformation loginInformation = market.login("eli@gmail.com", "123Aaa").getValue();
            int id = loginInformation.getUserId();
            String token = loginInformation.getToken();
            loginInformation = market.login("eli2@gmail.com", "123Aaa").getValue();
            int id2 = loginInformation.getUserId();
            String token2 = loginInformation.getToken();
            market.addNotification(id, NotificationOpcode.CHAT_MESSAGE, "test");
            //Admin a = new Admin(1, "elibs@gmail.com", "123Aaa");
            //us.addAdmin(a, "aaaaaa");
//            Member member = us.getMember(2);
//            Member member2 = us.getMember(3);
//            Store s = new Store(0, "", member);
//            Store store = new Store(1, "nike", "good store","",member2);
            int sid = market.openStore(id,token, "nike", "good store", "da;nen").getValue();
            int sid2 = market.openStore(id2,token2, "adidas", "bad store", "dkndn").getValue();
            market.appointManager(id, token, "eli2@gmail.com", sid);
            List<String> categories = new ArrayList<>();
            categories.add("good");
            int pid = market.addProduct(id, token, sid, categories, "apple", "pink apple", 10, 20, "iifii").getValue();
            categories.add("bad");
            int pid2 = market.addProduct(id, token, sid, categories, "banana", "yellow banana", 7, 15, "ffjfjffii").getValue();
            market.addProductToCart(id, sid, pid, 3);
            market.addProductToCart(id, sid, pid2, 5);
            market.makePurchase(id, "000000000");
            market.saveState();
//            us.updateAdminState(1);
//            us.updateMemberState(2);
//            us.saveMemberState(3);

//            MemberDto m = us.getMemberDto(2);
//            System.out.println(m.getEmail());
//            List<NotificationDto> nlist = us.getSubscriberNotificationsDto(2);
//            for(NotificationDto n : nlist)
//                System.out.println(n.getId() + " " + n.getContent());
//            List<UserHistoryDto> hlist = us.getMemberUserHistoryDto(2);
//            for(UserHistoryDto history : hlist) {
//                System.out.println(history.getReceiptId() + " " + history.getTotalPrice());
//                for(ReceiptDto r : history.getReceiptDtos())
//                    System.out.println(r.getStoreId() + " " + r.getProductId() + " " + r.getQuantity());
//            }
            assert true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            assert false;
        }
    }


    @Test
    void fireManager(){
        UserController us = new UserController();
        try {
            us.register("eli@gmail.com", "123Aaa", "aaaaaa", "24/02/2002");
            us.register( "eli2@gmail.com", "123Aaa", "aaaaaa", "24/02/2002");
            us.login("eli@gmail.com", "aaaaaa");
            us.openStore(1, new Store(0, "nike", "good store", "img", us.getMember(1)));
            us.appointManager(1, "eli2@gmail.com", 0);
            System.out.println(us.getMember(2).getRole(0).getRole());
            us.fireManager(1, 2, 0);
            System.out.println(us.getMember(2).getRole(0).getRole());
            assert false;
        }catch (Exception e){
            assert true;
        }
    }
    @Test
    void checkBirthday() {
        StringChecks sc = new StringChecks();
        assertTrue(sc.checkBirthday("24/02/2002"));
        assertFalse(sc.checkBirthday("24/02/1902"));
        assertFalse(sc.checkBirthday("24/02/2024"));
        assertFalse(sc.checkBirthday("24/05/2060"));
        assertFalse(sc.checkBirthday("29/02/2022"));
        //System.out.println(hour + " " + minute +" " + second);
    }

    @Test
    void checkEmail() {
        StringChecks sc = new StringChecks();
        assertTrue(sc.checkEmail("eliben@gmail.com"));
        assertTrue(sc.checkEmail("eliben1233@gmail.com"));
        assertTrue(sc.checkEmail("benshime@post.bgu.ac.il"));
        assertFalse(sc.checkEmail("elibengmail.com"));
        assertFalse(sc.checkEmail("eliben@gmailcom"));
        assertFalse(sc.checkEmail("eliben@gmail"));
        assertFalse(sc.checkEmail("eliben@gmail.c"));

    }
}