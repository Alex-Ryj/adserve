<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Item List</title>
        <link rel="stylesheet" type="text/css" href="/css/style.css"/>
    </head>
    <body>
        <h1>Items List</h1>
        
        <div>
            <nobr>
                <a href="/items/add">Add Item</a> |
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
                        <td>  <img src="data:image/png;base64, ${item.image64BaseStr}" alt="Item image" /></td>
                        <td><a href="${'items/' + item.itemId}">${item.title}</a></td>
                        <td><a href="${'items/' + item.itemId}">${item.title}</a></td>
                         <td><a href="${item.viewItemURL}">View</a></td>
                        <td>${(item.createdOn)}</td>
                        <td>${(item.updatedOn)}</td>
                        <td><a href="${'items/' + item.itemId + '/edit'}">Edit</a></td>
                    </tr>
                </#list>
                            </table>          
        </div>
        <div>
  <p>Taken from wikpedia</p>
  <img src="data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAAAUA
    AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO
        9TXL0Y4OHwAAAABJRU5ErkJggg==" alt="Red dot" />
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
