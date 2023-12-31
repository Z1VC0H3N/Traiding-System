package domain.user;

import database.DbEntity;
import database.daos.SubscriberDao;
import jakarta.persistence.*;
import org.hibernate.Session;
import utils.infoRelated.LoginInformation;
import utils.messageRelated.Notification;
import utils.stateRelated.Action;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;




@Entity
@Table(name = "users")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Subscriber implements DbEntity{

    @Id
    protected int id;
    protected String email;
    protected String birthday;
    protected String password;

    @Transient
    protected boolean isConnected;

    @Transient
    protected BlockingQueue<Notification> notifications;

    public Subscriber(){
    }
    public Subscriber(int id, String email, String password){
        this.id = id;
        this.email = email;
        this.password = password;
        this.birthday = "no input";
        notifications = new LinkedBlockingQueue<>();
        isConnected = false;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return email;
    }
    public String getPassword(){return password;}

    public String getBirthday(){return birthday;}

    public void connect(){
        isConnected = true;
    }

    public void disconnect(){
        isConnected = false;
    }
    public boolean getIsConnected(){
        return isConnected;
    }

    public void login(String password) throws Exception{
        if(isConnected)
            throw new Exception("the member is already connected");
        if (this.password.equals(password)) {
            connect();
            return;
        }
        throw new Exception("wrong password");
    }

    public synchronized void addNotification(Notification notification, Session session) throws Exception{
        notification.setSubId(id);
        SubscriberDao.saveNotification(notification, session);
        notifications.offer(notification);
    }

    public List<Notification> displayNotifications(){
        List<Notification> display = new LinkedList<>(notifications);
        notifications.clear();
        return display;
    }

    public Notification getNotification(Session session) throws Exception {
        synchronized (notifications) {
            Notification n = notifications.take();
            if(isConnected) {
                SubscriberDao.removeNotification(n.getId(), session);
                return n;
            }else{
                notifications.offer(n);
                return null;
            }
        }
    }

    public void initialNotificationsFromDb() throws Exception{
        if(notifications == null) {
            notifications = new LinkedBlockingQueue<>();
            List<Notification> notifics = SubscriberDao.getNotifications(id);
            for (Notification n : notifics)
                notifications.offer(n);
        }
    }

    public abstract LoginInformation getLoginInformation(String token);
    public abstract void checkPermission(Action action, int storeId)  throws Exception;
}
