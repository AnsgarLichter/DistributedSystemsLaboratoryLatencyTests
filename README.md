# Latency test for Distributed Systems Labooratory

Execute the following command to start the simulation:

```bash
mvn gatling:test -Dgatling.simulationClass=webshop.WebshopSimulation
```

If the command doesn't work, you can try the following version:

```bash
mvn gatling:test -D"gatling.simulationClass"="webshop.WebshopSimulation"
```
