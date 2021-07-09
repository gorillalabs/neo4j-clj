# Neo4j survival guide

The easiest way to get started with Neo4j is by running it in a docker container

```sh
docker run \
    --publish=7474:7474 \
    --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    neo4j:latest
```

__You have to login once and change the password! Default is neo4j/neo4j__

A complete guide for all kinds of scenarios can be found in the 
[docs](http://neo4j.com/docs/operations-manual/current/installation/docker/).

After starting a new Neo4j instance, you have to visit 
[localhost:7474](http://localhost:7474) to set a new password.

The Neo4j instance can be accessed under [localhost:7474](http://localhost:7474). The
web interface is great and provides a shell, example queries, overviews, settings etc.


## Trouble-shooting

One thing that might occur often: If you don't provide a SSL cert, Neo4j creates it's
own every restart. This leads to problems if you restart the server often (you'll get
a Java exception). You can force the Neo4j driver to forget the old container and 
accept the new one by

```sh
rm ~/.neo4j/known_hosts
```
