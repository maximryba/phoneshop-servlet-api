<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Price history">
    <h1>
        Price history
    </h1>
  <p>
    ${product.description}
  </p>
   <table>
      <thead>
           <tr>
             <td>Start date</td>
             <td>
             Price
             </td>
           </tr>
         </thead>
             <c:forEach var="priceHistory" items="${priceHistories}">
               <tr>
                 <td>
                   ${priceHistory.startDate}
                 </td>
                 <td class="price">
                   <fmt:formatNumber value="${priceHistory.price}" type="currency" currencySymbol="${priceHistory.currency.symbol}"/>
                 </td>
               </tr>
             </c:forEach>
    </table>

</tags:master>