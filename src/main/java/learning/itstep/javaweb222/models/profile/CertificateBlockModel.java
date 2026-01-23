package learning.itstep.javaweb222.models.profile;

import learning.itstep.javaweb222.data.dto.Academy;
import learning.itstep.javaweb222.data.dto.Certificate;

public class CertificateBlockModel {

    private Certificate certificate;
    private Academy academy;

    public Certificate getCertificate() {
        return certificate;
    }

    public CertificateBlockModel setCertificate(Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    public Academy getAcademy() {
        return academy;
    }

    public CertificateBlockModel setAcademy(Academy academy) {
        this.academy = academy;
        return this;
    }
}
