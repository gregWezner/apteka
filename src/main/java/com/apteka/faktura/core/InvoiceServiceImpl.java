package com.apteka.faktura.core;

import com.apteka.faktura.model.*;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.guice.transactional.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class InvoiceServiceImpl implements InvoiceService {

    @Inject
    private Injector injector;

    @Inject
    @Named("apteka")
    private SqlSessionFactory sqlSessionFactory;

    @Inject
    @Named("przyjecie")
    private SqlSessionFactory sqlSessionFactory1;

    @Override
    public List<Invoice> getActiveInvoices() {
        InvoiceParameter invoiceParameter = new InvoiceParameter(
                injector.getInstance(Key.get(Integer.class, Names.named("numberOfDaysInPast"))),
                injector.getInstance(Key.get(Integer.class, Names.named("idCompany"))),
                injector.getInstance(Key.get(Integer.class, Names.named("idToStartWith"))),
                Splitter.on(",").splitToList(injector.getInstance(Key.get(String.class, Names.named("statuses")))).stream()
                        .map(i -> Integer.valueOf(i))
                        .collect(toList()));
        SqlSession session = sqlSessionFactory.openSession();
        try {
            List<Invoice> invoices = session.getMapper(InvoiceMapper.class).getAllInvoices(invoiceParameter);
            invoices.stream()
                    .forEach(i->{
                        SqlSession session1 = sqlSessionFactory1.openSession();
                        try {
                            ProcessedInvoice processedInvoice = session1.getMapper(ProcessedInvoiceMapper.class).getProcessedInvoice(i.getDokfId());
                            i.withAmountOfProcessedGoods(processedInvoice.getAmount())
                                .withStatus(processedInvoice.getStatus());
                        } finally {
                            session1.close();
                        }
                    });
            return invoices;
        } finally {
            session.close();
        }
    }

    @Override
    public List<InvoicePosition> getInvoicePositions(int dokfId) {


        SqlSession session = sqlSessionFactory.openSession();
        try {
            List<InvoicePosition> invoicePositions = session.getMapper(InvoiceMapper.class).getInvoicePositions(dokfId);
            invoicePositions.stream()
                    .forEach(ip->{
                        SqlSession session1 = sqlSessionFactory1.openSession();
                        try {
                            ip.withIlosc(session1.getMapper(ProcessedInvoiceMapper.class).getScannedProductAmount(ip.getId()));
                        } finally {
                            session1.close();
                        }
                    });
            return invoicePositions;
        } finally {
            session.close();
        }
    }

    @Transactional
    @Override
    public void saveInvoice(InvoicePosition invoicePosition, int amount) {
        SqlSession session1 = sqlSessionFactory1.openSession();
        try {
            session1.getMapper(ProcessedInvoiceMapper.class).insertScannedProductAmount(
                    new ScannedProduct(invoicePosition.getIddokf(), invoicePosition.getId(), amount, invoicePosition.getKodKr()));
            session1.commit();
        } finally {
            session1.close();
        }
    }
}
