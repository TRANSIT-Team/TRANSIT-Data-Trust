# API CALLS RIGHTS SERVICE

 <br>
 <br>

## NODE

| Endpoint                              | Method | Description                                  |
| ------------------------------------- | ------ | -------------------------------------------- |
| `/node`                               | POST   | Creates a new node.                          |
| `/node/{node_id}`                     | DELETE | Deletes an existing node.                    |
| `/node/{node_id}`                     | PATCH  | Updates an existing node.                    |
| `/node/{node_id}?targetGroup={group}` | GET    | Get rights for an existing node for a group. |


### Create a Node

- Method: POST
- Endpoint: `/node`
- Description: Creates a new node and connects it to another node with a connection.
- Request Body:
```json
  {
    "name": "string",
    "type":"O",
    "id": "UUID",
    "properties": "(optional)",
    "connections":[ "(optional)"
    ]
  }
```




 ---
 <br>
 <br>

## CONNECTIONS
# (Assigment, Association, RightsConnection und bei der Association noch die Operationen)
#  `/connection/{node_id}?targetId=""&type=""&operations=[]&callingGroup=""`
# type und operations optional



| Endpoint                            | Method | Description                     |
| ----------------------------------- | ------ | ------------------------------- |
| `/connection/{node_id}?targedId=""` | POST   | Creates a new connection.       |
| `/connection/{node_id}?targedId=""` | DELETE | Deletes an existing connection. |
| `/connection/{node_id}`             | PATCH  | Updates an existing connection. |



### Create a Connection
- Method: POST
- Endpoint: `/connection/` (targedId can't be null)
- Description: Creates a connection from a node to another node.
- Request Body:
```json
  {
    "sourceId":"id1",
    "targetId": "id2",
    "type": "{ASSIGMENT, ASSOCIATION, RIGHTS}",
    "oOperations": "r,w (bedingt optional)",
    "oaOperations" :"c (bedingt optional)"
  }
```

### Update a Connection
- Method: PUT
- Endpoint: `/connection/?callinggroup="id1"&targetgroup="id2"` 
- Description: Updates a connection from a node to another node. Until now you can only update Association
- Request Body:
```json
  {
    "sourceId":"id1",
    "targetId": "id2",
    "type": "{ASSIGMENT, ASSOCIATION, RIGHTS}",
    "oOperations": "r,w (bedingt optional)",
    "oaOperations" :"c (bedingt optional)"
  }
```


### Update a Connection
- Method: PUT
- Endpoint: `/connection/{targetID}/associations?callinggroup="id1"&targetgroup="id2"` (targedId can't be null)
- Description: Get Related Associations for Companies and Target
- Response Body:
```json
[
    {
        "sourceId": "id1",
        "targetId": "oa1ID",
        "type": "ASSOCIATION",
        "oaOperations": "c",
        "oOperations": ""
    },
    {
        "sourceId": "id2",
        "targetId": "oa1ID",
        "type": "ASSOCIATION",
        "oaOperations": "",
        "oOperations": "r"
    }
]
```


 ---
 <br>
 <br>

## Rights

### Get Rights

GET:`/node/{node_id}/rights?callinggroup=55&targetgroup=55`
 ```json
 {
  "id":"",
  "oAId":"",
  "properties":{
    "readProperties": [] ,
     "writeProperties" : []}
 }

```

### Edit rights

1. Create a Node
2. Create a Connection
3. Edit the rights by accessing with groupId and that nodeId

PUT:`/node/{node_id}/rights?callinggroup=55&targetgroup=56`
 ```json
{
    "properties": [
        {
    
            "key":"readProperties",
            "value":"weight,height,width, depth"
        },
        {
    
            "key":"writeProperties",
            "value":"height,width, depth"
        }
    ]
}

```


<br>
<br>

## Backend Rights API

{{backend_url}}/entityrights/{id}

### Current

```json
{
  "canReadCompanies": [{
    "hasRightsCompany": "{{companyId1}}",
    "getRightsCompany":"{{companyId2}}"
 }]
}
```

### New PUT
```json
{ "entries": 
  [
    {
      "companyId": "bb9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "properties": 
        {
          "readProperties": ["weight", "height"],
          "writeProperties":["weight", "height"]
        }      
    },
     {
      "companyId": "aa9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "properties": 
        {
          "readProperties": ["height"],
          "writeProperties":[],
        }      
    }
  ]
}
```


Calling CompanyId extracting from AccessToken.
Then the Access Service does the rest.

POST für eine Order (mit 2 Paketen) outsourcen an ein anderes Unternehmen:

```json
{{backend_url}}/entityrights/{orderId}
{{backend_url}}/entityrights/{addressFromId}
{{backend_url}}/entityrights/{addressToId}
{{backend_url}}/entityrights/{packageId}
{{backend_url}}/entityrights/{packagePropertyId}
{{backend_url}}/entityrights/{packagePropertyId}
{{backend_url}}/entityrights/{packagePropertyId}
{{backend_url}}/entityrights/{packagePropertyId}
{{backend_url}}/entityrights/{packageId}
{{backend_url}}/entityrights/{packagePropertyId}
{{backend_url}}/entityrights/{packagePropertyId}
{{backend_url}}/entityrights/{packagePropertyId}
{{backend_url}}/entityrights/{packagePropertyId}
```



### NewNew


{{backend_url}}/entityrights

```json
{
  [
    {
      "entityId": "bb9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "companyId": "bb9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "properties": [{
        "readProperties": ["weight", "height"],
        "writeProperties":["weight", "height"],
      }]
    },
     {
      "entityId": "bb9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "companyId": "aa9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "properties": [{
        "readProperties": ["height"],
        "writeProperties":[],
      }]
    }
  ]
}
```




### GET Rights


{{backend_url}}/entityrights/{id}?companyid=54

#### Response
```json
{ "entries": 
  [
    {
      "entityId": "aa9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "companyId": "bb9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "properties": 
        {
          "readProperties": ["weight", "height"],
          "writeProperties":["weight", "height"]
        }      
    }
  ]
}
```


### UDPATE Rights

PATCH
{{backend_url}}/entityrights/{id}

#### Response
```json
{ "entries": 
  [
    {
      "entityId": "aa9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "companyId": "bb9ef0e2-dc00-46d2-b6be-0bc4009d8bc8",
      "properties": 
        {
          "readProperties": ["weight", "height"],
          "writeProperties":["weight", "height"]
        }      
    }
  ]
}
```
