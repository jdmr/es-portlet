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
<table class="articulo">
    <tbody>
        <tr>
            <td class="lado">
                <div class="articulo">
                    <p class="titulo"><a class="titulo" href="${verTema1}" >${tema1.titulo}</a></p>
                </div>
            </td>
            <td class="centro">&nbsp;</td>
            <td class="lado">
                <div class="articulo">
                    <p class="titulo"><a class="titulo" href="${verTema2}" >${tema2.titulo}</a></p>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <p class="autor">${tema1.autor}</p>
            </td>
            <td>&nbsp;</td>
            <td>
                <p class="autor">${tema2.autor}</p>
            </td>
        </tr>
        <tr>
            <td class="caja">
                <p>${tema1.contenido}</p>
                <p class="liga">
                    <a href="${verTema1}"><liferay-ui:message key="resumen.leerMas" /></a>
                </p>
            </td>
            <td>&nbsp;</td>
            <td class="caja">
                <p>${tema2.contenido}</p>
                <p class="liga">
                    <a href="${verTema2}"><liferay-ui:message key="resumen.leerMas" /></a>
                </p>
            </td>
        </tr>
    </tbody>
</table>
