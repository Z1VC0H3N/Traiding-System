package domain.states;

import database.daos.Dao;
import database.DbEntity;
import database.daos.StoreDao;
import domain.store.storeManagement.Store;
import domain.user.Member;
import jakarta.persistence.*;
import org.json.JSONObject;
import utils.infoRelated.Information;
import utils.stateRelated.Action;
import utils.stateRelated.Role;

import java.util.List;
import java.util.Set;


@Entity
@Table(name = "roles")
public abstract class UserState extends Information implements DbEntity {


    @Id
    protected int userId;

    @Id
    protected int storeId;
    @Transient
    protected Store store;
    protected String userName;
    @Transient
    protected Permissions permissions; //saves all the permission a user has for a store.

    @Enumerated(EnumType.STRING)
    protected Role role;

    protected boolean isActive;

    public UserState(){
    }

    public UserState(int memberId, String name, Store store){
        this.userId = memberId;
        this.userName = name;
        this.store = store;
        if(store != null)
            this.storeId = store.getStoreId();
        permissions = new Permissions();
        isActive = true;


    }

    public List<Action> getActions() {
        return permissions.getActions();
    }
    public List<Action> getPossibleActions(){return permissions.getAddedActions();}


    public Store getStore(){return store;}

    public int getUserId(){return userId;}
    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }
    public boolean isActive(){
        return isActive;
    }
    public boolean checkPermission(Action a){
        return permissions.checkPermission(a);
    }
    public Role getRole(){
        return role;
    }

    public boolean checkHasAvailableAction(Action a){
        return permissions.checkAvailablePermission(a);
    }

    public void addAction(Action a) throws Exception{
        throw new Exception("cannot add action to role: " + role);
    }

    public void removeAction(Action a) throws Exception{
        throw new Exception("cannot remove action to role: " + role);
    }

    public void appointManager(Member appointed) throws Exception{
        checkPermission(Action.appointManager);
        StoreManager m = new StoreManager(appointed.getId(), appointed.getName(), store);
        store.appointUser(userId, appointed, m);
        appointed.changeRoleInStore(m, store);
    }

    public Set<Integer> fireManager(int appointedId) throws Exception{
        if(userId == appointedId)
            return store.fireUser(appointedId);
        checkPermission(Action.fireManager);
        return store.fireUser(appointedId);
    }

    public void appointOwner(Member appointed) throws Exception{
        checkPermission(Action.appointOwner);
        StoreOwner s = new StoreOwner(appointed.getId(), appointed.getName(), store);
        store.appointUser(userId, appointed, s);
        appointed.changeRoleInStore(s, store);
    }

    public Set<Integer> fireOwner(int appointedId) throws Exception{
        if(userId == appointedId)
            return store.fireUser(appointedId);
        checkPermission(Action.fireOwner);
        return store.fireUser(appointedId);
    }

    public Set<Integer> closeStore() throws Exception{
        checkPermission(Action.closeStore);
        Set<Integer> ans = store.closeStoreTemporary(userId);
        Dao.save(store);
        return ans;
    }

    public Set<Integer> reOpenStore() throws Exception{
        checkPermission(Action.reopenStore);
        Set<Integer> ans = store.reopenStore(userId);
        Dao.save(store);
        return ans;
    }

    public Set<Integer> getWorkerIds() throws Exception{
        checkPermission(Action.checkWorkersStatus);
        return store.getUsersInStore();
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
    public String getUserName(){
        return this.userName;
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("userName", userName);
        json.put("storeRole", role.toString());
        json.put("actions", fromActionToString(getActions()));
        return json;
    }

    //database

    @Override
    public void initialParams(){
        getStoreFromDb();
        getPermissionsFromDb();
    }

    protected void getStoreFromDb(){
        if(store == null){
            store = StoreDao.getStore(storeId);
        }
    }

    protected abstract void getPermissionsFromDb();

    protected void getPermissionsHelp(){
        if(permissions == null) {
            permissions = new Permissions();
            List<? extends DbEntity> permissionsDto = Dao.getListByCompositeKey(Permission.class, userId, storeId,
                    "Permission", "userId", "storeId");
            for (Permission p : (List<Permission>) permissionsDto)
                permissions.addAction(p.getPermission());
        }
    }
}
