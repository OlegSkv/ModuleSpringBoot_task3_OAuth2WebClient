Quick start:
1. Before running the app, start KeyCloak provider:
 go to folder \keycloak-21.0.2\bin -> kc.bat start-dev

I start with configuration file \keycloak-21.0.2\conf\keycloak.conf with:
```
hostname=localhost
http-enabled=true
http-port=8442

# we are not using https, but without these lines below KeyCloak doesn't start
https-certificate-file=<path to file .pem>
https-certificate-key-file=<path to file .pem>`
```

2. Start REST service (see resource-uri property in application.yml)
   https://github.com/OlegSkv/ModuleSpringBoot-tasks
3. Start the app