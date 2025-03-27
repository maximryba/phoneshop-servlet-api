<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Details">
  <p>
    ${product.description}
  </p>
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
              <td>
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
          <td>
          <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
          ${product.description} </a>
          </td>
          <td class="price">
            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
          </td>
        </tr>
    </table>

</tags:master>