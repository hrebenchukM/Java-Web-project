
package learning.itstep.javaweb222.ioc;

import com.google.inject.AbstractModule;
import learning.itstep.javaweb222.services.Signature.HS256SignatureService;
import learning.itstep.javaweb222.services.Signature.SignatureService;
import learning.itstep.javaweb222.services.config.ConfigService;
import learning.itstep.javaweb222.services.config.JsonConfigService;
import learning.itstep.javaweb222.services.hash.HashService;
import learning.itstep.javaweb222.services.hash.Md5HashService;
import learning.itstep.javaweb222.services.kdf.KdfService;
import learning.itstep.javaweb222.services.kdf.PbKdfService;
import learning.itstep.javaweb222.services.timestamp.TimestampService;
import learning.itstep.javaweb222.services.timestamp.UnixTimestampService;

/**
 *
 * @author Lenovo
 */
public class ServicesConfig extends AbstractModule {
 
    @Override
    protected void configure() {
       bind(KdfService.class).to(PbKdfService.class);
       bind(HashService.class).to(Md5HashService.class);
       bind(TimestampService.class).to(UnixTimestampService.class);
       bind(ConfigService.class).to(JsonConfigService.class)
               .asEagerSingleton();
       bind(SignatureService.class).to(HS256SignatureService.class);
    }
}