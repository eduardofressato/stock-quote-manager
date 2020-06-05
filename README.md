# Build and Run

## Create a docker network
`docker network create my-network`

## Run MYSQL

`docker container run --name mysqlicc --network=my-network -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=bootdb -p 3306:3306 -p 33060:33060 -d mysql:8`

## Run API stock manager

`docker container run --name stockmanager --network=my-network -p 8080:8080 -d lucasvilela/stock-manager`


## Build and Run API stock quotes manager

### local

`docker build . -t squotesmanager --network=my-network`

`docker container run --name squotesmanagerapi -p 8081:8081 --network=my-network -d squotesmanager`

### docker hub

`docker container run --name stockquotesmanagerapi -p 8081:8081 --network=my-network -d eduardofressato/squotesmanager`

# Endpoints

### Create: 
POST `localhost:8081/stockquotes`

`{
	"id": "usa",
	"quotes": {
		"2019-01-01": "10",
		"2019-01-03": "14"
	}
}`

### List ALL: 
GET `localhost:8081/stockquotes`

### List One: 
GET `localhost:8081/stockquotes/id`

### Delete Cache
DELETE `localhost:8081/stockcache`