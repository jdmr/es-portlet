<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="cambiaFecha"/>
</portlet:actionURL>
<form id="navegaForm" action="${actionUrl}" method="post">
	<input type="hidden" name="fechaNavegaTxt" id="fechaNavegaTxt" />
</form>
<div id="fechaNavega"></div>
<c:if test="${audioLeccion != null}">
    <div id="<portlet:namespace />podcastDiarioDiv" ></div>
</c:if>

<script type="text/javascript">
$(document).ready(function() {
	$( "#fechaNavega" ).datepicker({
		altField:"#fechaNavegaTxt",
		altFormat:"dd/mm/yy",
		dateFormat:"dd/mm/yy",
		defaultDate:"${hoyString}",
		firstDay: 0,
		showOtherMonths: true,
		selectOtherMonths: true,
		minDate: "26/03/2011",
		onSelect:function(dateText, inst) {
			$("form#navegaForm").submit();
		}
	});
	$( "#fechaNavega" ).datepicker($.datepicker.regional['es']);
        
        <c:if test="${audioLeccion != null}">
        jwplayer("<portlet:namespace />podcastDiarioDiv").setup({
        'flashplayer': '/es-portlet/jwplayer/player.swf',
        'file': '${podcastDiarioURL}',
        'controlbar': 'bottom',
        'width': '260',
        'height': '24',
        'provider': 'sound'
        });
        </c:if>
});
</script>