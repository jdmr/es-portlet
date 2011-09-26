<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div id="versiculoDiv" <c:if test="${empty texto}">style="display:none;"</c:if>>
    <div class="caja2">
        <div class="biblia">
            <div id="contenidoVersiculo">
                <h2>${ubicacion}</h2>
                ${texto}
            </div>
            <div class="navegaVersiculo">
                <portlet:renderURL var="anterior" >
                    <portlet:param name="vid" value="${vid - 5}" />
                </portlet:renderURL>
                <portlet:renderURL var="siguiente" >
                    <portlet:param name="vid" value="${vid + 5}" />
                </portlet:renderURL>
                <div id="versiculoAnterior" style="float:left;font-size: 0.8em;">
                    <a id="versiculoAnteriorLink" href="${anterior}"><< <liferay-ui:message key="biblia.anterior" /></a>
                </div>
                <div id="versiculoSiguiente" style="text-align: right;font-size: 0.8em;">
                    <a id="versiculoSiguienteLink" href="${siguiente}"><liferay-ui:message key="biblia.siguiente" /> >></a>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function() {
    var container = $("#contenidoVersiculo");
    
    $("a#versiculoAnteriorLink").click(function(e) {
        e.preventDefault();
        container.load('<portlet:resourceURL id="versiculoAnterior" />', function() {
            container.hide("slide",{direction:"right"});
            container.show("slide",{direction:"left"});
        });
    });
    $("a#versiculoSiguienteLink").click(function(e) {
        e.preventDefault();
        container.load('<portlet:resourceURL id="versiculoSiguiente" />', function() {
            container.hide("slide",{direction:"left"});
            container.show("slide",{direction:"right"});
        });
    });
});
</script>
