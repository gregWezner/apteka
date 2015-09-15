package com.apteka.faktura;

import com.apteka.faktura.core.InvoiceService;
import com.apteka.faktura.core.InvoiceServiceImpl;
import com.apteka.faktura.model.InvoiceMapper;
import com.apteka.faktura.model.ProcessedInvoiceMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.name.Names;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.dbcp.BasicDataSourceProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.google.inject.name.Names.bindProperties;

/**
 * Created by grzegorz.weznerowicz on 2015-08-14.
 */
public class InjectorInitializer {

    static {
        initialize();
    }

    private static List<Module> modules;

    public static void initialize(){
        modules = new ArrayList<>();
        modules.add(new FxmlExampleModule());
//        modules.add(JdbcHelper.Firebird);
        modules.add(
                new PrivateModule() {
                    @Override
                    protected void configure() {
                        install(new MyBatisModule() {

                            @Override
                            protected void initialize() {
                                bindDataSourceProviderType(BasicDataSourceProvider.class);
                                bindTransactionFactoryType(JdbcTransactionFactory.class);
                                addMapperClass(InvoiceMapper.class);
                            }

                        });
                        try {
                            bindProperties(binder(), loadProperties("com/apteka/faktura/dbApteka.properties"));
                        } catch (IOException e) {
                            System.exit(1);
                        }
                        expose(InvoiceMapper.class);

                        bind(SqlSessionFactory.class).annotatedWith(Names.named("apteka")).to(SqlSessionFactory.class);
                        expose(SqlSessionFactory.class).annotatedWith(Names.named("apteka"));
                    }
                }
        );
        modules.add(
                new PrivateModule() {
                    @Override
                    protected void configure() {
                        install(new MyBatisModule() {

                            @Override
                            protected void initialize() {
                                bindDataSourceProviderType(BasicDataSourceProvider.class);
                                bindTransactionFactoryType(JdbcTransactionFactory.class);
                                addMapperClass(ProcessedInvoiceMapper.class);
                            }

                        });
                        try {
                            bindProperties(binder(), loadProperties("com/apteka/faktura/dbPrzyjecie.properties"));
                        } catch (IOException e) {
                            System.exit(1);
                        }
                        expose(ProcessedInvoiceMapper.class);

                        bind(SqlSessionFactory.class).annotatedWith(Names.named("przyjecie")).to(SqlSessionFactory.class);
                        expose(SqlSessionFactory.class).annotatedWith(Names.named("przyjecie"));
                    }
                }
        );
        modules.add(binder -> {
            try {
                bindProperties(binder, loadProperties("com/apteka/faktura/global.properties"));
            } catch (IOException e) {
                System.exit(1);
            }

            binder.bind(InvoiceService.class).to(InvoiceServiceImpl.class);
        });
    }

    private static Properties loadProperties(String name) throws IOException {
        Properties properties = new Properties();
        ClassLoader loader = MainApp.class.getClassLoader();
        URL url = loader.getResource(name);
        properties.load(url.openStream());
        return properties;
    }

    public static List<Module> getModules() {
        return modules;
    }

    private static class FxmlExampleModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ResourceBundle.class).annotatedWith(Names.named("i18n-resources"))
                    .toInstance(ResourceBundle.getBundle(MainApp.class.getName()));

        }
    }
}
