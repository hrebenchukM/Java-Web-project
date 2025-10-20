package learning.itstep.javaweb222.ioc;

import com.google.inject.AbstractModule;
import learning.itstep.javaweb222.services.Signature.HS256SignatureService;
import learning.itstep.javaweb222.services.Signature.SignatureService;
import learning.itstep.javaweb222.services.config.ConfigService;
import learning.itstep.javaweb222.services.config.JsonConfigService;
import learning.itstep.javaweb222.services.form.FormParseService;
import learning.itstep.javaweb222.services.form.MixedFormParseService;
import learning.itstep.javaweb222.services.hash.HashService;
import learning.itstep.javaweb222.services.hash.Md5HashService;
import learning.itstep.javaweb222.services.kdf.KdfService;
import learning.itstep.javaweb222.services.kdf.PbKdf1Service;
import learning.itstep.javaweb222.services.storage.DiskStorageService;
import learning.itstep.javaweb222.services.storage.StorageService;

public class ServicesConfig extends AbstractModule {

    @Override
    protected void configure() {
        bind(KdfService.class).to(PbKdf1Service.class);
        bind(HashService.class).to(Md5HashService.class);
        bind(ConfigService.class).to(JsonConfigService.class)
                .asEagerSingleton();
        bind(SignatureService.class).to(HS256SignatureService.class);
        bind(FormParseService.class).to(MixedFormParseService.class);
        bind(StorageService.class).to(DiskStorageService.class);
    }
    
}