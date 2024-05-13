DROP INDEX ControlRelation IF EXISTS;
DROP INDEX AccessRelation IF EXISTS;
DROP INDEX OwnsRelation IF EXISTS;

DROP INDEX ENodes IF EXISTS;
DROP INDEX ONodes IF EXISTS;
DROP INDEX OANodes IF EXISTS;
DROP INDEX UANodes IF EXISTS;




DROP INDEX EEntityCLass IF EXISTS;

CREATE INDEX ENodes
FOR (e:E)
ON (e.id);

CREATE INDEX ONodes
FOR (o:O)
ON (o.id);

CREATE INDEX OANodes
FOR (oa:OA)
ON (oa.id);

CREATE INDEX UANodes
FOR (ua:UA)
ON (ua.id);


CREATE INDEX EEntityCLass FOR (e:E)
ON (e.entityClass);



CREATE INDEX ControlRelation
FOR ()-[r:relation]-()
ON (r.control);


CREATE  INDEX AccessRelation
FOR ()-[r:relation]-()
ON (r.access);


CREATE  INDEX OwnsRelation
FOR ()-[r:relation]-()
ON (r.owns);
