package com.apteka.faktura;

import com.apteka.faktura.core.InvoiceService;
import com.apteka.faktura.core.InvoiceServiceImpl;
import com.apteka.faktura.model.Invoice;
import com.apteka.faktura.model.InvoiceMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.google.inject.name.Names.bindProperties;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@Ignore
public class InvoiceTest {

    private Injector injector;

    private InvoiceService invoiceService;

    @Before
    public void setupMyBatisGuice() throws Exception {

        // bindings
        List<Module> modules = this.createMyBatisModule();
        this.injector = Guice.createInjector(modules);

        this.invoiceService = this.injector.getInstance(InvoiceService.class);
    }

    protected List<Module> createMyBatisModule() {
        List<Module> modules = new ArrayList<>();
        modules.add(JdbcHelper.Firebird);
        modules.add(new MyBatisModule() {

            @Override
            protected void initialize() {
                bindDataSourceProviderType(PooledDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                addMapperClass(InvoiceMapper.class);
            }

        });
        modules.add(binder -> {
            bindProperties(binder, createTestProperties());
            binder.bind(InvoiceService.class)
                    .to(InvoiceServiceImpl.class);
        });

        return modules;
    }

    protected static Properties createTestProperties() {
        final Properties myBatisProperties = new Properties();
        myBatisProperties.setProperty("mybatis.environment.id", "test");
        myBatisProperties.setProperty("JDBC.schema", "c:/KSBAZA/KS-APW/WAPTEKA.FDB");
        myBatisProperties.setProperty("JDBC.username", "apw_user");
        myBatisProperties.setProperty("JDBC.password", "apw_user");
        myBatisProperties.setProperty("JDBC.autoCommit", "false");
        myBatisProperties.setProperty("numberOfDaysInPast", "30");
        myBatisProperties.setProperty("idCompany", "1");
        myBatisProperties.setProperty("idToStartWith", "1");
        myBatisProperties.setProperty("statuses", "1,5");
        return myBatisProperties;
    }

    @Test
    public void testFooService(){
        List<Invoice> invoices = this.invoiceService.getActiveInvoices();
        assertNotNull(invoices);
        assertThat(invoices.size(), CoreMatchers.equalTo(1));
    }
}
