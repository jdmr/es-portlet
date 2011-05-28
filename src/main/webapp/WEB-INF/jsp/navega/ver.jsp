<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="cambiaFecha"/>
</portlet:actionURL>
<form id="navegaForm" action="${actionUrl}" method="post">
	<input type="hidden" name="fechaNavegaTxt" id="fechaNavegaTxt" />
</form>
<div id="fechaNavega"></div>
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
});
</script>