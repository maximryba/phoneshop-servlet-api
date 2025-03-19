package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        List<PriceHistory> priceHistories = new ArrayList<>();
        priceHistories.add(new PriceHistory
                (1L, BigDecimal.valueOf(50), LocalDate.of(2024, 12, 5), usd));
        priceHistories.add(new PriceHistory
                (2L, BigDecimal.valueOf(100), LocalDate.of(2025, 1, 10), usd));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
    }

    @Test
    public void testFindProductsHaveResults() {
        assertFalse(productDao.findProducts("", SortField.description, SortOrder.asc).isEmpty());
    }

    @Test
    public void testSaveNewProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product-", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new ArrayList<>());
        List<Product> products = productDao.findProducts("", SortField.description, SortOrder.asc);
        assertFalse(products.contains(product));
        productDao.save(product);
        List<Product> productsAfterSave = productDao.findProducts("", SortField.description, SortOrder.asc);
        assertTrue(productsAfterSave.contains(product));
    }

    @Test
    public void testGetProductById() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new ArrayList<>());
        productDao.save(product);
        Product foundProduct = productDao.getProduct(product.getId());
        assertEquals(foundProduct, product);
        assertEquals("test-product", foundProduct.getCode());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetProductByIdFailed() {
        productDao.getProduct(100L);
    }

    @Test
    public void testDeleteProduct() {
        List<Product> products = productDao.findProducts("", SortField.description, SortOrder.asc);
        Product deletedProduct = productDao.getProduct(products.get(0).getId());
        productDao.delete(deletedProduct.getId());
        List<Product> productsAfterDelete = productDao.findProducts("", SortField.description, SortOrder.asc);
        assertFalse(productsAfterDelete.contains(deletedProduct));
    }

    @Test
    public void testUpdateProduct() {
        Product productForUpdate = productDao.getProduct(1L);
        productForUpdate.setPrice(BigDecimal.valueOf(200));
        productForUpdate.setDescription("Updated product");
        productDao.save(productForUpdate);
        assertNotNull(productForUpdate.getId());
        assertEquals("Updated product", productDao.getProduct(productForUpdate.getId()).getDescription());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testProductUpdateFailed() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(50L,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new ArrayList<>());
        productDao.save(product);
    }
}
