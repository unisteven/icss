
**Gemaakt door:** Steven Krol

**Student nummer:** 597272

**Datum:** 22-10-2019


Deze opdracht handelt de volgende onderdelen volledig af:

Dit zijn dus alle levels van de parser:

- PA01
- PA02
- PA03
- PS04

Dit zijn alle mogelijke checks op de abstract syntax tree:

- CH01
- CH02
- CH03
- CH04
- CH05

Alle transformaties met de Ifclause en de expressies worden goed afgehandelt.
De expressies hebben ook  ondersteuning voor variable referenties en berekende waardes in variablen.
Rekenregels worden ook goed afgehandelt:


- TR01
- TR02

De stappen voor genereren worden ook goed afgehandelt door gebruik te maken van een  `toCss()` methode in de ASTNode classes.
Dit zorgt ervoor dat elke node opzichzelf verantwoordlijk is voor het afdrukken van zijn juiste css representatie.
De parent is dan verantwoordelijk dat de indentation goed is voor de children:

- GE01
- GE02

Er zijn geen afgesproken extra uitbreidingen echter zijn er wel een aantal dingen toegevoegd voor het optimaliseren van de ast.
dit zijn:

- Variabelen kunnen gedeclareed worden in een `scope` maar er wordt niet rekening gehouden of een variable buiten een scope gebruikt kan worden
- Bij een ifclause worden de waardes van de body op de juiste plaats geinjecteerd zodat de volgordelijkheid blijft kloppen volgens het `Css` princiepe.
