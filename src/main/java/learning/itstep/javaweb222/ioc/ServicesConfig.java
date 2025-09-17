
package learning.itstep.javaweb222.ioc;

import com.google.inject.AbstractModule;
import learning.itstep.javaweb222.services.hash.HashService;
import learning.itstep.javaweb222.services.hash.Md5HashService;
import learning.itstep.javaweb222.services.kdf.KdfService;
import learning.itstep.javaweb222.services.kdf.PbKdfService;

/**
 *
 * @author Lenovo
 */
public class ServicesConfig extends AbstractModule {
 
    @Override
    protected void configure() {
       bind(KdfService.class).to(PbKdfService.class);
       bind(HashService.class).to(Md5HashService.class);
    }
}