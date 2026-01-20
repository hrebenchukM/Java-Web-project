package learning.itstep.javaweb222.models.profile;

import java.util.List;
import learning.itstep.javaweb222.data.dto.Experience;
import learning.itstep.javaweb222.data.dto.Company;

public class ExperienceBlockModel {

    private Experience experience;
    private Company company;

    public Experience getExperience() {
        return experience;
    }

    public ExperienceBlockModel setExperience(Experience experience) {
        this.experience = experience;
        return this;
    }

    public Company getCompany() {
        return company;
    }

    public ExperienceBlockModel setCompany(Company company) {
        this.company = company;
        return this;
    }
}
