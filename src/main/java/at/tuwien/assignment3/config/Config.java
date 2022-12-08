package at.tuwien.assignment3.config;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public OntModel ontModel(){
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
    }
}
