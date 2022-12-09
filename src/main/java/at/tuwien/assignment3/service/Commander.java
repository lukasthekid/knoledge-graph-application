package at.tuwien.assignment3.service;

import at.tuwien.assignment3.config.TextConfig;
import at.tuwien.assignment3.utils.PersonType;
import at.tuwien.assignment3.utils.ReasonerType;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.jena.riot.Lang;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.Quit;

import java.time.LocalDate;
import java.util.Objects;

@ShellComponent
@CommonsLog
public class Commander implements TextConfig, Quit.Command {
    private final RdfService rdfService;

    public Commander(RdfService rdfService) {
        this.rdfService = rdfService;
    }

    @ShellMethod("load film ontology")
    public String loadOntology() {
        return rdfService.loadRdfFile(INPUT_FILE);
    }

    @ShellMethod("print ontology in terminal")
    public void print(@ShellOption(value = "--inf")boolean inf){
        rdfService.print(inf);
    }
    @ShellMethod("activate a reasoner")
    public void activateReasoners(String reasoner){
        try{
            ReasonerType r = ReasonerType.valueOf(reasoner.toUpperCase());
            rdfService.activateReasoners(r);
        }catch (Exception e){
            log.error(e);
        }
    }

    @ShellMethod(key = "export", value = "export graph")
    public void exportGraph(@ShellOption(value = "--f", defaultValue = "turtle") String format,
                            @ShellOption(value = "--p", defaultValue = "./output/output.ttl") String path,
                            @ShellOption(value = "--inf")boolean inf){
        format = format.toUpperCase();
        Lang f = Lang.TURTLE;
        if (format.equals("N3")) f = Lang.N3;
        if (format.equals("NTRIPLES")) f = Lang.NTRIPLES;
        if (format.equals("JSONLD")) f = Lang.JSONLD;
        if (format.equals("RDFJSON")) f = Lang.RDFJSON;
        if (format.equals("CSV")) f = Lang.CSV;

        rdfService.writeRdfFile(path, f, inf);
    }

    /**
     * Operations
     */

    @ShellMethod("add class")
    public void addClass(@ShellOption(value = "-n") String name,
                         @ShellOption(value = "-l") String label,
                         @ShellOption(value = "--s",defaultValue = "n") String subcName){
        String uri = NS_FILM+name;
        String subUri = !Objects.equals(subcName, "n") ?NS_FILM+subcName:null;
        try {
            rdfService.addClass(uri, label, subUri);
            System.out.println("successfully added a class");
        }catch (Exception e){
            log.error(e);
        }
    }

    @ShellMethod("add property")
    public void addProperty(@ShellOption(value = "--data")boolean data,
                            String domain,
                            @ShellOption(value = "--range",defaultValue = "n")String range,
                            @ShellOption(value = "--l")String label,
                            @ShellOption(value = "--d")boolean d,
                            @ShellOption(value = "--f")boolean f){
        try {
            domain = NS_FILM + domain;
            range = NS_FILM + range;
            String uri = NS_FILM + label.replace(" ", "");
            if (data) {
                rdfService.addDataTypeProperty(domain, uri, label, d, f);
                System.out.println("DataTypeProperty was successfully created");
            } else {
                rdfService.addOntProperty(domain, uri, range, label);
                System.out.println("OntProperty was successfully created");
            }
        }catch (Exception e){
            log.error(e);
        }

    }

    @ShellMethod("add person")
    public void addPerson(@ShellOption(value = "--n")String fullName,
                          @ShellOption(value = "--d",defaultValue = "n") String dateOfBirth,
                          @ShellOption(value = "--g",defaultValue = "n") String gender,
                          @ShellOption(value = "--t") String type){
        try{
            PersonType t = PersonType.valueOf(type.toUpperCase());
            LocalDate date = null;
            if (!Objects.equals(dateOfBirth, "n")) date = LocalDate.parse(dateOfBirth);
            rdfService.addPerson(fullName, date, !Objects.equals(gender, "n") ?gender:null, t);
            System.out.println("successfully added a person of type: " + type);
        }catch (Exception e){
            log.error(e);
        }

    }

    @ShellMethod("add film studio")
    public void addFilmStudio(@ShellOption(value = "--l")String label,
                              @ShellOption(value = "--d",defaultValue = "n") String date){
        try{
            LocalDate localDate = null;
            if (!Objects.equals(date, "n")) localDate = LocalDate.parse(date);

            rdfService.addFilmStudio(label,localDate);
            System.out.println("successfully added a film studio");
        }catch (Exception e){
            log.error(e);
        }

    }

    @ShellMethod("add film")
    public void addFilm(@ShellOption(value = "--l")String label,
                        @ShellOption(value = "--year",defaultValue = "0")int releaseYear,
                        @ShellOption(value = "--budget",defaultValue = "0.0")float budget){
        try {
            rdfService.addFilm(label, releaseYear, budget);
            System.out.println("successfully added a film");
        }catch (Exception e){
            log.error(e);
        }
    }

    /**
     * Queries
     */


    @ShellMethod("list all queries")
    public String showQueries(){
        StringBuilder sb = new StringBuilder();
        sb.append("Please select a query you want to run. Type in the number and press enter:");
        sb.append("\n SELECT QUERIES");
        sb.append("\n 1. List all Films");
        sb.append("\n 2. List all FilmStudios established after 1960");
        sb.append("\n 3. List all Films with their Writers -> born between 1950 and 1970");
        sb.append("\n CONSTRUCT QUERIES");
        sb.append("\n 4. Construct Films with titles");
        sb.append("\n 5. Construct Writers with all Studios they worked for");
        sb.append("\n 6. Construct Soundtracks that where used for a specific Genre");
        return sb.toString();

        //rdfService.addPerson("Lukas Burtscher", LocalDate.now(), "male", PersonType.DIRECTOR);
    }
    @ShellMethod("run select query")
    public void runQuery( int q){
        try {
            if (q < 1 || q > 6) System.out.println("No valid query selected");
            if (q <= 3) {
                rdfService.selectQuery(QUERIES[q - 1]);
            } else {
                rdfService.constructQuery(QUERIES[q - 1], "construct-query-result" + q);
            }
        }catch (Exception e){
            log.error(e);
        }

    }

    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit", "terminate"})
    public void exit(){
        System.out.println("Exiting the Application");
        System.out.println("Good Bye!!!!!!!!");
        System.exit(0);
        //throw new ExitRequest();
    }

    @ShellMethodAvailability({"print","run-query","show-queries","add-film","add-person","add-film-studio","add-property","add-class"})
    public Availability printAvailability(){
        return rdfService.isRdfLoaded()?Availability.available():
                Availability.unavailable("The base RDF File must be loaded before running other commands");
    }


}
