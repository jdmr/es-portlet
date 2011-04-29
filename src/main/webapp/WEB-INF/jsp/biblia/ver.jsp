<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:if test="${not empty texto}">
    <div class="caja2">
        <div class="biblia">
            <h2>${ubicacion}</h2>
            ${texto}
            <portlet:renderURL var="anterior" >
                <portlet:param name="vid" value="${vid - 5}" />
            </portlet:renderURL>
            <portlet:renderURL var="siguiente" >
                <portlet:param name="vid" value="${vid + 5}" />
            </portlet:renderURL>
            <div>
                <div style="float:left;font-size: 0.8em;">
                    <a href="${anterior}"><< <liferay-ui:message key="biblia.anterior" /></a>
                </div>
                <div style="text-align: right;font-size: 0.8em;">
                    <a href="${siguiente}"><liferay-ui:message key="biblia.siguiente" /> >></a>
                </div>
            </div>
        </div>
    </div>
</c:if>
