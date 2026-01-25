package learning.itstep.javaweb222.models.profile;

import learning.itstep.javaweb222.data.dto.Recommendation;
import learning.itstep.javaweb222.data.dto.User;

public class RecommendationBlockModel {

    private Recommendation recommendation;
    private User author;

    // ---------- getters / setters ----------

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public RecommendationBlockModel setRecommendation(Recommendation recommendation) {
        this.recommendation = recommendation;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public RecommendationBlockModel setAuthor(User author) {
        this.author = author;
        return this;
    }
}
