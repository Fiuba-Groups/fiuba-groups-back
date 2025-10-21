# fiuba-groups-back
## levantar todo en docker (app + db)
Se puede levantar el back en local usando docker con:
```sh
docker compose up
```
o con
```sh
docker compose up --build
```
para buildear y levantar.

## levantar back en local y conectar a db dockerizada
Para evitar buildear la app cada vez que hay un cambio en el codebase, se puede levantar la app en local y conectarla a la db levantada en docker:
```sh
docker compose up db  # sólo la db
gradle bootRun
```

## swagger
Se puede ver la documentación de los endpoints en local en [swagger-ui](http://localhost:8080/swagger-ui/index.html) (con la app levantada).
