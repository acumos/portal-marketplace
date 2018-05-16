<html>
 
    <body>
        <p>Dear <#if user.firstName??>${user.firstName},<#else>User,</#if></p>
        <p>
            Your new Acumos Password is : ${user.loginHash?if_exists}
        </p>

        <p>Thanks</p>
        <p>${signature?if_exists}</p>
    </body>
</html>
