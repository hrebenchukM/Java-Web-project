package learning.itstep.javaweb222.services.timestamp;

import java.time.Instant;


public class UnixTimestampService implements TimestampService{

    @Override
    public long getTimestamp() {
         return Instant.now().getEpochSecond();
    }
}
