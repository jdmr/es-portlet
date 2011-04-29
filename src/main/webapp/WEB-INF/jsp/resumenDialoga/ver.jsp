<%@ include file="/WEB-INF/jsp/include.jsp" %>
<c:if test="${contenidoTema != null}">
    <div class="caja2">
        <div class="resumenArticulo">
            <p class="titulo"><a class="titulo" href="${verTema}" target="_blank">${tituloTema}</a></p>
            <p class="autor">${autorTema}</p>
            <p>${contenidoTema}</p>
            <p class="liga">
                <a href="${verTema}" target="_blank"><liferay-ui:message key="resumen.leerMas" /></a>
            </p>
        </div>
    </div>
</c:if>
