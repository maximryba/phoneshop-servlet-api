package com.es.phoneshop.model.product;

import com.es.phoneshop.model.general.EntityNotFoundException;
import org.junit.After;
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
        productDao.save(new Product( "test-phone", "Test 1", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        productDao.save(new Product( "test-phone", "Test 2", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
    }

    @After
    public void tearDown() {
        List<Product> products = productDao.findProducts("", null, SortOrder.ASC);
        for (Product product : products) {
            productDao.delete(product.getId());
        }
    }

    @Test
    public void testFindProductsWithoutQueryWithoutSortingHaveResults() {
        List<Product> products = productDao.findProducts("", null, SortOrder.ASC);
        assertEquals("Test 1", products.get(0).getDescription());
        assertEquals(2, products.size());
    }

    @Test
    public void testFindProductsWithQueryWithoutSorting() {
        List<Product> products = productDao.findProducts("t 1", null, SortOrder.ASC);
        assertEquals("Test 1", products.get(0).getDescription());
    }

    @Test
    public void testFindProductsWithQueryAndSorting() {
        List<Product> products = productDao.findProducts("t", SortField.DESCRIPTION, SortOrder.DESC);
        assertEquals("Test 2", products.get(0).getDescription());
    }

    @Test
    public void testSaveNewProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product-", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new ArrayList<>());
        List<Product> products = productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC);
        assertFalse(products.contains(product));
        productDao.save(product);
        List<Product> productsAfterSave = productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC);
        assertTrue(productsAfterSave.contains(product));
    }

    @Test
    public void testAdvancedSearch() {
        List<Product> products = productDao.advancedSearchProducts("test", 0, null, "all");
        assertTrue(products.get(0).getDescription().contains("Test"));
    }

    @Test
    public void testGetProductById() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new ArrayList<>());
        productDao.save(product);
        Product foundProduct = productDao.get(product.getId());
        assertEquals(foundProduct, product);
        assertEquals("test-product", foundProduct.getCode());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetProductByIdFailed() {
        productDao.get(100L);
    }

    @Test
    public void testDeleteProduct() {
        List<Product> products = productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC);
        Product deletedProduct = productDao.get(products.get(0).getId());
        productDao.delete(deletedProduct.getId());
        List<Product> productsAfterDelete = productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC);
        assertFalse(productsAfterDelete.contains(deletedProduct));
    }

    @Test
    public void testUpdateProduct() {
        List<Product> products = productDao.findProducts("", null, SortOrder.ASC);
        Product productForUpdate = productDao.get(products.get(0).getId());
        productForUpdate.setPrice(BigDecimal.valueOf(200));
        productForUpdate.setDescription("Updated product");
        productDao.save(productForUpdate);
        assertNotNull(productForUpdate.getId());
        assertEquals("Updated product", productDao.get(productForUpdate.getId()).getDescription());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testProductUpdateFailed() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(50L,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", new ArrayList<>());
        productDao.save(product);
    }
}
