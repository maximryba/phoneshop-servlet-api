<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="lastProducts" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Last Viewed Products">
  <p>
    Your three last viewed products
  </p>
  <c:if test="${not empty lastProducts}">
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
    <c:forEach var="product" items="${lastProducts}">
      <tr>
        <td>
          <img class="product-tile" src="${product.imageUrl}">
        </td>
        <td>
        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
        ${product.description} </a>
        </td>
        <td class="price">
            <a href="${pageContext.servletContext.contextPath}/products/price-histories/${product.id}">
          <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
          </a>
        </td>
      </tr>
    </c:forEach>
  </table>
  </c:if>
<c:if test="${empty lastProducts}">
    <p>Sorry, you dont have last viewed products</p>
    <a href="${pageContext.servletContext.contextPath}/products">Return to list page</a>
</c:if>
</tags:master>