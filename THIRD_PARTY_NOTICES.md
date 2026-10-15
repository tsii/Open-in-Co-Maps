# Third-party notices

The original application code is licensed under BSD-3-Clause (see LICENSE).
Third-party components retain their original licenses.

## CoMaps coordinate decoder — Apache-2.0

`CoMapsShareParser.kt` and the upstream coordinate test vectors in
`CoMapsShareParserTest.kt` are adapted from CoMaps' ge0 parser and tests:

- https://codeberg.org/comaps/comaps/src/commit/e82373286a76451d267c0f19f887c5c213bfe0a4/libs/ge0/parser.cpp
- https://codeberg.org/comaps/comaps/src/commit/e82373286a76451d267c0f19f887c5c213bfe0a4/libs/ge0/ge0_tests/parser_tests.cpp

Modifications: Kotlin port restricted to coordinate extraction, validation of
supported link prefixes, and Android share-text / geo: support.

Copyright 2020 My.com B.V. (Mail.Ru Group)
Copyright 2025 Organic Maps Contributors
Copyright 2026 CoMaps Contributors

Licensed under the Apache License, Version 2.0. See licenses/Apache-2.0.txt
and NOTICE. This exception is not relicensed as BSD.

## Gradle wrapper — Apache-2.0

The Gradle wrapper scripts and JAR are distributed by the Gradle project under
Apache-2.0. Their existing copyright headers and JAR notices are retained.

CoMaps and Google Maps are trademarks of their respective owners. This app is
an independent integration and is not endorsed by either project.
