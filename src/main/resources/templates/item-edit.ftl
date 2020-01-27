<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8" />
        <title><#if add>Create item<#else>Edit an item</#if></title>
        <link rel="stylesheet" type="text/css" href="/css/style.css"/>
    </head>
    <body>
        <h1><#if add>Create an Item:<#else>Edit an Item:</#if></h1>
        <a href="/items">Back to Item List</a>
        <br/><br/>
        <#if add>
            <#assign urlAction>/items/add</#assign>
            <#assign submitTitle>Create</#assign>
        <#else>
            <#assign urlAction>${'/items/' + item.itemId + '/edit'}</#assign>
            <#assign submitTitle>Update</#assign>
        </#if>
        <form action="${urlAction}" enctype="multipart/form-data" name="item" method="POST">
            <table border="0">
                <#if item.itemId??>
                <tr>
                    <td>ID</td>
                    <td>:</td>
                    <td>${item.itemId}</td>             
                </tr>
                <tr>
                    <td>Image</td>
                    <td>:</td>
                    <td><#if item.image64BaseStr??>
                       <img src="data:image/png;base64, ${item.image64BaseStr}" alt="Item image" />
                        <#else>no image</#if>
                       </td>    
                    <td>upload image<input id="fileInput" type="file" name="uploadingFile"/> </td>     
                </tr>
                <tr>
                    <td>Modified Image</td>
                    <td>:</td>
                    <td><#if item.modifiedImage64BaseStr??>
                         <img src="data:image/png;base64, ${item.modifiedImage64BaseStr}" alt="Item image" />
                         <#else>no image</#if></td>          
                </tr>
                </#if>
                <tr>
                    <td>Title</td>
                    <td>:</td>
                    <td><input type="text" name="title" value="${(item.title)!''}" /></td>              
                </tr>
                <tr>
                    <td>Description</td>
                    <td>:</td>
                    <td><textarea name="description" rows="4" cols="50">${(item.description)!""}</textarea></td>                    
                </tr>             
            </table>
            <input type="submit" value="${submitTitle}" />
        </form>
 
        <br/>
        <!-- Check if errorMessage is not null and not empty -->       
        <#if errorMessage?has_content>
            <div class="error">${errorMessage}</div>
        </#if>       
    </body>
</html>
                    