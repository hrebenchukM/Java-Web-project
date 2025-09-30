
package learning.itstep.javaweb222.data.jwt;

public class JwtHeader {
    private String alg;
    private String typ;

    public JwtHeader() {
        alg = "HS256";
        typ = "JWT";
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }
           
}
