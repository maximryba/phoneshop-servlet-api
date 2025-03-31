<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Details">
  <p>
    ${product.description}
  </p>
  <p>
    Cart: ${cart}
  </p>
  <c:if test="${not empty param.message}">
                      <p class="success">
                          ${param.message}
                      </p>
                  </c:if>
  <c:if test="${not empty error}">
                      <p class="error">
                          ${error}
                      </p>
                  </c:if>
   <form method="post">
       <table>
            <tr>
                <td>
                    Image
                </td>
                <td>
                    <img src="${product.imageUrl}">
                </td>
            </tr>
             <tr>
                <td>
                    Code
                </td>
                <td>
                    <p>${product.code}</p>
                </td>
             </tr>
             <tr>
                  <td>
                    Price
                  </td>
                  <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                  </td>
             </tr>
             <tr>
                <td>
                    Stock
                </td>
                <td>
                    <p>${product.stock}</p>
                </td>
             </tr>
             <tr>
              <td>
                    Description
              </td>
              <td class="description">
                <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                              ${product.description} </a>
              </td>
            </tr>
            <tr>
            <td class="quantity">
               Quantity
            </td>
            <td>
                <input name="quantity" value="${not empty error ? param.quantity : 1}"/>
                <c:if test="${not empty error}">
                    <p class="error">
                        ${error}
                    </p>
                </c:if>
            </td>
            </tr>
        </table>
        <button>Add to cart</button>
    </form>

</tags:master>