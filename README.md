# Demo Project

Based on java 15

Swagger URL to test rest API: 

`http://localhost:8080/swagger-ui/index.html`

### Post /api/test

#### Request body format

`
{
    "nestedDemoObjects" : 
    [
        {
            "data": <alphanumeric string>
        },
        ...
    ]
}
`

#### Example 1

`
{
    "nestedDemoObjects": 
    [
        {
            "data": "abc123"
        }
    ]
}
`

Response: OK

#### Example 2

`
{
    "nestedDemoObjects": 
    [
        {
            "data": "!"
        }
    ]
}
`

Response: Bad Request