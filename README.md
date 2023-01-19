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
SELECT f.id_film,
f.name,
COUNT(l.id_user) AS number_of_likes                                                      
FROM Film AS f
JOIN Likes AS l ON l.id_film = f.id_film                                                    
GROUP BY f.id_film
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
SELECT id_friends                                                        
FROM (SELECT id_user, 
id_friends 
FROM friends 
WHERE id_user = 1) AS friends_1                                                        
INTERSECT SELECT id_friends
FROM (SELECT id_user, 
id_friends 
FROM friends 
WHERE id_user = 2) AS friends_2;  
```