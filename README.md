**API**  
* `GET /api/fs` - Get filesystem structure  
_Examples_ :  
    * Get root FS structure   
    Request :   
    ``` GET /api/fs ```  
    Response:   
    ```json
      [
        {  
          "mediaType": "image/jpeg",  
          "name": "image.jpg",  
          "path": "%2Fimage.jpg",  
          "fType": "file",  
          "url": "http://localhost:5998/files/%2image.jpg"  
      },
      {
          "name": "folder",
          "path": "%2Ffolder",
          "fType": "folder"
      }
    ]    
    ``` 
    * Get inner folder   
    Request :   
    ``` GET /api/fs/%2Ffolder ```  
    Response:   
    ```json
    [
      {
          "mediaType": "image/jpeg",
          "name": "another-image.jpg",
          "path": "%2Ffolder%2Fanother-image.jpg",
          "fType": "file",
          "url": "http://localhost:5998/files/%2Ffolder%2Fanother-image.jpg"
      }
    ]    
    ```
* `GET /files` - Get file content. We use `url` field value from `/api/fs` response   
_Example_ :
    * `GET http://localhost:5998/files/%2Ffolder%2Fanother-image.jpg`
    
**How to Build**

1. Install [SBT]
2. sbt assembly