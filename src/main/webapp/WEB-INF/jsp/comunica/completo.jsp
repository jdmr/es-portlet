<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="regresar" />
<div class="articulo">
    <p class="tituloPrincipal">${entrada.title}</p>
    <p class="autorPrincipal">${autor}</p>
    <div class="caja">
        ${contenido}
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
                    classPK="${entrada.id}"
                    contentTitle="${entrada.title}"
                    reportedUserId="${entrada.userId}"
                    />
                <liferay-ui:ratings
                    className="<%= com.liferay.portlet.journal.model.JournalArticle.class.getName() %>"
                    classPK="${entrada.id}"
                    />
            </div>
            <div style="padding: 10px 0;">
                <!-- AddThis Button BEGIN -->
                <div class="addthis_toolbox addthis_default_style ">
                    <a class="addthis_button_facebook_like" fb:like:layout="button_count"></a>
                    <a class="addthis_button_tweet"></a>
                    <a class="addthis_counter addthis_pill_style"></a>
                </div>
                <script type="text/javascript">var addthis_config = {"data_track_clickback":true};</script>
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
                <div style="float:left;padding-right: 10px;">
                    <a class="importante" href="${messageUrl}" target="_blank"><liferay-ui:message key="comunica.opina" /></a>
                </div>
                <div>
                    <a class="importante" href="${regresar}"><liferay-ui:message key="comunica.regresa" /></a>
                </div>
            </div>
        </div>        
    </div>
</div>
