package domain.store.purchase;

import org.json.JSONObject;
import org.mockito.internal.matchers.Null;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PurchasePolicyFactory {
    AtomicInteger policyIds;
    int storeId;
    public PurchasePolicyFactory(AtomicInteger policyIds,int storeId){
        this.policyIds = policyIds;
        this.storeId = storeId;
    }
    public PurchasePolicy createPolicy() throws Exception{
        throw new Exception("Place Holder for the real function create policy");
    }
    public PurchasePolicy createPolicy(PurchasePolicyDataObject policyData) throws Exception {
        PurchasePolicy policy = switch (policyData.type) {
            case Category -> createCategoryPolicy(policyData);
            case Item -> createItemPolicy(policyData);
            case User -> createUserPolicy(policyData);
            case Basket -> createBasketPolicy(policyData);
            case DateTime -> createDateTimePolicy(policyData);
        };
        if(policyData.next!=null){
            policy.setNext(createPolicy(policyData.next),policyData.composure);
        }
        return policy;
    }

    private PurchasePolicy createDateTimePolicy(PurchasePolicyDataObject policyData) throws Exception {
        if(policyData.dateLimit == null && policyData.timeLimit == null)
            throw new Exception("Some information is missing in creating dateTime policy, please check given date or time");
        if(policyData.limiter == null || policyData.category == null){
            throw new Exception("Some information is missing in creating dateTime policy, please check- limiter, category");
        }
        return new DateTimePolicy(policyData.policyID, policyData.storeID, policyData.category, policyData.limiter, policyData.dateLimit, policyData.timeLimit);
    }

    private PurchasePolicy createBasketPolicy(PurchasePolicyDataObject policyData) throws Exception {
        if(policyData.productID < 0 || policyData.amount ==0){
            throw new Exception("Some information is missing in creating basket purchase policy, please check all required fields: \n productID, amount");
        }
        return new BasketPolicy(policyData.policyID, policyData.storeID, policyData.productID, policyData.amount);
    }

    private PurchasePolicy createUserPolicy(PurchasePolicyDataObject policyData) throws Exception {
        if(policyData.ageLimit>=0 && policyData.productID >=0 && policyData.limiter!=null){
            return new UserPolicy(policyData.policyID, policyData.storeID, policyData.ageLimit, policyData.productID,policyData.limiter);
        }
        if(policyData.ageLimit>=0 && !Objects.equals(policyData.category,"") && policyData.limiter!=null){
            return new UserPolicy(policyData.policyID, policyData.storeID, policyData.ageLimit, policyData.category,policyData.limiter);
        }
        throw new Exception("Some information is missing in creating user purchase policy, please check all required fields:\n productID or Category, ageLimit,limiter");
    }

    private PurchasePolicy createItemPolicy(PurchasePolicyDataObject policyData) throws Exception {
        if(policyData.productID < 0 || policyData.limiter ==null || policyData.amount == 0){
            throw new Exception("Some Information is missing in creating item purchase policy, please check all required fields:\n productID,amount,limiter");
        }
        return new ItemPolicy(policyData.policyID, policyData.storeID, policyData.productID, policyData.amount, policyData.limiter);
    }

    public PurchasePolicy createCategoryPolicy(PurchasePolicyDataObject policyData) throws Exception {
        if(Objects.equals(policyData.category, "") || policyData.amount == 0 || policyData.limiter == null){
            throw new Exception("Some Information is missing in creating category purchase policy, please check all required fields:\n category,amount,limiter");
        }
        return new CategoryPolicy(policyData.policyID,policyData.storeID, policyData.category, policyData.amount, policyData.limiter);
    }

    public PurchasePolicyDataObject parseDateTime(JSONObject policy, PurchasePolicyDataObject prev) {
        String timeType = policy.get("timeType").toString();
        int[] timeLimit = null;
        int[] dateLimit = null;
        switch (timeType){
            case "Time Limit" -> {
                String[] time =  policy.get("timeLimit").toString().split(":");
                timeLimit = new int[]{Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2])};
            }
            case "Date Limit" -> {
                String[] date = policy.get("timeLimit").toString().split("/");
                dateLimit = new int[]{Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2])};
            }
        }
        String category = policy.get("category").toString();
        PurchasePolicy.limiters limiter = getLimiter(policy.get("limiter").toString());
        PurchasePolicy.policyTypes type = getPolicyType(policy.get("type").toString());
        PurchasePolicy.policyComposeTypes compose = getPolicyCompose(policy.get("compose").toString());
        PurchasePolicyDataObject dataObj = new PurchasePolicyDataObject(policyIds.getAndIncrement(),storeId,policy.toString(),limiter,
                -1,-1,category,-1,dateLimit,timeLimit,null,compose,type);
        if (prev != null){
            prev.next = dataObj;
            prev = null;
        }
        if(compose!=null){
            prev = dataObj;
        }
        return dataObj;
    }

    public PurchasePolicyDataObject parseBasket(JSONObject policy, PurchasePolicyDataObject prev) {
        int productId = Integer.parseInt(policy.get("productId").toString());
        int amount = Integer.parseInt(policy.get("amount").toString());
        PurchasePolicy.policyTypes type = getPolicyType(policy.get("type").toString());
        PurchasePolicy.policyComposeTypes compose = getPolicyCompose(policy.get("composore").toString());
        int[] nullVal = null;
        PurchasePolicyDataObject dataObj = new PurchasePolicyDataObject(policyIds.getAndIncrement(),storeId,policy.toString(),null,productId,
                -1,"" ,amount,nullVal,nullVal,null,compose,type);
        if (prev != null){
            prev.next = dataObj;
            prev = null;
        }
        if(compose!=null){
            prev = dataObj;
        }
        return dataObj;
    }

    public PurchasePolicyDataObject parseUser(JSONObject policy, PurchasePolicyDataObject prev) {
        int productId = Integer.parseInt(policy.get("productId").toString());
        String category = policy.get("category").toString();
        int ageLimit = Integer.parseInt(policy.get("ageLimit").toString());
        PurchasePolicy.limiters limiter = getLimiter(policy.get("limiter").toString());
        PurchasePolicy.policyTypes type = getPolicyType(policy.get("type").toString());
        PurchasePolicy.policyComposeTypes compose = getPolicyCompose(policy.get("composore").toString());
        int[] nullVal = null;
        PurchasePolicyDataObject dataObj;
        if(!Objects.equals(category, "")){
            dataObj = new PurchasePolicyDataObject(policyIds.getAndIncrement(),storeId,policy.toString(),limiter,-1,
                    ageLimit,category,-1,nullVal,nullVal,null,compose,type);
        }
        else {
            dataObj = new PurchasePolicyDataObject(policyIds.getAndIncrement(), storeId, policy.toString(), limiter, productId,
                    ageLimit, "", -1, nullVal, nullVal, null, compose, type);
        }
        if (prev != null){
            prev.next = dataObj;
            prev = null;
        }
        if(compose!=null){
            prev = dataObj;
        }
        return dataObj;
    }

    public PurchasePolicyDataObject parseCategory(JSONObject policy, PurchasePolicyDataObject prev) {
        String category = policy.get("category").toString();
        int amount = Integer.parseInt(policy.get("amount").toString());
        PurchasePolicy.limiters limiter = getLimiter(policy.get("limiter").toString());
        PurchasePolicy.policyTypes type = getPolicyType(policy.get("type").toString());
        PurchasePolicy.policyComposeTypes compose = getPolicyCompose(policy.get("composore").toString());
        int[] nullVal = null;
        PurchasePolicyDataObject dataObj = new PurchasePolicyDataObject(policyIds.getAndIncrement(),storeId,policy.toString(),limiter,-1,
                -1,category,amount,nullVal,nullVal,null,compose,type);
        if (prev != null){
            prev.next = dataObj;
            prev = null;
        }
        if(compose!=null){
            prev = dataObj;
        }
        return dataObj;
    }

    public PurchasePolicyDataObject parseItem(JSONObject policy,PurchasePolicyDataObject prev) {
        int amount = Integer.parseInt(policy.get("amount").toString());;
        int productId = Integer.parseInt(policy.get("productId").toString());
        PurchasePolicy.limiters limiter = getLimiter(policy.get("limiter").toString());
        PurchasePolicy.policyTypes type = getPolicyType(policy.get("type").toString());
        PurchasePolicy.policyComposeTypes compose = getPolicyCompose(policy.get("composore").toString());
        int[] nullVal = null;
        PurchasePolicyDataObject dataObj = new PurchasePolicyDataObject(policyIds.getAndIncrement(),storeId,policy.toString(),limiter,productId,
                -1,"",amount,nullVal,nullVal,null,compose,type);
        if (prev != null){
            prev.next = dataObj;
            prev = null;
        }
        if(compose!=null){
            prev = dataObj;
        }
        return dataObj;

    }

    private PurchasePolicy.policyComposeTypes getPolicyCompose(String composore) {
        return switch (composore){
            case "And" -> PurchasePolicy.policyComposeTypes.PolicyAnd;
            case "Or" -> PurchasePolicy.policyComposeTypes.PolicyOr;
            case "Conditional" -> PurchasePolicy.policyComposeTypes.PolicyConditioning;
            default -> null;
        };
    }

    private PurchasePolicy.policyTypes getPolicyType(String type) {
        return switch (type){
            case "category" -> PurchasePolicy.policyTypes.Category;
            case "item" ->PurchasePolicy.policyTypes.Item;
            case "dateTime" ->PurchasePolicy.policyTypes.DateTime;
            case "user"->PurchasePolicy.policyTypes.User;
            case "basket" ->PurchasePolicy.policyTypes.Basket;
            default -> null; //TODO miki
        };
    }

    private PurchasePolicy.limiters getLimiter(String limit){
        return switch (limit){
            case "Min" -> PurchasePolicy.limiters.Min;
            case "Max" -> PurchasePolicy.limiters.Max;
            default -> null;
        };
    }
}
