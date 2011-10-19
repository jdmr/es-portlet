<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1>Conócenos</h1>
<table class="taglib-search-iterator">
    <tbody>
        <c:forEach items="${perfiles}" var="perfil">
        <tr class="portlet-section-body results-row" onmouseout="this.className = 'portlet-section-body results-row';" onmouseover="this.className = 'portlet-section-body-hover results-row hover';">
            <td style="width: 50px;vertical-align: top;">
                <img alt='<liferay-ui:message key="user-portrait" />' class="user-profile-image" src="${perfil.imagen}" />
            </td>
            <td style="vertical-align: top;">
                <p style="font-weight: bold; letter-spacing: 2px; font-size: 1.2em;">${perfil.nombre}</p>
                <p>${perfil.titulo}</p>
                <p><a href="mailto:${perfil.correo}">${perfil.correo}</a></p>
                <p><c:if test="${not empty perfil.twitter}">Twitter:  <a class="user-twitter-link" href="http://twitter.com/${perfil.twitter}">${perfil.twitter}</a></c:if></p>
                <p><c:if test="${not empty perfil.facebook}">Facebook: ${perfil.facebook}</c:if></p>
                <p>${perfil.resena}</p>
                <p><a href="${perfil.urlPerfil}">Ver más...</a></p>
            </td>
        </tr>
        </c:forEach>
    </tbody>
</table>
    
