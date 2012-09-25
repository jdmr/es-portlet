<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div class="row">
    <div class="span8">
        <c:choose>
            <c:when test="${videoLeccion != null}">
                <p style="text-align:center;">
                <iframe 
                    src="${videoLeccion}" 
                    width="100%"
                    height="360"
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
    <div class="span4">
        <c:if test="${audioLeccion2 != null}">
            <div id="<portlet:namespace />podcastDiarioDiv" ></div>
        </c:if>
            <h1><a href="/estudia" class="titulo">${tituloLeccion}</a></h1>
        <h3>${fecha}</h3>
        <p>${contenidoLeccion}</p>
        <p>
            <a class="btn btn-large btn-primary" href="/estudia"><liferay-ui:message key="inicio.leerMas" /></a>
        </p>
    </div>
</div>
<div class="row">
    <div class="span6">
        <c:forEach items="${temasDialoga}" var="tema">
            <h2><a href="${tema.url}">${tema.titulo}</a></h2>
            <h5>${tema.autor}</h5>
            <p>${tema.contenido}</p>
            <p>
                <a href="${tema.url}" class="btn btn-primary"><liferay-ui:message key="inicio.leerMas" /></a>
            </p>
        </c:forEach>
    </div>
    <div class="span6">
        <c:forEach items="${temasComunica}" var="tema">
            <h2><a href="${tema.url}">${tema.titulo}</a></h2>
            <h5>${tema.autor}</h5>
            <p>${tema.contenido}</p>
            <p>
                <a href="${tema.url}" class="btn btn-primary"><liferay-ui:message key="inicio.leerMas" /></a>
            </p>
        </c:forEach>
    </div>
</div>
<c:if test="${not empty podcastSemanalURL || not empty podcastDiarioURL}">
    <script type="text/javascript">
    $(document).ready(function() {
        <c:if test="${not empty podcastSemanalURL}">
            jwplayer("<portlet:namespace />podcastSemanalDiv").setup({
            'flashplayer': '/es-portlet/jwplayer/player.swf',
            'file': '${podcastSemanalURL}',
            'controlbar': 'bottom',
            'width': '640',
            'height': '24',
            'provider': 'sound'
            });
        </c:if>
        
        <c:if test="${not empty podcastDiarioURL}">
            jwplayer("<portlet:namespace />podcastDiarioDiv").setup({
            'flashplayer': '/es-portlet/jwplayer/player.swf',
            'file': '${podcastDiarioURL}',
            'controlbar': 'bottom',
            'width': '270',
            'height': '24',
            'provider': 'sound'
            });
        </c:if>
    });
    </script>
</c:if>