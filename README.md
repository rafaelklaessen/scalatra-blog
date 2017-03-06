# Scalatra Blog

A weekend project to practice Scala & Scalatra.
It's basically a blog app, but all backend communication goes through AJAX (there is no frontend).
Read the [wiki](https://github.com/rafaelklaessen/scalatra-blog/wiki) for more information.

## Functionality

- User CRUD + login
- Posts CRUD
- Category creating, reading and deleting. Posts can be added and removed from categories

## Build & Run

Scalatra-blog requires Scalatra v2.5.0

```sh
$ cd Scalatra_Blog
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
