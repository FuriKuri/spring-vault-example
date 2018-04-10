# spring-vault-example


## Usage

Start the mysql and vault server.
```
$ docker-compose up
  ...
  vault_1  | Root Token: f6b9d03d-d7bf-ed6f-71e1-690b9d2796e8
  ...
  vault_1  | ==> Vault server started! Log data will stream in below:
  ...
  mysql_1  | Version: '5.7.21'  socket: '/var/run/mysqld/mysqld.sock'  port: 3306  MySQL Community Server (GPL)
```

After vault was started, export the token, which is printed in the log from the vault container into the environment variable `VAULT_TOKEN`.
Also set the `VAULT_ADDR` to http://127.0.0.1:8200. Otherwise the vault client expect a https connection.
Then use the vault client to configure the mysql readonly role, we will use in the sprint boot app.
```
$ export VAULT_TOKEN=f6b9d03d-d7bf-ed6f-71e1-690b9d2796e8

$ export VAULT_ADDR=http://127.0.0.1:8200

$ vault secrets enable mysql

$ vault write mysql/config/connection connection_url="root:changeme@tcp(mysql:3306)/"

$ vault write mysql/roles/readonly sql="CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}';GRANT SELECT ON *.* TO '{{name}}'@'%';"

```

You can verify, if the role and the created users by vault are working.
```
$ vault read mysql/creds/readonly
  Key                Value
  ---                -----
  lease_id           mysql/creds/readonly/5de9e11f-04d5-9da1-c6d7-6d70392ad13d
  lease_duration     768h
  lease_renewable    true
  password           ec033f5d-653b-07f3-0490-60ef00de2ed7
  username           read-root-2dbfea

$ docker exec -it springvaultexample_mysql_1 mysql -uread-root-2dbfea -pec033f5d-653b-07f3-0490-60ef00de2ed7
  mysql: [Warning] Using a password on the command line interface can be insecure.
  Welcome to the MySQL monitor.  Commands end with ; or \g.
  Your MySQL connection id is 5
  Server version: 5.7.21 MySQL Community Server (GPL)
```

Create a vault token for the spring boot app, export the value and start the spring boot app.
```
$ vault token create
  Key                Value
  ---                -----
  token              14be42db-9ce3-98b7-ebc4-1236db2b8f28
  token_accessor     5777c9c1-df55-7202-c230-6887034c2f99
  token_duration     ∞
  token_renewable    false
  token_policies     [root]

$ export VAULT_APP_TOKEN=14be42db-9ce3-98b7-ebc4-1236db2b8f28

$ docker-compose -f docker-compose-app.yaml up
  ...
  app_1  | Current connection user: read-toke-362ed4@%
  ...
  app_1  | 2018-04-07 13:55:07.665  INFO 1 --- [           main] net.furikuri.vault.Application           : Started Application in 8.697 seconds (JVM running for 9.639)
```