Goal
====

Partially automated creation of client code for a given JAX-RS interface.

Clients can be:
- other services
- Android devices

Due to Android UI handling network tasks need to be performed on a background
thread, which makes Retrofit (https://square.github.io/retrofit/) a good base
for creating client code (see Retrofit Callback API).


Features
========

- Reads .java files, looking for JAX-RS annotations and converts those to their
  Retrofit counterpart where feasable
- Supply two client methods for each server method, one using Callbacks (for
  Android clients) and one without (for 'regular' clients)
- Resolve imports for models that are being used for communcation, without
  actually copying model classes (models should be added as dependecy to client
  code).
