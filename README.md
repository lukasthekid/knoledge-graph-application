# Assignment 3 - Documentation
## Knowledge Graph Application in Apache Jena

This Application works with RDF Ontology data and SPARQL Queries. The Project is managed by Maven and supports Java language-level 11. 
[Apache Jena](https://jena.apache.org/) is a light Java Framework for using RDF API and SPARQL. Jena is included in a simple Spring Boot Project here.

### Implemented features
1. Load the initial Ontology (Film.ttl) with all their instances. Loaded as OntModel.

    `shell:> load-ontology`
2. Activate Reasoners provided by the API. Client can select betwen **RDFS, OWL, TRANSITIVE** Reasoners. Loads model as InfModel again.
    
    `shell:> activate-reasoners --reasoner owl`
3. Print all Statements found in the Ontology. Activate **--inf** if you want to print InfModel with Reasoners.

    `shell:> print` or `shell:> print --inf`
4. Export the Ontology Graph to a selected format (default TURTLE) and a selected output path (default ./output/output.ttl). If user wants to export the InfModel instead of the OntModel, the client should hit **--inf** to the command line. Formats available: *TURTLE, N3, NTRIPLES, JSONLD, RDFJSON, CSV*

    `shell:> export` or `shell:> export --f n3 --p ./output/output.n3 --inf`
5. Add Class to Ontology with *name, label and optional subClass*. SubClass must exist before if you use that parameter.

   `shell:> add-class -n Demo -l democlass` or `shell:> add-class -n Demo -l democlass --s Film`
6. Add Property to the Ontology. User can decide to either add a new DataProperty or to add Object/OntProperty to the graph. If a DataTypeProperty should be initialized the client should hit *--data* to the command. DataTypeProperties need a domain class (--domain), a label (--l) and a DataType (default is long, *--d* for type date and *--f* for type float). OntProperties need domain, label and a range Ressource (--range).

    `shell:> add-property --data --domain Film --l generatedRevenue --f` or `shell:> add-property --domain Film --range Actor --l hasActor`
7. Add Person Instance. Client can decide to add *SCRIPT_WRITER, DIRECTOR, ACTOR or COMPOSER* to the examples by using the *--t* parameter. The name of a person must be declared (--n), dateOfBirth (--d), gender (--g) are optional.

    `shell:> add-person --n Peter --t director` or `shell:> add-person --n Peter --d 1980-09-03 --g male --t composer`
8. Add FilmStudio Instance. Client must declare the label (--l), *label without spaces will be used for URI*. EstablishedDate is optional (--d).

    `shell:> add-film-studio --l Netflix` or `shell:> add-film-studio --l Netflix --d 2009-07-06`
9. Add Film Instance. Label must be declared (same rules as FilmStudio), release year and budget is optional.

    `shell:> add-film --l Venum` or `shell:> add-film --l Venum --year 2010 --budget 623982.54`
10. Show all available SPARQL Queries. User can see all available queries, select one and run the query.

    `shell:> show-queries` Client then select a query by listed number and runs (**run-query** number of the query)
11. Execute selected SPARQL query. Result will be printed out to the terminal, but if client runs a **CONSTRUCT** query, the result will also be exported to the *./output/* directory.

    `shell:> run-query 1` or `shell:> run-query --q 1`
12. Exit the Shell.
    `shell:> quit` or `shell:> exit` or `shell:> terminate`

None of the queries can be executed if the initial **load-ontology** command wasn't executed


### Software Architecture
for the communication [Spring Shell](https://docs.spring.io/spring-shell/docs/2.1.4/site/reference/htmlsingle/) was used. The Shell component is under *./source/main/java/../service/Commander.java*. The Commander takes the terminal commands from the user and transfers the task to the RdfService class. This one can be found under *./source/main/java/../service/RdfService.java*. Both of them use TextData for storing all the input/output and schematic information. This TextConfig can be found under *./source/main/java/../config/TextConfig.java*.
    
