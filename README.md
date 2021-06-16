# Sigil - Clojure Version
## sigil-clj

Sigil was a customer recommendation and interaction forum for organizations.
Users could vote on suggestions for implementation by businesses, organizations,
or governments.

Two versions were implemented: First, a C# ASP.NET version, which quickly manifested
typical OOP shared-state and architectural issues which would make it difficult to
manage for a small team. We then switched to Clojure, which yielded a much simpler
product in half the code, but with the same performance and end result at our scale.

Sigil was abandoned due to the idea being a bit thin, but the software engineering
lessons live on in the minds of the authors.

By Chris Collazo and Dominic Cox
