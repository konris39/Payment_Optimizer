# Hi and welcome to my payment optimizer 

## Requirements:

- Java Runtime Enviroment (App was written in java 21)
- Maven

To check if you have requirements installed write in terminal:
```
java -version
mvn -version
```
It should return their versions!

## To run build application:
Go to root folder of application in terminal and type:
```
mvn clean package
```

(It generates 2 files, app.jar which is fat-jar and original.jar which does NOT include libraries, files are created in target folder)

## To run aplication simply write in your terminal:

```
java -jar path/for/app.jar path/for/orders.json path/for/paymentmethods.json
```


## Input files formats:

orders.json:
```
[
  {
    "id": "ORDER1",
    "value": "100.00",
    "promotions": [
      "mZysk"
    ]
  },
...
]
```

paymentmethods.json:
```
[
  {
    "id": "PUNKTY",
    "discount": "15",
    "limit": "100.00"
  },
...
]
```

