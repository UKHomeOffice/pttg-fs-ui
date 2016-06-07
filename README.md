Financial Status Service UI
=

Overview
-

This is an overview of the financial status service UI.
The financial status service UI is a UI for the financial status service.
That was an overview of the financial status service UI.


Suggestions
-

Added a demo of the Spring Boot actuator support and healthchecks

see eg

http://localhost:8001/info

> Gives application version and git version details. 
> Perhaps useful when we have a deployment pipeline
  
  
http://localhost:8001/health

> Reports healthcheck results.
> Hit refresh a few times to see different results.


http://localhost:8001/mappings

> Shows all known path mappings 

http://localhost:8001/metrics

> Metrics including hit counts

http://localhost:8001/trace

> Request log traces
