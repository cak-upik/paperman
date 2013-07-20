<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome to Paperman Server HTTP Invoker</title>
    </head>
    
    <body>
        <img alt="Logo Paperman" src="splash.png">
        <p>Assalammualaykum! This is the default welcome page for a Paperman Spring Web MVC project.</p>
        <p><b>If you're seeing this page via a web browser, it means you've setup Paperman-Server-HttpInvoker successfully. Congratulations!</b></p>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th align="center">Operation</th>
                <th align="center">Description</th>
            </tr>
                <tr>
                    <td align="justify"><a href="http://<% out.print(request.getLocalAddr()); %>:<% out.print(request.getLocalPort()); %>/manager/status" target="_blank">Server Status</a></td>
                    <td align = "justify"> View status of client connection</td>
                </tr>

                <tr>
                    <td align="justify"><a href="http://<% out.print(request.getLocalAddr()); %>:<% out.print(request.getLocalPort()); %>/manager/html" target="_blank">Server Manager</a></td>
                    <td align = "justify"> Manage properties of client connection and deploy a new war files</td>
                </tr>
        </table>
        <p><u>Thank's For Using Paperman Payroll Software!</u></p>
    </body>
</html>
