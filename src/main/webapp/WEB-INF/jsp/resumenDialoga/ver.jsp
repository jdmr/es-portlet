<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:if test="${contenidoTema != null}">
    <div class="caja">
        <div class="resumenArticulo">
            <p class="titulo"><a class="titulo" href="/dialoga" target="_blank">${tituloTema}</a></p>
            <p>${contenidoTema}</p>
            <p class="liga">
                <a href="/dialoga"><liferay-ui:message key="resumen.leerMas" /></a>
            </p>
        </div>
    </div>
</c:if>
