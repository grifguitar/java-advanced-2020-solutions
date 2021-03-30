# Homework for the course of Java Advanced, ITMO CT, Spring 2020

Выполнил студент группы М3236, Хлытин Григорий.

## Подготовка

Домашние задания рассчитывают на то, что рядом с репозиторием java-advanced-2020-solutions в директории
java-advanced-2020 лежит
клон [java-advanced-2020](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020)
Пожалуйста, выполните это перед началом работы.

## Тесты к курсу «Технологии Java»

[Условия домашних заданий](http://www.kgeorgiy.info/courses/java-advanced/homeworks.html)

Для запуска тестов можно воспользоваться следующим шаблоном команды, запущенным из
директории `java-advanced-2020-solutions\out\production>`:
```java -cp .\java-advanced-2020-solutions -p ..\..\java-advanced-2020\lib;..\..\java-advanced-2020\artifacts -m info.kgeorgiy.java.advanced.implementor jar-class ru.ifmo.rain.khlytin.implementor.JarImplementor```

### Домашнее задание 10. HelloUDP

Интерфейсы

* `HelloUDPClient` должен реализовывать интерфейс
  [HelloClient](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClient.java)
* `HelloUDPServer` должен реализовывать интерфейс
  [HelloServer](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServer.java)

Тестирование

* простой вариант:
    * клиент:
      ```info.kgeorgiy.java.advanced.hello client <полное имя класса>```
    * сервер:
      ```info.kgeorgiy.java.advanced.hello server <полное имя класса>```
* сложный вариант:
    * на противоположной стороне находится система, дающая ответы на различных языках
    * клиент:
      ```info.kgeorgiy.java.advanced.hello client-i18n <полное имя класса>```
    * сервер:
      ```info.kgeorgiy.java.advanced.hello server-i18n <полное имя класса>```
* продвинутый вариант:
    * на противоположной стороне находится старая система, не полностью соответствующая последней спецификации
    * клиент:
      ```info.kgeorgiy.java.advanced.hello client-evil <полное имя класса>```
    * сервер:
      ```info.kgeorgiy.java.advanced.hello server-evil <полное имя класса>```

Исходный код тестов:

* [Клиент](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClientTest.java)
* [Сервер](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServerTest.java)

### Домашнее задание 9. Web Crawler

Тестирование

* простой вариант:
  ```info.kgeorgiy.java.advanced.crawler easy <полное имя класса>```
* сложный вариант:
  ```info.kgeorgiy.java.advanced.crawler hard <полное имя класса>```

Исходный код тестов:

* [интерфейсы и вспомогательные классы](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/)
* [простой вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/CrawlerEasyTest.java)
* [сложный вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/CrawlerHardTest.java)

### Домашнее задание 8. Параллельный запуск

Тестирование

* простой вариант:
  ```info.kgeorgiy.java.advanced.mapper scalar <полное имя класса>```
* сложный вариант:
  ```info.kgeorgiy.java.advanced.mapper list <полное имя класса>```
* продвинутый вариант:
  ```info.kgeorgiy.java.advanced.mapper advanced <полное имя класса>```

Исходный код тестов:

* [простой вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ScalarMapperTest.java)
* [сложный вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ListMapperTest.java)
* [продвинутый вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/AdvancedMapperTest.java)

### Домашнее задание 7. Итеративный параллелизм

Тестирование

* простой вариант:
  ```info.kgeorgiy.java.advanced.concurrent scalar <полное имя класса>```

  Класс должен реализовывать интерфейс
  [ScalarIP](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ScalarIP.java)
  .

* сложный вариант:
  ```info.kgeorgiy.java.advanced.concurrent list <полное имя класса>```

  Класс должен реализовывать интерфейс
  [ListIP](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ListIP.java)
  .

* продвинутый вариант:
  ```info.kgeorgiy.java.advanced.concurrent advanced <полное имя класса>```

  Класс должен реализовывать интерфейс
  [AdvancedIP](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/AdvancedIP.java)
  .

Исходный код тестов:

* [простой вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ScalarIPTest.java)
* [сложный вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/ListIPTest.java)
* [продвинутый вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.concurrent/info/kgeorgiy/java/advanced/concurrent/AdvancedIPTest.java)

### Домашнее задание 5. JarImplementor

Класс должен реализовывать интерфейс
[JarImpler](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/JarImpler.java)
.

Тестирование

* простой вариант:
  ```info.kgeorgiy.java.advanced.implementor jar-interface <полное имя класса>```
* сложный вариант:
  ```info.kgeorgiy.java.advanced.implementor jar-class <полное имя класса>```
* продвинутый вариант:
  ```info.kgeorgiy.java.advanced.implementor jar-advanced <полное имя класса>```

Исходный код тестов:

* [простой вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceJarImplementorTest.java)
* [сложный вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassJarImplementorTest.java)
* [продвинутый вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedJarImplementorTest.java)

### Домашнее задание 4. Implementor

Класс должен реализовывать интерфейс
[Impler](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java)
.

Тестирование

* простой вариант:
  ```info.kgeorgiy.java.advanced.implementor interface <полное имя класса>```
* сложный вариант:
  ```info.kgeorgiy.java.advanced.implementor class <полное имя класса>```
* продвинутый вариант:
  ```info.kgeorgiy.java.advanced.implementor advanced <полное имя класса>```

Исходный код тестов:

* [простой вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceImplementorTest.java)
* [сложный вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassImplementorTest.java)
* [продвинутый вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedImplementorTest.java)

### Домашнее задание 3. Студенты

Тестирование

* простой вариант:
  ```info.kgeorgiy.java.advanced.student StudentQuery <полное имя класса>```
* сложный вариант:
  ```info.kgeorgiy.java.advanced.student StudentGroupQuery <полное имя класса>```

Исходный код

* простой вариант:
  [интерфейс](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQuery.java)
  ,
  [тесты](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQueryTest.java)
* сложный вариант:
  [интерфейс](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentGroupQuery.java)
  ,
  [тесты](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentGroupQueryTest.java)
* продвинутый вариант:
  [интерфейс](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedStudentGroupQuery.java)
  ,
  [тесты](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedStudentGroupQueryTest.java)

### Домашнее задание 2. ArraySortedSet

Тестирование

* простой вариант:
  ```info.kgeorgiy.java.advanced.arrayset SortedSet <полное имя класса>```
* сложный вариант:
  ```info.kgeorgiy.java.advanced.arrayset NavigableSet <полное имя класса>```

Исходный код тестов:

* [простой вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/SortedSetTest.java)
* [сложный вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/NavigableSetTest.java)

### Домашнее задание 1. Обход файлов

Для того, чтобы протестировать программу:

* Скачайте
    * тесты
        * [info.kgeorgiy.java.advanced.base.jar](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/artifacts/info.kgeorgiy.java.advanced.base.jar)
        * [info.kgeorgiy.java.advanced.walk.jar](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/artifacts/info.kgeorgiy.java.advanced.walk.jar)
    * и библиотеки к ним:
        * [junit-4.11.jar](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/lib/junit-4.11.jar)
        * [hamcrest-core-1.3.jar](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/lib/hamcrest-core-1.3.jar)
* Откомпилируйте решение домашнего задания
* Протестируйте домашнее задание
    * Текущая директория должна:
        * содержать все скачанные `.jar` файлы;
        * содержать скомпилированное решение;
        * __не__ содержать скомпилированные самостоятельно тесты.
    * простой вариант:
      ```java -cp . -p . -m info.kgeorgiy.java.advanced.walk Walk <полное имя класса>```
    * сложный вариант:
      ```java -cp . -p . -m info.kgeorgiy.java.advanced.walk RecursiveWalk <полное имя класса>```

Исходный код тестов:

* [простой вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/WalkTest.java)
* [сложный вариант](https://github.com/grifguitar/java-advanced-2020-solutions/tree/master/java-advanced-2020/modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/RecursiveWalkTest.java)

## Правила игры

Далее стандартный текст:

Этот репозиторий будет склонирован для каждого студента и доступен по адресу
`http://ctddev.ifmo.ru:25231/git/<user>/java-advanced-2020-solutions`, где `<user>` — имя пользователя и пароль, которые
вам пришлют на `@niuitmo.ru`

Для сдачи домашних заданий

* Клонируйте ваш личный репозиторий
    * `git clone http://ctddev.ifmo.ru:25231/git/<user>/java-advanced-2020-solutions`
    * У личных репозиториев __нет__ web-интерфейса, используйте инструменты командной строки.
* Добавьте ссылку на исходный репозиторий
    * `git remote add source https://www.kgeorgiy.info/git/geo/java-advanced-2020-solutions`
    * По мере появления новых домашних заданий в исходном репозитории будут появляться заготовки решений забирайте из
      через `git pull source`.
* Переименуйте пакет `ru.ifmo.rain.khlytin`, заменив
  `__last_name__` на вашу фамилию.
    * В остальном сохраняйте текущую структуру каталогов и имена файлов.
    * Если структура репозитория не соответсвует исходной, преподаватель не будет проверять решение.
* Добавляйте только исходные файлы решений
* Вы можете редактировать `.gitignore` как вам удобно
* Отправьте решение на проверку
    * Проверьте, что все исходники компилируеются
    * Проверьте, что тесты сдаваемого ДЗ проходят
    * Закоммитьте все изменения в `master`
    * Запушите все изменения
    * Запросите проверку решения, заполнив форму
* После проверки преподаватель либо укажет найденные недостатки в `NOTES.md`, либо укажет их в виде комментариев в
  исходном коде, пометив их как `:NOTE:`