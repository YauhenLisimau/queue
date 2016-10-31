<#include "components/header.ftl">
<table class="table">
    <thead>
    <tr>
        <th>username</th>
        <th>link</th>
        <#if user??>
        <th>actions</th>
        </#if>
    </tr>
    </thead>
    <tbody>
    <#list tasks as task>
    <tr>
        <td>${task.username}</td>
        <td>
            <#if user?? && user == task.username>
            <div class="dropdown">
                <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                    Actions <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" aria-labelledby="dropdownMenu">
                    <li><a href="/task/done?id=${task._id}">Done</a></li>
                    <li><a href="/task/cancel?id=${task._id}">Cancel</a></li>
                </ul>
            </div>
            </#if>
        </td>
    </tr>
    </#list>
    </tbody>
</table>
<#if user??>
<form class="form-inline" action="/task" method="post">
    <div class="form-group">
        <label for="url">Url</label>
        <input type="text" class="form-control" id="url" name="url" placeholder="Url">
    </div>
    <button type="submit" class="btn btn-default">Add</button>
</form>
</#if>
<#include "components/footer.ftl">