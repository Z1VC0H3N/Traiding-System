package database.daos;

import database.DbEntity;
import utils.messageRelated.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageDao {

    private static HashMap<Integer, Complaint> complaintMap = new HashMap<>();
    private static boolean complaints = false;
    private static HashMap<Integer, HashMap<Integer, StoreReview>> storeReviewMap = new HashMap<>();
    private static HashMap<Integer, Boolean> storeReviews = new HashMap<>();
    private static HashMap<Integer, HashMap<Integer, HashMap<Integer, ProductReview>>> productReviewMap = new HashMap<>();
    private static HashMap<Integer, HashMap<Integer, Boolean>> productReviews = new HashMap<>();
    private static HashMap<Integer, HashMap<Integer, Question>> questionMap = new HashMap<>();
    private static HashMap<Integer, Boolean> questions = new HashMap<>();


    public static void saveMessage(Message m){
        Dao.save(m);
    }


    public static Complaint getComplaint(int messageId){
        if(complaintMap.containsKey(messageId))
            return complaintMap.get(messageId);
        Complaint complaint = (Complaint) Dao.getById(Complaint.class, messageId);
        if(complaint != null)
            complaintMap.put(complaint.getMessageId(), complaint);
        return complaint;
    }

    public static List<Complaint> getComplaints(){
        if(!complaints) {
            List<? extends DbEntity> complaintsDto = Dao.getAllInTable("Complaint");
            for (Complaint complaint : (List<Complaint>) complaintsDto)
                complaintMap.put(complaint.getMessageId(), complaint);
            complaints = true;
        }
        return new ArrayList<>(complaintMap.values());
    }

    public static void removeComplaint(int messageId){
        Dao.removeIf("Complaint", String.format("messageId = %d", messageId));
        complaintMap.remove(messageId);
    }

    public static StoreReview getStoreReview(int storeId, int messageId){
        if(storeReviewMap.containsKey(storeId))
            if(storeReviewMap.get(storeId).containsKey(messageId))
                return storeReviewMap.get(storeId).get(messageId);

        StoreReview review = (StoreReview) Dao.getById(StoreReview.class, messageId);
        if(review != null) {
            if(storeReviews.containsKey(storeId))
                storeReviewMap.put(storeId, new HashMap<>());
            storeReviewMap.get(storeId).put(review.getMessageId(), review);
        }
        return review;
    }

    public static List<StoreReview> getStoreReviews(int storeId){
        if(!storeReviews.containsKey(storeId) || !storeReviews.get(storeId)) {
            if (!storeReviewMap.containsKey(storeId))
                storeReviewMap.put(storeId, new HashMap<>());

            List<? extends DbEntity> reviewsDto = Dao.getListById(StoreReview.class, storeId, "StoreReview", "storeId");
            for (StoreReview review : (List<StoreReview>) reviewsDto) {
                storeReviewMap.get(storeId).put(review.getMessageId(), review);
            }
            storeReviews.put(storeId, true);
        }
        return new ArrayList<>(storeReviewMap.get(storeId).values());
    }

    public static void removeStoreReview(int storeId, int messageId){
        Dao.removeIf("StoreReview", String.format("messageId = %d", messageId));
        if(storeReviewMap.containsKey(storeId))
            storeReviewMap.get(storeId).remove(messageId);
    }

    public static ProductReview getProductReview(int storeId, int productId, int messageId){
        if(productReviewMap.containsKey(storeId))
            if(productReviewMap.get(storeId).containsKey(productId))
                if(productReviewMap.get(storeId).get(productId).containsKey(messageId))
                    productReviewMap.get(storeId).get(productId).get(messageId);

        ProductReview review = (ProductReview) Dao.getById(ProductReview.class, messageId);
        if(review != null) {
            if(productReviewMap.containsKey(storeId))
                productReviewMap.put(storeId, new HashMap<>());

            if(productReviewMap.get(storeId).containsKey(productId))
                productReviewMap.get(storeId).put(productId, new HashMap<>());

            productReviewMap.get(storeId).get(productId).put(review.getMessageId(), review);
        }
        return review;
    }

    public static List<ProductReview> getProductReviews(int storeId, int productId){
            if(!productReviews.containsKey(storeId) || !productReviews.get(storeId).containsKey(productId)
                    || !productReviews.get(storeId).get(productId)) {

                if (!productReviewMap.containsKey(storeId))
                    productReviewMap.put(storeId, new HashMap<>());
                if(!productReviewMap.get(storeId).containsKey(productId))
                    productReviewMap.get(storeId).put(productId, new HashMap<>());

                List<? extends DbEntity> reviewDto = Dao.getListByCompositeKey(ProductReview.class, storeId, productId,
                        "ProductReview", "storeId", "productId");
                for (ProductReview review : (List<ProductReview>) reviewDto) {
                    productReviewMap.get(storeId).get(productId).put(review.getMessageId(), review);
                }
            }
        return new ArrayList<>(productReviewMap.get(storeId).get(productId).values());
    }

    public static void removeProductReview(int storeId, int productId, int messageId){
        Dao.removeIf("ProductReview", String.format("messageId = %d", messageId));

        if(!productReviewMap.containsKey(storeId))
            productReviewMap.put(storeId, new HashMap<>());
        if(!productReviewMap.get(storeId).containsKey(productId))
            productReviewMap.get(storeId).put(productId, new HashMap<>());
        productReviewMap.get(storeId).get(productId).remove(messageId);

    }

    public static Question getQuestion(int storeId, int messageId){
        if(questionMap.containsKey(storeId))
            if(questionMap.get(storeId).containsKey(messageId))
                questionMap.get(storeId).get(messageId);

        Question question = (Question) Dao.getById(Question.class, messageId);
        if(question != null) {
            if(!questionMap.containsKey(storeId))
                questionMap.put(storeId, new HashMap<>());
            questionMap.get(storeId).put(question.getMessageId(), question);
        }
        return question;
    }

    public static List<Question> getQuestions(int storeId){
        if(!questions.containsKey(storeId) || !questions.get(storeId)) {
            if(!questionMap.containsKey(storeId))
                questionMap.put(storeId, new HashMap<>());
            List<? extends DbEntity> questionDto = Dao.getListById(Question.class, storeId, "Question", "storeId");
            for (Question question : (List<Question>) questionDto)
                questionMap.get(storeId).put(question.getMessageId(), question);

            questions.put(storeId, true);
        }
        return new ArrayList<>(questionMap.get(storeId).values());
    }

    public static void removeQuestion(int messageId){
        Dao.removeIf("Question", String.format("messageId = %d", messageId));
        questionMap.remove(messageId);
    }
}
