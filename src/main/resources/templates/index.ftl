<#include "components/header.ftl">
<form class="form-inline" action="/task" method="post">
    <div class="form-group">
        <label for="taskId">Request id</label>
        <input type="text" class="form-control" id="taskId" name="taskId" placeholder="Request id">
    </div>
    <button type="submit" class="btn btn-default">Add to queue</button>
</form>
<table class="table">
    <thead>
    <tr>
        <th>created</th>
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
        <td>${task.creationDate?datetime}</td>
        <td>${task.username}</td>
        <td><a href="${task.requestUrl}">${task.id}</a></td>
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
<#include "components/footer.ftl">