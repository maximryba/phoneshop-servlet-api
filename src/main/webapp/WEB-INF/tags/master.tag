<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="true" %>

<html>
<head>
  <title>${pageTitle}</title>
  <link href='http://fonts.googleapis.com/css?family=Lobster+Two' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/styles/main.css">
</head>
<body class="product-list">
  <header>
    <a href="${pageContext.servletContext.contextPath}">
      <img src="${pageContext.servletContext.contextPath}/images/logo.svg"/>
      PhoneShop
    </a>
    <a href="${pageContext.servletContext.contextPath}/cart">
    <jsp:include page="/cart/minicart"/>
    </a>
  </header>
  <main>
    <jsp:doBody/>
  </main>
  <a href="${pageContext.servletContext.contextPath}/products/last-viewed">
    Last three viewed products
    </a> <br>
  (c) Expert-Soft
</body>
</html>