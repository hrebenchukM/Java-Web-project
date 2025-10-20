package learning.itstep.javaweb222.services.Signature;

public interface SignatureService {
    String getSignatureHex(String data, String secret);
    byte[] getSignatureBytes(String data, String secret);
}