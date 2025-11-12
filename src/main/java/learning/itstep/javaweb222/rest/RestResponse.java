package learning.itstep.javaweb222.rest;


public class RestResponse {
    private RestStatus status = RestStatus.status200;
    private RestMeta meta;
    private Object data;

    public RestMeta getMeta() {
        return meta;
    }

    public void setMeta(RestMeta meta) {
        this.meta = meta;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public RestStatus getStatus() {
        return status;
    }

    public void setStatus(RestStatus status) {
        this.status = status;
    }
    
}



/*
REST Representational State Transfer - архітектурний стиль програмного забезпечення
Зазвичай, мова іде про дотримання переліку вимог до інформаційної системи.
Client/Server – для веб - очевидно
Stateless – заборона сесій, сервер не повинен зберігати даних про попередні
  взаємодії - не мати стану. -> всі дані про "пам'ять" потрібно вкладати до 
  запитів. Зазвичай це включається до токенів
Cache – Сервер повинен повідомляти клієнта про можливість кешування даних.
  Зазвичай зазначається час, протягом якого дані не будуть змінюватись.
  Зі свого боку клієнт повинен кешувати дані, не надсилаючи повторні запити.
Layered system – можливість проксі, проміжних вузлів між клієнтом і сервером
  client ---------> server
<---400---
  client ---------> proxy -------> server
<--500----      <---400---
          time=12347        time=12345
          server=nginx      server=Tomcat
-> Є потреба розділити дані про сутність та про шлях сутності
    Статус запитів НТТР засвідчують успішність проходження запитів
    Статус роботи - включається у пакет додатково
 
  client ---------> proxy -------> server
<--200----      <---200---
        {status: 400}    {status: 400}

 
Code on demand (optional) – Дозволяється код, зокрема, HTML / SVG
 
Uniform interface -
- Resource identification in requests: назва ресурсу включається у запит
- Resource manipulation through representations: до відповіді додається 
    метаінформація про можливості маніпуляції ресурсом (CRUD)
- Self-descriptive messages: відповідь повинна містити дані про тип контента
    чи способи його оброблення
- Hypermedia as the engine of application state (HATEOAS) – включення до 
    відповіді відомостей про "зміст" - додаткові посилання в межах даного
    сервісу (або всіх посилань для "головної" сторінки)
 
---------------- зразок REST формату ----------------------
GET /api/groups -->
 
{
    "status": {
        "code": 200,
        "isOk": true,
        "phrase": "OK"
    },
    "meta": {
        "service": "Shop API 'Product groups'",
        "cacheSeconds": 1000,
        "pagination": {
            "page": 2,
            "perPage": 10,
            "lastPage": 3,
            "totalCount": 25
        },
        "dataType": "json.array",
        "links": {
            "groups": "/api/groups",
            "group": "/api/groups/{id}",
        },
        "manpulations": ["GET", "POST"],
        "pathParams": ["glass"],
Д.З. REST - додати до метаданих відповіді сервера параметр
"pathParams": ["glass"],
який буде відповідати за варіативні частини запиту,
(/groups/glass)
на базі побудована відповідь
 
    },
    "data": [
        {
          "id": "0b227043-a994-11f0-9062-62517600596c",
          "parentId": null,
          "name": "Скло",
          "description": "Вироби з кольорового та прозорого скла",
          "slug": "glass",
          "imageUrl": "http://localhost:8080/JavaWeb222/file/76d2e198-d8e0-463d-bc86-482c87ce73b6.jpg",
          "deletedAt": null,
          "products": null
        },
        {...
        }
    ]
}
*/