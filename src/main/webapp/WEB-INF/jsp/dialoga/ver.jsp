<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="verTema1" >
    <portlet:param name="assetId" value="${tema1.assetId}" />
    <portlet:param name="entradaId" value="${tema1.entradaId}" />
    <portlet:param name="action" value="completo" />
</portlet:renderURL>
<portlet:renderURL var="verTema2" >
    <portlet:param name="assetId" value="${tema2.assetId}" />
    <portlet:param name="entradaId" value="${tema2.entradaId}" />
    <portlet:param name="action" value="completo" />
</portlet:renderURL>
<div class="row-fluid">
    <div class="span6">
        <h1 style="text-align: center;"><a class="titulo" href="${verTema1}" >${tema1.titulo}</a></h1>
        <h4>Por ${tema1.autor}</h4>
        <p>${tema1.contenido}</p>
        <p class="liga">
            <a href="${verTema1}" class="btn btn-primary btn-large"><liferay-ui:message key="resumen.leerMas" /></a>
        </p>
    </div>
    <div class="span6">
        <h1 style="text-align: center;"><a class="titulo" href="${verTema2}" >${tema2.titulo}</a></h1>
        <h4>Por ${tema2.autor}</h4>
        <p>${tema2.contenido}</p>
        <p class="liga">
            <a href="${verTema2}" class="btn btn-primary btn-large"><liferay-ui:message key="resumen.leerMas" /></a>
        </p>
    </div>
</div>
