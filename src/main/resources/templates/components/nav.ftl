<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">Queue</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <#if user??>
                <div class="navbar-right">
                    <p class="navbar-text">${user}</p>
                    <a href="/logout" class="btn btn-default navbar-btn">Sign out</a>
                </div>
            <#else>
            <form class="navbar-form navbar-right" action="/login" method="post">
                <div class="form-group">
                    <input type="text" placeholder="Email" name="username" class="form-control">
                </div>
                <div class="form-group">
                    <input type="password" placeholder="Password" name="password" class="form-control">
                </div>
                <button type="submit" class="btn btn-default">Sign in</button>
                <a href="/register" class="btn btn-success">Sign up</a>
            </form>
            </#if>
        </div>
    </div>
</nav>