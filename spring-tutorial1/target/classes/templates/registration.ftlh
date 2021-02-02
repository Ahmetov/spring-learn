<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
    Registration page
    <#if message??>
        Error ${message}
    </#if>
    <@l.login "/registration"/>
</@c.page>
