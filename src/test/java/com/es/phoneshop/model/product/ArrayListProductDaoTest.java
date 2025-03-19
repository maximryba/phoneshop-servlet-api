package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
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
        productDao = new ArrayListProductDao();
    }

    @Test
    public void testFindProductsHaveResults() {
        assertFalse(productDao.findProducts().isEmpty());
    }

    @Test
    public void testSaveNewProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product-", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        List<Product> products = productDao.findProducts();
        assertTrue(products.stream()
                .filter(product1 -> product1.getCode()
                        .equals("test-product-"))
                .findFirst()
                .isEmpty());
        productDao.save(product);
        List<Product> productsAfterSave = productDao.findProducts();
        assertFalse(productsAfterSave.stream()
                .filter(product1 -> product1.getCode().equals("test-product-"))
                .findFirst()
                .isEmpty());

    }

    @Test
    public void testGetProductById() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
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
        List<Product> products = productDao.findProducts();
        Product deletedProduct = products.get(0);
        assertTrue(products.contains(deletedProduct));
        productDao.delete(deletedProduct.getId());
        List<Product> productsAfterDelete = productDao.findProducts();
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
        Product product = new Product(50L,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
    }
}
