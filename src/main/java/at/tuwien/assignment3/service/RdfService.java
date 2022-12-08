package at.tuwien.assignment3.service;

import at.tuwien.assignment3.config.TextConfig;
import at.tuwien.assignment3.utils.PersonType;
import at.tuwien.assignment3.utils.ReasonerType;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.java.Log;
import org.apache.jena.ontology.*;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.XSD;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
@CommonsLog
@Data
public class RdfService implements TextConfig {

    private OntModel model;
    private boolean rdfLoaded;

    public RdfService(OntModel model) {
        this.model = model;
    }

    /**
     * Basic Operations
     *
     */

    public String loadRdfFile(String inputFile) {
        try {
            RDFDataMgr.read(model, inputFile);
            this.rdfLoaded = true;
            return "Film  ontology loaded successfully";
        } catch (Exception e) {
            log.error(e);
            return e.getMessage();
        }

    }

    public void iteratingRdfData() {
        model.listStatements().forEachRemaining(System.out::println);
    }

    public void activateReasoners(ReasonerType type) {
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        if (type.equals(ReasonerType.RDFS))reasoner = ReasonerRegistry.getRDFSReasoner();
        else if (type.equals(ReasonerType.OWL)) {
            ReasonerRegistry.getOWLReasoner();
        }else reasoner = ReasonerRegistry.getTransitiveReasoner();
        model = (OntModel) ModelFactory.createInfModel(reasoner, model);
    }

    public void writeRdfFile(String outputFile, Lang format) {
        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            RDFDataMgr.write(fos, model, format);
            System.out.println("exported successfully into -> " + outputFile);
        }catch (Exception e){
            log.error(e);
            System.out.println(e.getMessage());
        }
    }
    /**
     * Ontology operations
     *
     */
    public void addClass(String URI, String label, String subClassURI){
        OntClass c = model.createClass(URI);
        c.addLabel(label, "en");
        if (subClassURI!=null){
            OntClass sub = model.getOntClass(subClassURI);
            c.addSubClass(sub);
        }

    }
    public void addOntProperty(String domain, String URI, String range, String label){
        OntProperty p = model.createOntProperty(URI);
        OntClass d = model.getOntClass(domain);
        OntClass r = model.getOntClass(range);
        p.addDomain(d);
        p.addRange(r);
        p.addLabel(label,"en");

    }
    public void addDataTypeProperty(String domain, String URI, String label, boolean date, boolean f){
        DatatypeProperty p = model.createDatatypeProperty(URI);
        OntClass d = model.getOntClass(domain);
        p.addDomain(d);
        p.addRange(date?XSD.date:f?XSD.xfloat:XSD.xlong);
        p.addLabel(label, "en");

    }
    public void addFilmStudio(String label, LocalDate date){
        try {
            String convention = label.toLowerCase().replace(" ", "_");
            Individual i = model.createIndividual(NS_EXAMPLE + convention, model.getOntClass(NS_FILM + "FilmStudio"));
            i.addLabel(label, "en");
            DatatypeProperty p = model.getDatatypeProperty(NS_FILM+"establishedDate");
            i.addLiteral(p, date.toString());
        }catch (Exception e){
            log.error(e);
            System.out.println(e.getMessage());
        }

    }
    public void addPerson(String fullName, LocalDate dateOfBirth, String gender, PersonType type){
        try {
            String convention = fullName.toLowerCase().replace(" ", "_");
            Individual i = model.createIndividual(NS_EXAMPLE + convention, FOAF.Person);
            DatatypeProperty name = model.getDatatypeProperty(NS_FILM + "fullName");
            DatatypeProperty dob = model.getDatatypeProperty(NS_FILM + "dateOfBirth");
            DatatypeProperty sex = model.getDatatypeProperty(NS_FILM + "gender");
            i.addLiteral(name, fullName);
            if (dateOfBirth != null) {
                i.addLiteral(dob, dateOfBirth.toString());
            }
            if (gender != null) {
                i.addLiteral(sex, gender);
            }
            OntClass c = null;
            if (type.equals(PersonType.ACTOR)) c = model.getOntClass(ACTOR);
            if (type.equals(PersonType.DIRECTOR))c = model.getOntClass(DIRECTOR);
            if (type.equals(PersonType.SCRIPT_WRITER))c = model.getOntClass(NS_FILM + "ScriptWriter");
            if (type.equals(PersonType.COMPOSER))c = model.getOntClass(NS_FILM + "Composer");
            i.setOntClass(c);
        }catch (Exception e){
            log.error(e);
            System.out.println(e.getMessage());
        }

    }

    public void addFilm(String label, int releaseYear, float budget){
        String convention = label.toLowerCase().replace(" ", "_");
        OntClass film = model.getOntClass(NS_FILM+"Film");
        Individual i = model.createIndividual(NS_EXAMPLE + convention, film);
        i.setLabel(label, "en");
        if (releaseYear>1){
            DatatypeProperty year = model.getDatatypeProperty(NS_FILM+"releaseYear");
            i.addLiteral(year,releaseYear);
        }if (budget>1){
            DatatypeProperty bud = model.getDatatypeProperty(NS_FILM+"budget");
            i.addLiteral(bud,budget);
        }
    }



    /**
     * Basic query operations
     *
     */

    public void selectQuery(String queryFile) {
        try {
            String queryString = readFile(queryFile, StandardCharsets.UTF_8);
            QueryExecution execution = QueryExecutionFactory.create(queryString, model);
            ResultSet resultSet = execution.execSelect();
            ResultSetFormatter.out(resultSet);

        } catch (IOException e) {
            log.error(e);
            System.out.println(e.getMessage());
        }
    }
    public void constructQuery(String queryFile, String name) {
        try {
            String queryString = readFile(queryFile, StandardCharsets.UTF_8);
            QueryExecution execution = QueryExecutionFactory.create(queryString, model);
            Model model = execution.execConstruct();
            RDFDataMgr.write(System.out, model, Lang.TURTLE);

            FileOutputStream fos = new FileOutputStream(OUTPUT+name + ".ttl");
            RDFDataMgr.write(fos, model, Lang.TURTLE);


        } catch (IOException e) {
            log.error(e);
            System.out.println(e.getMessage());
        }
    }
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
