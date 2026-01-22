package learning.itstep.javaweb222.models.profile;

import learning.itstep.javaweb222.data.dto.Academy;
import learning.itstep.javaweb222.data.dto.Education;

public class EducationBlockModel {

    private Education education;
    private Academy academy;

    public Education getEducation() {
        return education;
    }

    public EducationBlockModel setEducation(Education education) {
        this.education = education;
        return this;
    }

    public Academy getAcademy() {
        return academy;
    }

    public EducationBlockModel setAcademy(Academy academy) {
        this.academy = academy;
        return this;
    }
}
