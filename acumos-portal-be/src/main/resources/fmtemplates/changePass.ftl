<html>
 
    <body>
        <p>Dear <#if user.firstName??>${user.firstName},<#else>User,</#if></p>
        <p>
            Your Acumos Password has been changed successfully. Please try to login with your new password.
        </p>

        <p>Thanks</p>
        <p>${signature?if_exists}</p>
    </body>
</html>