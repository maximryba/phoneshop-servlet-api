package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFindProductsHaveResults() {
        assertFalse(productDao.findProducts("", SortField.description, SortOrder.asc).isEmpty());
    }

    @Test
    public void testSaveNewProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertTrue(product.getId() > 0);
    }

    @Test
    public void testGetProductById() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertEquals(productDao.getProduct(product.getId()), product);
        assertEquals("test-product", productDao.getProduct(product.getId()).getCode());
    }

    @Test
    public void testGetProductByIdFailed() {
        thrown.expect(ProductNotFoundException.class);
        thrown.expectMessage(not(equalTo("")));
        Product product = productDao.getProduct(100L);
        thrown = ExpectedException.none();
    }

    @Test
    public void testDeleteProduct() {
        List<Product> products = productDao.findProducts("", SortField.description, SortOrder.asc);

        productDao.delete(products.get(0).getId());
        assertEquals(12, products.size());
    }

    @Test
    public void testUpdateProduct() {
        Product productForUpdate = productDao.getProduct(1L);
        productForUpdate.setPrice(BigDecimal.valueOf(200));
        productForUpdate.setDescription("Updated product");
        productDao.save(productForUpdate);
        assertNotNull(productForUpdate.getId());
        assertEquals(productForUpdate, productDao.getProduct(productForUpdate.getId()));
    }

    @Test
    public void testProductUpdateFailed() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(50L,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        thrown.expect(ProductNotFoundException.class);
        thrown.expectMessage(equalTo("Product for update not found"));
        productDao.save(product);
        thrown = ExpectedException.none();
    }
}
