<%@ page import="com.liferay.portlet.journal.model.JournalArticle"%>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<portlet:actionURL var="anterior" >
    <portlet:param name="dias" value="${dias - 1}" />
    <portlet:param name="action" value="navega" />
</portlet:actionURL>
<portlet:actionURL var="siguiente" >
    <portlet:param name="dias" value="${dias + 1}" />
    <portlet:param name="action" value="navega" />
</portlet:actionURL>
<div class="row-fluid">
    <div class="span4">
        <a class="navegacion" href="${anterior}"><i class="icon-chevron-left"></i> <liferay-ui:message key="leccion.anterior" /></a>
    </div>
    <div class="span4" style="text-align: center;">
        <h6>${fecha}</h6>
    </div>
    <div class="span4" style="text-align: right;">
        <a class="navegacion" href="${siguiente}"><liferay-ui:message key="leccion.siguiente" /> <i class="icon-chevron-right"></i></a>
    </div>
</div>
<h1 style="text-align: center;">
    <c:choose>
        <c:when test="${leccion != null}">
            ${leccion.title}
        </c:when>
        <c:otherwise>
            Esta lecci�n no ha sido cargada (favor de avisar al administrador)
        </c:otherwise>
    </c:choose>
</h1>
<c:if test="${leccion != null}">
    <div class="caja">
        <div>
            ${contenido}
        </div>
        <div>
            <div>
                <c:choose>
                    <c:when test="${assetEntry.viewCount eq 1}">
                        ${assetEntry.viewCount} <liferay-ui:message key="view" />,&nbsp;
                    </c:when>
                    <c:when test="${assetEntry.viewCount gt 1}">
                        ${assetEntry.viewCount} <liferay-ui:message key="views" />,&nbsp;
                    </c:when>
                </c:choose>
                <liferay-ui:flags
                    className="<%= JournalArticle.class.getName() %>"
                    classPK="${leccion.resourcePrimKey}"
                    contentTitle="${leccion.title}"
                    reportedUserId="${leccion.userId}"
                    />
                <liferay-ui:ratings
                    className="<%= JournalArticle.class.getName() %>"
                    classPK="${leccion.resourcePrimKey}"
                    />
            </div>
            <div class="mensajesDiscusion" style="margin-top: 20px;">
                <!-- AddThis Button BEGIN -->
                <div class="addthis_toolbox addthis_default_style ">
                    <a class="addthis_button_facebook_like" fb:like:layout="button_count" fb:like:width="120"></a> 
                    <a class="addthis_button_tweet"></a>
                    <a class="addthis_button_google_plusone" g:plusone:size="medium"></a>
                    <a class="addthis_counter addthis_pill_style"></a>
                </div>
                <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#pubid=ra-4d8a78014d97ad87"></script>
                <!-- AddThis Button END -->
            </div>
            <div class="mensajesDiscusion">
                <c:if test="${discussionMessages != null}">
                    <liferay-ui:tabs names="comments" />
                </c:if>

                <portlet:actionURL var="discussionURL">
                    <portlet:param name="action" value="discusion" />
                    <portlet:param name="entradaId" value="${leccion.resourcePrimKey}" />
                    <portlet:param name="assetId" value="${assetEntry.primaryKey}" />
                </portlet:actionURL>

                <liferay-ui:discussion
                    formName="fm${leccion.resourcePrimKey}"
                    formAction="${discussionURL}"
                    className="<%= JournalArticle.class.getName() %>"
                    classPK="${leccion.resourcePrimKey}"
                    userId="${leccion.userId}"
                    subject="${leccion.title}"
                    redirect="${currentURL}"
                    ratingsEnabled="true"
                    />
            </div>
        </div>
    </div>

<script type="text/javascript">
$(document).ready(function() {
    var link;
    var container = $("#versiculoDiv");
    var contenidoVersiculo = $("#contenidoVersiculo");
    
    $(".caja a").click(function(e) {
        if (!($(this).hasClass("importante"))) {
            e.preventDefault();

            link = $(this).attr("href");

            container.slideUp();
            
            contenidoVersiculo.load('<portlet:resourceURL id="buscaVersiculo" />', link, function() {
                container.slideDown();
            });
        }
    });
});
</script>

</c:if>

