<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Note List</title>
        <link rel="stylesheet" type="text/css" href="/css/style.css"/>
    </head>
    <body>
        <h1>Note List</h1>
        
        <div>
            <nobr>
                <a href="/notes/add">Add Item</a> |
                <a href="/">Back to Index</a>
            </nobr>
        </div>
        <br/><br/>
        <div>
            <table border="1">
                <tr>
                    <th>Id</th>
                    <th>Image</th>
                    <th>Title</th>
                    <th>URL</th> 
                    <th>Edit</th>                    
                </tr>
                <#list items as item>
                    <tr>
                        <td><a href="${'items/' + item.itemId}">${item.itemId}</a></td>
                        <td><a href="${'items/' + item.itemId}">${item.title}</a></td>
                         <td><a href="${'item.viewImageURL'}">View</a></td>
                        <td>${(item.createdOn)}</td>
                        <td>${(item.updatedOn)}</td>
                        <td><a href="${'items/' + item.itemId + '/edit'}">Edit</a></td>
                    </tr>
                </#list>
                            </table>          
        </div>
        <br/><br/>
        <div>
            <nobr>
                <#if hasPrev><a href="${'items?page=' + prev}">Prev</a>&nbsp;&nbsp;&nbsp;</#if>
                <#if hasNext><a href="${'items?page=' + next}">Next</a></#if>
            </nobr>
        </div>
    </body>
</html>
