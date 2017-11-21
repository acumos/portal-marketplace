<html>
 
    <body>
        <p>Dear <#if user.firstName??>${user.firstName},<#else>User,</#if></p>
        <p>
            Your Acumos Account has been created successfully.
        </p>

        <p>Thanks</p>
        <p>${signature?if_exists}</p>
    </body>
</html>