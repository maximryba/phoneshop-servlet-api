<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
  <p>
    Cart page
  </p>
             <c:if test="${not empty errors}">
                      <p class="error">
                          There were errors updating a cart
                      </p>
              </c:if>
              <c:if test="${empty errors}">
                                    <p class="success">
                                        ${param.message}
                                    </p>
                            </c:if>
  <form method="post" action="${pageContext.servletContext.contextPath}/cart">
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
        Description
        </td>
        <td class="quantity">
            Quantity
        </td>
        <td class="price">
            Price
        </td>
        <td></td>
      </tr>
    </thead>
    <c:forEach var="item" items="${cart.items}" varStatus="status">
      <tr>
        <td>
          <img class="product-tile" src="${item.product.imageUrl}">
        </td>
        <td>
        <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
        ${item.product.description} </a>
        </td>
        <td class="quantity">
            <fmt:formatNumber value="${item.quantity}" var="quantity"/>
            <input name="quantity" value="${not empty errors[item.product.id] ? paramValues['quantity'][status.index] : quantity}" class="quantity"/>
            <c:if test="${not empty errors[item.product.id]}">
                    <p class="error">
                        ${errors[item.product.id]}
                    </p>
            </c:if>
            <input name="productId" type="hidden" value="${item.product.id}"/>
        </td>
        <td class="price">
            <a href="${pageContext.servletContext.contextPath}/products/price-histories/${item.product.id}">
          <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="${item.product.currency.symbol}"/>
          </a>
        </td>
        <td>
            <button
            formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}"
             form="deleteCartItem">
            Delete
            </button>
        </td>
      </tr>
    </c:forEach>
    <tr>
        <td>Total cost</td>
        <td>${cart.totalCost}</td>
        <td>Total quantity<td>
        <td>${cart.totalQuantity}</td>
    </tr>
  </table>
  <p>
  <button>Update</button>
  </p>
  </form>
  <form id="deleteCartItem" method="post">
  </form>
</tags:master>