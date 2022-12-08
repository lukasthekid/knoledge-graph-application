package at.tuwien.assignment3.config;

public interface TextConfig {
    String INPUT_FILE = "./input/film.ttl";
    String OUTPUT = "./output/";
    String URI = "http://semantics.id/ns/example/";
    String NS_FILM = "http://semantics.id/ns/example/film#";
    String NS_EXAMPLE = "http://semantics.id/ns/example#";

    String FILM_STUDIO = NS_FILM+"FilmStudio";
    String ACTOR = NS_FILM+"Actor";
    String DIRECTOR = NS_FILM+"Director";

    String LIST_ALL_FILMS ="./input/query/SELECT_Q1.sparql";
    String LIST_ALL_FILM_STUDIOS_AFTER_1960 ="./input/query/SELECT_Q2.sparql";
    String LIST_ALL_FILMS_WITH_WRITER_BETWEEN_1950_AND_1970 ="./input/query/SELECT_Q3.sparql";
    String CONSTRUCT_FILM_WITH_TITLES ="./input/query/CONSTRUCT_Q1.sparql";
    String CONSTRUCT_WRITERS_WITH_THEIR_STUDIOS ="./input/query/CONSTRUCT_Q2.sparql";
    String CONSTRUCT_SOUNDS_FOR_GENRE ="./input/query/CONSTRUCT_Q3.sparql";

    String[] QUERIES = {LIST_ALL_FILMS,LIST_ALL_FILM_STUDIOS_AFTER_1960,LIST_ALL_FILMS_WITH_WRITER_BETWEEN_1950_AND_1970,
            CONSTRUCT_FILM_WITH_TITLES,CONSTRUCT_WRITERS_WITH_THEIR_STUDIOS,CONSTRUCT_SOUNDS_FOR_GENRE};
}
