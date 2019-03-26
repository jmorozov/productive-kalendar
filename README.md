# productive-kalendar

[![Build Status](https://travis-ci.com/jmorozov/productive-kalendar.svg?branch=master)](https://travis-ci.com/jmorozov/productive-kalendar)

Russian productive calendar with REST API. **K**alendar, because it is written in Kotlin.

# productive-kalendar [RU]

Приложение для получения данных из производственного календаря Российской Федерации по REST API.

## Run

### Docker

Всё становится лучше с Docker

```bash
docker build --no-cache -t productive-kalendar .
docker run -it --rm -p 8080:8080 productive-kalendar
```

После чего приложение доступно по адресу http://localhost:8080/

### Локально

Тут всё типично для приложения spring-boot и gradle-wrapper.

#### Сборка проекта
```bash
./gradlew build
```

#### Сборка проекта без тестов
```bash
./gradlew build -x test
```

#### Запуск статического анализатора

В проекте используется статический анализ и линтинг средствами detekt.

```bash
./gradlew detektCheck
```

#### Запуск приложения
```bash
./gradlew bootRun
```

## API

### Обновить информацию в календаре

Информация о выходных днях извлекается из csv-файла, последняя версия которого скачивается с портала открытых данных РФ 
(https://data.gov.ru/opendata/7708660670-proizvcalendar). На 2018-08-29 портал предоставляет данные с 1999 г. по 2025 г., 
однако данные 2019-2025 г. ещё не утверждены правительством РФ и являются приблизительными.

_POST_ http://localhost:8080/api/command/parse/gov

_RESPONSE_ обновленный список дат выходных дней в формате json

### Запросить информацию из календаря

Далее {date} - это дата в формате **гггг-ММ-дд**, например 2018-08-18. 

В запросах, где предполагается интервал дат, последняя дата не включается. Если даты одинаковые, то в ответе будет 0.
Если одна из дат интервала не указана, то вместо неё будет использоваться текущая дата. Если обе даты не указаны, то
будет возбуждено исключение и получите ошибку 400 "Incorrect request. Start and end together must not be null".

#### Является ли данная дата выходным днём

_GET_ http://localhost:8080/api/query/is/{date}/holiday

_RESPONSE_ boolean, true - выходной, false - рабочий день

#### Является ли завтра выходным днём

_GET_ http://localhost:8080/api/query/is/tomorrow/holiday

_RESPONSE_ boolean, true - выходной, false - рабочий день

#### Получить список всех выходных, которые хранятся в системе

_GET_ http://localhost:8080/api/query/all/holidays

_RESPONSE_ отсортированный по возрастанию список дат

#### Получить список всех выходных за определенный год

{year} - Int, год в диапазоне [1999, 2100]

_GET_ http://localhost:8080/api/query/{year}/holidays

_RESPONSE_ отсортированный по возрастанию список дат. Если за указанный год в системе нет информации, то вернется пустой
список

#### Получить количество рабочих дней между датами

Опциональный параметр _inclusive_ типа _Boolean_ определяет, включает ли подсчет дату _end_ в интервал или нет.
По умолчанию *false* 

_POST_ http://localhost:8080/api/query/workdays/between

*Content-Type: application/json*

```json
{
  "start": "{date}",
  "end": "{date}",
  "inclusive": {true or false(default)}
}
```

_RESPONSE_ Int - количество рабочих дней между датами

#### Получить количество выходных дней между датами

Опциональный параметр _inclusive_ типа _Boolean_ определяет, включает ли подсчет дату _end_ в интервал или нет.
По умолчанию *false* 

_POST_ http://localhost:8080/api/query/holidays/between

*Content-Type: application/json*

```json
{
  "start": "{date}",
  "end": "{date}",
  "inclusive": {true or false(default)}
}
```

_RESPONSE_ Int - количество выходных дней между датами

#### Получить весь производственный календарь

_GET_ http://localhost:8080/api/query/productive-calendar

_RESPONSE_ { "holidays": ['1999-01-01', ...], "preholidays": ['1999-03-07', ...] }

#### Получить производственный календарь за определенный год

{year} - Int, год в диапазоне [1999, 2100]

_GET_ http://localhost:8080/api/query/{year}/productive-calendar

_RESPONSE_ { "holidays": ['2018-01-01', ...], "preholidays": ['2018-03-07', ...] }

#### Получить тип дня

_GET_ http://localhost:8080/api/query/day/{date}/type

_RESPONSE_ одно из значений перечисления: WORKDAY, HOLIDAY, PREHOLIDAY

#### Получить тип завтрашнего дня

_GET_ http://localhost:8080/api/query/day/tomorrow/type

_RESPONSE_ одно из значений перечисления: WORKDAY, HOLIDAY, PREHOLIDAY

## Telegram bot

Есть возможность поднять [Telegram-бота](https://github.com/jmorozov/productive-kalendar-telegram-bot), который будет стучаться в календарь.