<html>
 
    <body>
        <p>Dear <#if user.firstName??>${user.firstName},<#else>User,</#if></p>
        <p>
            Your Acumos Account Username is : ${user.username?if_exists}</br>
            And Acumos Account Password is : ${user.password?if_exists}
        </p>

        <p>Thanks</p>
        <p>${signature?if_exists}</p>
    </body>
</html>