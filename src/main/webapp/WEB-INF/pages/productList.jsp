<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <c:if test="${not empty param.message}">
                        <p class="success">
                            ${param.message}
                        </p>
                    </c:if>
  <c:if test="${not empty errors}">
                                  <p class="error">
                                      There was an error adding to cart
                                  </p>
                              </c:if>
  <form>
    <input name="query" value="">
    <button>Search</button>
  </form>
  <form method="post">
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
        Description
        <tags:sortLink sort="description" order="asc"/>
        <tags:sortLink sort="description" order="desc"/>
        </td>
        <td>
        Quantity
        </td>
        <td class="price">
            Price
            <tags:sortLink sort="price" order="asc"/>
            <tags:sortLink sort="price" order="desc"/>
        </td>
        <td></td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}" varStatus="status">
      <tr>
        <td>
          <img class="product-tile" src="${product.imageUrl}">
        </td>
        <td>
        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
        ${product.description} </a>
        </td>
        <td>
            <input name="quantity_${product.id}"
             value="${not empty errors[product.id] ? 'Input a number' : 1}" data-product-id="${product.id}"/>
                            <c:if test="${not empty errors}">
                                <p class="error">
                                    ${errors[product.id]}
                                </p>
                            </c:if>
        </td>
        <td class="price">
            <a href="${pageContext.servletContext.contextPath}/products/price-histories/${product.id}">
          <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
          </a>
        </td>
        <td>
        <button type="submit"
        formaction="${pageContext.servletContext.contextPath}/products?productId=${product.id}">
        Add to cart</button>
        </td>
      </tr>
    </c:forEach>
  </table>
  </form>
</tags:master>