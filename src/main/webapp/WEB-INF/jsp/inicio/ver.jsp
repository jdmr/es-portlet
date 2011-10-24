<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div id="home-top-left">
    <c:choose>
        <c:when test="${videoLeccion != null}">
            <p style="text-align:center;">
            <iframe 
                src="${videoLeccion}" 
                width="640"
                height="315"
                frameborder="0">
            </iframe>
            </p>
        </c:when>
        <c:otherwise>
            <img alt="" src="${imagenLeccion}" />
        </c:otherwise>
    </c:choose>
    <c:if test="${audioLeccion != null}">
        <div id="<portlet:namespace />podcastSemanalDiv" ></div>
    </c:if>
</div>
<div id="home-top-right">
    <c:if test="${audioLeccion2 != null}">
        <div id="<portlet:namespace />podcastDiarioDiv" ></div>
    </c:if>
    <p class="titulo"><a href="/estudia" class="titulo">${tituloLeccion}</a></p>
    <p class="fecha">${fecha}</p>
    <p class="top">${contenidoLeccion}</p>
    <p class="liga">
        <a href="/estudia"><liferay-ui:message key="inicio.leerMas" /></a>
    </p>
</div>
<div id="home-bottom-left" class="home-bottom">
    <p class="titulo"><liferay-ui:message key="inicio.dialoga" /></p>
    <div class="inner-box">
        <c:forEach items="${temasDialoga}" var="tema">
            <a href="${tema.url}" class="titulo">${tema.titulo}</a>
            <p class="autor">${tema.autor}</p>
            <p>${tema.contenido}</p>
            <p class="liga">
                <a href="${tema.url}"><liferay-ui:message key="inicio.leerMas" /></a>
            </p>
        </c:forEach>
    </div>
</div>
<div id="home-bottom-right" class="home-bottom">
    <p class="titulo"><liferay-ui:message key="inicio.comunica" /></p>
    <div class="inner-box">
        <c:forEach items="${temasComunica}" var="tema">
            <a href="${tema.url}" class="titulo">${tema.titulo}</a>
            <p class="autor">${tema.autor}</p>
            <p>${tema.contenido}</p>
            <p class="liga">
                <a href="${tema.url}"><liferay-ui:message key="inicio.leerMas" /></a>
            </p>
        </c:forEach>
    </div>
</div>
<c:if test="${audioLeccion != null}">
    <script type="text/javascript">
    $(document).ready(function() {
        jwplayer("<portlet:namespace />podcastSemanalDiv").setup({
        'flashplayer': '/es-portlet/jwplayer/player.swf',
        'file': '${podcastSemanalURL}',
        'controlbar': 'bottom',
        'width': '640',
        'height': '24',
        'provider': 'sound'
        });
        
        jwplayer("<portlet:namespace />podcastDiarioDiv").setup({
        'flashplayer': '/es-portlet/jwplayer/player.swf',
        'file': '${podcastDiarioURL}',
        'controlbar': 'bottom',
        'width': '270',
        'height': '24',
        'provider': 'sound'
        });
    });
    </script>
</c:if>