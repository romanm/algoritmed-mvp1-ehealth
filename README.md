# Мінімальний життездатний продукт електронної охорони здоров'я від algoritmed.com

прототип

Інсталяція

1. Створити і ініціювати h2 сервер бази даних

/src/main/db/h2-mvp-medical-algoritmed1-create-db.sql

/src/main/db/h2-mvp-medical-algoritmed1-init-db.sql

$>: h2-server-start.sh

Адреса БД: 

jdbc:h2:tcp://localhost/h2-am-mvp1-medic-1/h2-am-mvp1-medic-1;DATABASE_TO_UPPER=false

для розробки і тестування рекомендується http://executequery.org


2. Зробити клон проекту

$>: git clone https://github.com/romanm/algoritmed-mvp1-ehealth

$>: cd algoritmed-mvp1-ehealth

3. Виконати
$>: ./copyMedicToCurrent.sh
для конфігірації сервера медика

4. Створити робочий jar файл
$>: gradlew clean build

5. Запуск сервера
$>: java -jar build/libs/algoritmed-mvp1-ehealth-0.0.1-SNAPSHOT.jar

6. Доступ до сервера через бровзер url адрес http://localhost:8090/


оцінка відповідності 
липень 2017

