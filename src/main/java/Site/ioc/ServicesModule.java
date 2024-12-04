package Site.ioc;

import Site.services.hash.HashService;
import Site.services.hash.Md5HashService;
import Site.services.hash.Sha1HashService;
import Site.services.hash.Sha256HashService;
import Site.services.random.RandomService;
import Site.services.random.RandomServiceV1;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import Site.services.db.DbProvider;
import Site.services.db.PlanetDbProvider;
import Site.services.formparse.FormParseService;
import Site.services.formparse.MixedFormParseService;
import step.learning.services.hash.*;
import Site.services.kdf.DigestHashKdfService;
import Site.services.kdf.KdfService;
import step.learning.services.random.*;

import java.util.Date;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HashService.class).annotatedWith(Names.named("Digest-Hash2")).to(Sha256HashService.class);
        bind(HashService.class).annotatedWith(Names.named("DSA-Hash")).to(Sha1HashService.class);
        bind(HashService.class).annotatedWith(Names.named("Digest-Hash")).to(Md5HashService.class);//

        bind(FormParseService.class).to(MixedFormParseService.class);
        bind(DbProvider.class).to(PlanetDbProvider.class);
        bind(KdfService.class).to(DigestHashKdfService.class);

        bind(String.class)
                .annotatedWith(Names.named("db-prefix"))
                .toInstance("java201_");
    }

    private RandomService randomService;

    @Provides
    private RandomService randomService() {
        /*
        Провайдери - методи, які дозволяють більш гнучко керувати процесом
        інжекції залежностей. Зв'язується за типом даних - включається для
        кожної точки інжекції з типом RandomService. У якості інжектованого
        об'єкту буде те, що поверне даний метод. Назва методу - не має значення
        тільки тип повернення
         */
        if (randomService == null) {
            randomService = new RandomServiceV1();
            randomService.seed(String.valueOf(new Date().getTime())); // як приклад того що самого конструктора недостатньо
        }
        return randomService;
    }
}
