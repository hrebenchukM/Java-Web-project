package learning.itstep.javaweb222.websocket;

import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.HandshakeResponse;

import java.util.Base64;
import java.util.List;

import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.services.Signature.SignatureService;
import learning.itstep.javaweb222.services.Signature.HS256SignatureService;

public class WsAuthConfigurator
        extends ServerEndpointConfig.Configurator {

    private static final SignatureService signatureService =
            new HS256SignatureService();

    @Override
    public void modifyHandshake(
            ServerEndpointConfig sec,
            HandshakeRequest req,
            HandshakeResponse res) {

        List<String> auth = req.getHeaders().get("Authorization");

        if (auth == null || auth.isEmpty()) {
            throw new RuntimeException("Missing Authorization header");
        }

        String header = auth.get(0);
        if (!header.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid auth scheme");
        }

        String jwt = header.substring(7);
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT format");
        }

        // 🔐 Проверка подписи (ТОЧНО как в AuthFilter)
        String jwtBody = parts[0] + "." + parts[1];
        String signature = Base64.getEncoder().encodeToString(
            signatureService.getSignatureBytes(jwtBody, "secret")
        );

        if (!parts[2].equals(signature)) {
            throw new RuntimeException("Invalid JWT signature");
        }

        // 🔓 JWT валиден
        JwtToken token = JwtToken.fromParts(parts);

        // 🔑 userId = payload.sub
        String userId = token.getPayload().getSub();

        sec.getUserProperties().put("userId", userId);
    }
}
