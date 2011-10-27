<%@ include file="/WEB-INF/jsp/include.jsp" %>
<table class="taglib-search-iterator">
    <tbody>
        <c:forEach items="${articulos}" var="tema">
        <tr class="portlet-section-body results-row" onmouseout="this.className = 'portlet-section-body results-row';" onmouseover="this.className = 'portlet-section-body-hover results-row hover';">
            <td style="vertical-align: top;">
                <p style="font-size: 1.3em;"><a href="${tema.url}">${tema.titulo}</a></p>
                <p>${tema.contenido} <a href="${tema.url}"><liferay-ui:message key="misArticulos.leerMas" /></a></p>
            </td>
        </tr>
        </c:forEach>
    </tbody>
</table>
    
