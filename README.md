# java-filmorate
Template repository for Filmorate project.

![Database](/database.png)

Получить все фильмы
```SQL
SELECT *
FROM Film;
```

Получить топ 10 фильмов
```SQL
SELECT f.idFilm,
f.name,
COUNT(l.idUser) AS number_of_likes                                                      
FROM Film AS f
JOIN Likes AS l ON l.idFilm = f.idFilm                                                    
GROUP BY f.idFilm
ORDER BY number_of_likes DESC
LIMIT 10;    
``` 

Получить всех пользователей
```SQL
SELECT *
FROM User;
```

Получить список общих друзей у пользователей  id = 1 и id = 2
```SQL
SELECT idFriends                                                        
FROM (SELECT idUser, 
idFriends 
FROM friends 
WHERE idUser = 1) AS friends1                                                        
INTERSECT SELECT idFriends
FROM (SELECT idUser, 
idFriends 
FROM friends 
WHERE idUser = 2) AS friends2;  
```