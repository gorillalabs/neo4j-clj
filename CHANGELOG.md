# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).


## [Unreleased]
[Commit Log](https://github.com/gorillalabs/neo4j-clj/compare/v0.5.0...HEAD)

### Changed
[Moved dependency to neo4j-harness](https://github.com/gorillalabs/neo4j-clj/issues/5). If you relied on neo4j-clj to bring this dependency so far, you need to add it to your project yourself. It should be a dev dependency only. 


## [0.5.0] - 2018-01-08
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



