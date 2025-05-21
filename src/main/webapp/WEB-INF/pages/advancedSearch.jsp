<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Advanced search">
  <h1>
    Advanced search
  </h1>
  <form>
    <label for="description">Description</label>
    <input name="description" value="">
   <select name="search-criteria">
        <option value="all">all words</option>
        <option value="any">any word</option>
   </select>
    <br>
    <label for="min-price">Min price</label>
    <input name="min-price" value=""> <br>
    <c:if test="${not empty errors}">
                                    <p class="error">
                                        ${errors["min-price"]}
                                    </p>
                                </c:if>
    <label for="max-price">Max price</label>
    <input name="max-price" value=""> <br>
    <c:if test="${not empty errors}">
                                    <p class="error">
                                        ${errors["max-price"]}
                                    </p>
                                </c:if>
    <c:if test="${not empty errors['price-range']}">
                                        <p class="error">
                                            ${errors["price-range"]}
                                        </p>
                                    </c:if>
    <button>Search</button>
  </form>
    <c:if test="${empty errors}">
   <table>
      <thead>
        <tr>
          <td>Image</td>
          <td>
          Description
          </td>
          <td class="price">
              Price
          </td>
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
          <td class="price">
            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
          </td>
        </tr>
      </c:forEach>
    </table>
</c:if>
</tags:master>