package domain.states;

import database.daos.Dao;
import domain.store.storeManagement.Store;
import jakarta.persistence.Entity;
import org.hibernate.Session;
import utils.stateRelated.Action;
import utils.stateRelated.Role;

import java.util.LinkedList;
import java.util.List;

@Entity
public class StoreOwner extends UserState {

    public StoreOwner(){
    }

    public  StoreOwner(int memberId, String name, Store store){
        super(memberId, name, store);
        role = Role.Owner;
        List<Action> actions = new LinkedList<>();

        actions.add(Action.appointManager);
        actions.add(Action.changeStoreDetails);
        actions.add(Action.deletePurchasePolicy);
        actions.add(Action.deleteDiscountPolicy);
        actions.add(Action.addPurchaseConstraint);
        actions.add(Action.fireManager);
        actions.add(Action.checkWorkersStatus);
        actions.add(Action.viewMessages);
        actions.add(Action.answerMessage);
        actions.add(Action.seeStoreHistory);
        actions.add(Action.seeStoreOrders);
        actions.add(Action.addProduct);
        actions.add(Action.removeProduct);
        actions.add(Action.updateProduct);
        actions.add(Action.addDiscountConstraint);

        actions.add(Action.appointOwner);
        actions.add(Action.fireOwner);
        actions.add(Action.changeManagerPermission);

        permissions.addActions(actions);
    }

    @Override
    protected void getPermissionsFromDb() throws Exception {
        getPermissionsHelp();
    }
}
