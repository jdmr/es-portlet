<%@ include file="/WEB-INF/jsp/include.jsp" %>
<c:if test="${contenidoTema != null}">
    <div class="caja2">
        <div class="resumenArticulo">
            <h3><a class="titulo" href="${verTema}" target="_blank">${tituloTema}</a></h3>
            <h5>Por ${autorTema}</h5>
            <p>${contenidoTema}</p>
            <p class="liga">
                <a href="${verTema}" class="btn btn-primary" target="_blank"><liferay-ui:message key="resumen.leerMas" /></a>
            </p>
        </div>
    </div>
</c:if>
