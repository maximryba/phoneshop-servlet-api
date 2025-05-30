<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">
  <p>
    Checkout page
  </p>
  <c:if test="${not empty errors}">
    <div class="error">
        Error while placing order
    </div>
  </c:if>
  <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
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
      </tr>
    </thead>
    <c:forEach var="item" items="${order.items}" varStatus="status">
      <tr>
        <td>
          <img class="product-tile" src="${item.product.imageUrl}">
        </td>
        <td>
        <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
        ${item.product.description} </a>
        </td>
        <td class="quantity">
            <fmt:formatNumber value="${item.quantity}"/>
        </td>
        <td class="price">
            <a href="${pageContext.servletContext.contextPath}/products/price-histories/${item.product.id}">
          <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="${item.product.currency.symbol}"/>
          </a>
        </td>
      </tr>
    </c:forEach>
    <tr>
        <td>Delivery cost</td>
        <td>${order.deliveryCost}</td>
        <td>Subtotal<td>
        <td>${order.subtotal}</td>
    </tr>
    <tr>
            <td>Total cost</td>
            <td>${order.totalCost}</td>
        </tr>
  </table>
    <h2>Your details</h2>
  <table>
  <tags:orderFormRow name="firstName" label="First name" order="${order}" errors="${errors}"></tags:orderFormRow>
  <tags:orderFormRow name="lastName" label="Last name" order="${order}" errors="${errors}"></tags:orderFormRow>
  <tags:orderFormRow name="phone" label="Phone" order="${order}" errors="${errors}"></tags:orderFormRow>
  <tags:orderFormRow name="deliveryAddress" label="Delivery address" order="${order}" errors="${errors}"></tags:orderFormRow>
  <tr>
          <td>Delivery date<span style="color: red;">*</span></td>
          <td>
          <c:set var="error" value="${errors['deliveryDate']}"/>
          <input type="date" name="deliveryDate" value="${not empty error ? param['deliveryDate'] : order['deliveryDate']}"/>
          <c:if test="${not empty error}">
              <div class="error">${error}</div>
          </c:if>
          </td>
      </tr>
    <tr>
        <td>Payment method<span style="color:red;">*</span>
            <select name="paymentMethod">
                    <c:set var="error" value="${errors['paymentMethod']}"/>
                    <c:if test="${not empty error}">
                        ${param['paymentMethod']}
                    </c:if>
                      <c:if test="${empty error}">
                        <option></option>
                    </c:if>
                  <c:forEach var="paymentMethod" items="${paymentMethods}">
                <option>
                    ${paymentMethod}
                </option>
                </c:forEach>
            </select>
            <c:set var="error" value="${errors['paymentMethod']}"/>
                    <c:if test="${not empty error}">
                        <div class="error">${error}</div>
                    </c:if>
        </td>
    </tr>
  </table>

  <p>
  <button>Place order</button>
  </p>
  </form>
</tags:master>