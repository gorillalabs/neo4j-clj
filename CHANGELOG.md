# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).


## [Unreleased]
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v4.0.1...HEAD)

## [4.1.0] - 2020-09-18
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v4.0.0...v4.0.1)
- Fix documentation and example project (#20, #22)
- Clean dependencies (#19, #23)

## [4.0.1] - 2020-02-08
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v4.0.0...v4.0.1)
- Drop support for OpenJDK < 11

## [4.0.0] - 2020-02-08
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v2.0.1...v4.0.0)
- Update to Neo4j 4.0.0

## [2.0.1] - 2019-01-28
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v2.0.0...v2.0.1)
- Added support for Boolean values (#15)

## [2.0.0] - 2019-01-24
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v1.2.0...v2.0.0)

### Changed
- Updated to Clojure 1.10.0, Neo4j libraries 3.5.2 (#14)

## [1.2.0] - 2019-01-24
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v1.1.0...v1.2.0)

### Added
- Support for IntegerValues (#13)

### Changed
- Documentation (#12)

## [1.1.0] - 2018-05-22
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v1.0.0...v1.1.0)

### Added
- Support for driver configuration (logging)

### Fixed
- Tests running on Java 9 upwards.

## [1.0.0] - 2018-04-11
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v0.5.0...v1.0.0)

### Changed
[Moved dependency to neo4j-harness](https://github.com/gorillalabs/neo4j-clj/issues/5). If you relied on neo4j-clj to bring this dependency so far, you need to add it to your project yourself. It should be a dev dependency only. 


## [0.5.0] - 2018-01-18
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v0.4.2...v0.5.0)

### Changed
- Updated to new dependency versions
- Fixed security flaw: Do not show password in printed representation.


## [0.4.2] - 2017-11-08
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v0.4.1...v0.4.2)

### Changed
- Fixed conversion of InternalRelationshipType, aka properties on relationships in a collection.


### Changed

## [0.4.1] - 2017-11-01
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v0.4.0...v0.4.1)

### Added
- Support for RelationshipType, aka properties on relationships.

## [0.4.0] - 2017-10-23
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v0.3.3...v0.4.0)

### Added
- Joplin support

## [0.3.3] - 2017-09-05
### Added
- with-retry macro



