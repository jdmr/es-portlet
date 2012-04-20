<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="regresar" />
<div class="row">
    <div class="span12">
        <h1>${entrada.title}</h1>
        <h4>${autor}</h4>
        <span>${contenido}</span>
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
                    className="<%= com.liferay.portlet.journal.model.JournalArticle.class.getName() %>"
                    classPK="${entrada.resourcePrimKey}"
                    contentTitle="${entrada.title}"
                    reportedUserId="${entrada.userId}"
                    />
                <liferay-ui:ratings
                    className="<%= com.liferay.portlet.journal.model.JournalArticle.class.getName() %>"
                    classPK="${entrada.resourcePrimKey}"
                    />
            </div>
            <div style="margin: 10px 0;">
                <!-- AddThis Button BEGIN -->
                <div class="addthis_toolbox addthis_default_style ">
                    <a class="addthis_button_facebook_like" fb:like:layout="button_count" fb:like:width="120"></a> 
                    <a class="addthis_button_tweet"></a>
                    <a class="addthis_button_google_plusone" g:plusone:size="medium"></a>
                    <a class="addthis_counter addthis_pill_style"></a>
                </div>
                <script type="text/javascript">var addthis_config = {"data_track_addressbar":true};</script>
                <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#pubid=ra-4d8a78014d97ad87"></script>
                <!-- AddThis Button END -->
            </div>
            <div style="padding: 10px 0;">
                <c:if test="${discussionMessages != null}">
                    <liferay-ui:tabs names="comments" />
                </c:if>

                <portlet:actionURL var="discussionURL">
                    <portlet:param name="action" value="discusion" />
                    <portlet:param name="entradaId" value="${entrada.resourcePrimKey}" />
                    <portlet:param name="assetId" value="${assetEntry.primaryKey}" />
                </portlet:actionURL>

                <liferay-ui:discussion
                    formName="fm${entrada.resourcePrimKey}"
                    formAction="${discussionURL}"
                    className="<%= com.liferay.portlet.journal.model.JournalArticle.class.getName()%>"
                    classPK="${entrada.resourcePrimKey}"
                    userId="${entrada.userId}"
                    subject="${entrada.title}"
                    redirect="${currentURL}"
                    ratingsEnabled="true"
                    />
            </div>
            <div class="botones">
                <a class="importante" href="${regresar}"><liferay-ui:message key="comunica.regresa" /></a>
            </div>
        </div>        
    </div>
</div>
