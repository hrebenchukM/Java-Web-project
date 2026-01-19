package learning.itstep.javaweb222.data.jwt;

import com.google.gson.Gson;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import learning.itstep.javaweb222.data.dto.AccessToken;

public class JwtToken {
    private final static Gson gson = new Gson();
    
    private JwtHeader  header;
    private JwtPayload payload;
    private String     signature;
    
    public static JwtToken fromParts(String[] parts) {
        JwtToken jwt = new JwtToken();
        jwt.setHeader( 
                gson.fromJson( 
                    new String( Base64.getDecoder().decode(parts[0]) ),
                    JwtHeader.class
                )
        );
        jwt.setPayload(
                gson.fromJson( 
                    new String( Base64.getDecoder().decode(parts[1]) ),
                    JwtPayload.class
                )
        );
        jwt.setSignature(parts[2]);
        return jwt;
    }
    public static JwtToken fromAccessToken(AccessToken at) {
        JwtToken jwt = new JwtToken();
        jwt.setHeader(new JwtHeader());

        JwtPayload payload = new JwtPayload();

        // --- JWT core ---
        payload.setJti(at.getTokenId().toString());   // access_tokens.token_id
        payload.setSub(at.getUserId().toString());    // access_tokens.user_id
        payload.setAud(at.getRoleId());               // access_tokens.role_id
        payload.setIss("JavaWeb222");

        if (at.getIssuedAt() != null) {
            payload.setIat(at.getIssuedAt().toString());
        }
        if (at.getExpiredAt() != null) {
            payload.setExp(at.getExpiredAt().toString());
        }

        // --- User info (JOIN users) ---
        if (at.getUser() != null) {
            String first = at.getUser().getFirstName();
            String second = at.getUser().getSecondName();

            payload.setName(
                (first != null ? first : "") +
                (second != null ? " " + second : "")
            );
            payload.setEmail(at.getUser().getEmail());
        }

        jwt.setPayload(payload);
        return jwt;
    }

    public String getBody() {
        Encoder encoder = Base64.getEncoder();
        return  encoder.encodeToString( gson.toJson(header).getBytes() ) +
                "." +
                encoder.encodeToString( gson.toJson(payload).getBytes() );
    }

    @Override
    public String toString() {
        return getBody() + "." + signature;
    }
    
    
    public JwtHeader getHeader() {
        return header;
    }

    public void setHeader(JwtHeader header) {
        this.header = header;
    }

    public JwtPayload getPayload() {
        return payload;
    }

    public void setPayload(JwtPayload payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    
}