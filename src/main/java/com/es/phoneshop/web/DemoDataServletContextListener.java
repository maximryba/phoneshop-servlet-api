package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class DemoDataServletContextListener implements ServletContextListener {

    private ProductDao productDao;

    private final static String INSERT_DATA_FLAG = "insertDemoData";

    public DemoDataServletContextListener() {
        this.productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean insertDemoData = Boolean.parseBoolean(sce.getServletContext().getInitParameter(INSERT_DATA_FLAG));
        if (insertDemoData) {
            saveSampleProducts();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private List<List<PriceHistory>> getSamplePriceHistories() {
        List<List<PriceHistory>> result = new ArrayList<List<PriceHistory>>();
        Currency usd = Currency.getInstance("USD");
        LocalDate sampleDate1 = LocalDate.of(2024, 6, 5);
        LocalDate sampleDate2 = LocalDate.of(2024, 9, 14);
        LocalDate sampleDate3 = LocalDate.of(2024, 12, 30);
        LocalDate sampleDate4 = LocalDate.of(2025, 1, 10);
        LocalDate sampleDate5 = LocalDate.of(2025, 2, 8);
        LocalDate sampleDate6 = LocalDate.of(2025, 3, 7);

        List<PriceHistory> priceHistories1, priceHistories2, priceHistories3, priceHistories4, priceHistories5,
                priceHistories6, priceHistories7, priceHistories8, priceHistories9, priceHistories10, priceHistories11,
                priceHistories12, priceHistories13;
        priceHistories1 = List.of(new PriceHistory(1L, BigDecimal.valueOf(200), sampleDate1, usd),
                new PriceHistory(2L, BigDecimal.valueOf(250), sampleDate2, usd),
                new PriceHistory(3L, BigDecimal.valueOf(100), sampleDate3, usd));
        priceHistories2 = List.of(new PriceHistory(4L, BigDecimal.valueOf(200), sampleDate2, usd),
                new PriceHistory(5L, BigDecimal.valueOf(100), sampleDate3, usd),
                new PriceHistory(6L, BigDecimal.valueOf(150), sampleDate4, usd),
                new PriceHistory(7L, BigDecimal.valueOf(200), sampleDate5, usd));
        priceHistories3 = List.of(new PriceHistory(8L, BigDecimal.valueOf(100), sampleDate1, usd),
                new PriceHistory(9L, BigDecimal.valueOf(200), sampleDate2, usd),
                new PriceHistory(10L, BigDecimal.valueOf(300), sampleDate3, usd));
        priceHistories4 = List.of(new PriceHistory(11L, BigDecimal.valueOf(50), sampleDate4, usd),
                new PriceHistory(12L, BigDecimal.valueOf(150), sampleDate5, usd),
                new PriceHistory(13L, BigDecimal.valueOf(200), sampleDate6, usd));
        priceHistories5 = List.of(new PriceHistory(14L, BigDecimal.valueOf(1200), sampleDate3, usd),
                new PriceHistory(15L, BigDecimal.valueOf(1100), sampleDate3, usd),
                new PriceHistory(16L, BigDecimal.valueOf(1000), sampleDate5, usd));
        priceHistories6 = List.of(new PriceHistory(17L, BigDecimal.valueOf(420), sampleDate1, usd),
                new PriceHistory(18L, BigDecimal.valueOf(400), sampleDate2, usd),
                new PriceHistory(19L, BigDecimal.valueOf(310), sampleDate3, usd),
                new PriceHistory(20L, BigDecimal.valueOf(320), sampleDate4, usd));
        priceHistories7 = List.of(new PriceHistory(21L, BigDecimal.valueOf(200), sampleDate1, usd),
                new PriceHistory(22L, BigDecimal.valueOf(300), sampleDate2, usd),
                new PriceHistory(23L, BigDecimal.valueOf(420), sampleDate3, usd));
        priceHistories8 = List.of(new PriceHistory(24L, BigDecimal.valueOf(100), sampleDate2, usd),
                new PriceHistory(25L, BigDecimal.valueOf(110), sampleDate3, usd),
                new PriceHistory(26L, BigDecimal.valueOf(120), sampleDate4, usd));
        priceHistories9 = List.of(new PriceHistory(27L, BigDecimal.valueOf(100), sampleDate4, usd),
                new PriceHistory(28L, BigDecimal.valueOf(80), sampleDate5, usd),
                new PriceHistory(29L, BigDecimal.valueOf(70), sampleDate6, usd));
        priceHistories10 = List.of(new PriceHistory(30L, BigDecimal.valueOf(200), sampleDate2, usd),
                new PriceHistory(31L, BigDecimal.valueOf(180), sampleDate3, usd),
                new PriceHistory(32L, BigDecimal.valueOf(170), sampleDate4, usd));
        priceHistories11 = List.of(new PriceHistory(33L, BigDecimal.valueOf(110), sampleDate4, usd),
                new PriceHistory(34L, BigDecimal.valueOf(900), sampleDate5, usd),
                new PriceHistory(35L, BigDecimal.valueOf(70), sampleDate6, usd));
        priceHistories12 = List.of(new PriceHistory(36L, BigDecimal.valueOf(200), sampleDate1, usd),
                new PriceHistory(37L, BigDecimal.valueOf(150), sampleDate2, usd),
                new PriceHistory(38L, BigDecimal.valueOf(100), sampleDate3, usd),
                new PriceHistory(39L, BigDecimal.valueOf(80), sampleDate4, usd));
        priceHistories13 =  List.of(new PriceHistory(40L, BigDecimal.valueOf(200), sampleDate3, usd),
                new PriceHistory(41L, BigDecimal.valueOf(180), sampleDate4, usd),
                new PriceHistory(42L, BigDecimal.valueOf(150), sampleDate5, usd));
        result.add(priceHistories1);
        result.add(priceHistories2);
        result.add(priceHistories3);
        result.add(priceHistories4);
        result.add(priceHistories5);
        result.add(priceHistories6);
        result.add(priceHistories7);
        result.add(priceHistories8);
        result.add(priceHistories9);
        result.add(priceHistories10);
        result.add(priceHistories11);
        result.add(priceHistories12);
        result.add(priceHistories13);
        return result;
    }

    private void saveSampleProducts() {
        Currency usd = Currency.getInstance("USD");
        List<List<PriceHistory>> priceHistories = getSamplePriceHistories();
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", priceHistories.get(0)));
        productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", priceHistories.get(1)));
        productDao.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", priceHistories.get(2)));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg", priceHistories.get(3)));
        productDao.save(new Product( "iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg", priceHistories.get(4)));
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg", priceHistories.get(5)));
        productDao.save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg", priceHistories.get(6)));
        productDao.save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg", priceHistories.get(7)));
        productDao.save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg", priceHistories.get(8)));
        productDao.save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", priceHistories.get(9)));
        productDao.save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg", priceHistories.get(10)));
        productDao.save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg", priceHistories.get(11)));
        productDao.save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", priceHistories.get(12)));
    }
}
