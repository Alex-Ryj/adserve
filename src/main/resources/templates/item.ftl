<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>View item</title>
        <link rel="stylesheet" type="text/css" href="/css/style.css"/>
    </head>
    <body>
        <h1>View item</h1>
        <a href="/items">Back to item List</a>
        <br/><br/>
        <#if item??>
            <table border="0">
              <tr>
                    <td>Image</td>
                    <td>:</td>
                    <td><img src="data:image/png;base64, ${item.image64BaseStr}" alt="Item image" /></td>          
                </tr>
                <tr>
                    <td>Modified Image</td>
                    <td>:</td>
                    <td><#if item.modifiedImage64BaseStr??>
                         <img src="data:image/png;base64, ${item.modifiedImage64BaseStr}" alt="Item image" />
                         <#else>when-missing</#if></td>          
                </tr>
                <tr>
                    <td>ID</td>
                    <td>:</td>
                    <td>${item.itemId}</td>          
                </tr>
                <tr>
                    <td>Title</td>
                    <td>:</td>
                    <td>${item.title}</td>             
                </tr>
                <tr>
                    <td>Created On</td>
                    <td>:</td>
                    <td>${(item.createdOn)}</td>              
                </tr>
                <tr>
                    <td>Updated On</td>
                    <td>:</td>
                    <td>${(item.updatedOn)}</td>              
                </tr> 
            </table>
            <br/><br/>
            <#if allowDelete>
                <form action="${'/items/' + item.itemId + '/delete'}" method="POST">
                    Delete this item? <input type="submit" value="Yes" />
                </form>
            <#else>
                <div>
                    <a href="${'/items/' + item.itemId + '/edit'}">Edit</a> |
                    <a href="${'/items/' + item.itemId + '/delete'}">Delete</a>
                </div>
            </#if>
        </#if>
        <#if errorMessage?has_content>
            <div class="error">${errorMessage}</div>
        </#if>
    </body>
</html>
            